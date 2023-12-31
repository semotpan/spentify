package io.spentify.accounts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.testing.testcontainers.DebeziumContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@DirtiesContext
@Testcontainers
@TestPropertySource(locations = "/application-test.properties")
@Slf4j
public abstract class TestContainersSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestContainersSetup.class);

    private static final Network network = Network.newNetwork();

    @Container
    protected static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.1"))
            .withNetwork(network)
            .withNetworkAliases("kafka");

    @Container
    protected static PostgreSQLContainer<?> postgresSQL = new PostgreSQLContainer<>(DockerImageName.parse("debezium/postgres:16")
            .asCompatibleSubstituteFor("postgres"))
            .withNetwork(network)
            .withNetworkAliases("postgres");

    @Container
    protected static DebeziumContainer connector = new DebeziumContainer("debezium/connect:2.4.1.Final")
//            .withCopyFileToContainer(MountableFile.forClasspathResource("connector/postgres.properties"), "/etc/secrets/postgres.properties")
//            .withEnv("CONNECT_CONFIG_PROVIDERS", "file")
//            .withEnv("CONNECT_CONFIG_PROVIDERS_FILE_CLASS", "org.apache.kafka.common.config.provider.FileConfigProvider")
//            .withLogConsumer(new Slf4jLogConsumer(LOGGER))
            .withNetwork(network)
            .withKafka(kafka)
            .dependsOn(kafka, postgresSQL);

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        Startables.deepStart(Stream.of(postgresSQL, kafka, connector))
                .join();

        registry.add("spring.datasource.url", postgresSQL::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSQL::getUsername);
        registry.add("spring.datasource.password", postgresSQL::getUsername);
    }

    protected KafkaConsumer<String, String> kafkaConsumer() {
        return new KafkaConsumer<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        ), new StringDeserializer(), new StringDeserializer());
    }

    protected List<ConsumerRecord<String, String>> drain(KafkaConsumer<String, String> consumer, int expectedRecordCount) {
        var allRecords = new ArrayList<ConsumerRecord<String, String>>();

        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
            consumer.poll(Duration.ofMillis(50))
                    .iterator()
                    .forEachRemaining(allRecords::add);

            return allRecords.size() == expectedRecordCount;
        });

        return allRecords;
    }

    protected void setUpAccountDebeziumConnector(TestRestTemplate restTemplate, String topic) throws IOException {
        createOutboxTopic(topic);

        var objectMapper = new ObjectMapper();
        var payload = objectMapper
                .readValue(new ClassPathResource("connector/test-outbox-connector.json").getFile(), JsonNode.class);

        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        restTemplate.exchange(
                "%s/test-outbox-connector/config".formatted(connector.getConnectorsUri()),
                HttpMethod.PUT,
                new HttpEntity<>(objectMapper.writeValueAsString(payload), headers),
                String.class
        );
    }

    private void createOutboxTopic(String topic) {
        var props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());

        try (var admin = Admin.create(props)) {
            var topics = admin.listTopics();
            var names = topics.names().get();
            if (!names.contains(topic)) {
                var outboxTopic = new NewTopic(topic, 1, (short) 1);
                var createTopicsResult = admin.createTopics(List.of(outboxTopic));
                createTopicsResult.all().get(); // ensure executed successfully
            }
        } catch (Exception e) {
            log.error(String.format("Failed to create '%s' topic", topic), e);
            throw new IllegalStateException(e);
        }
    }
}
