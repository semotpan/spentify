package io.spentify.accounts;

import io.debezium.testing.testcontainers.DebeziumContainer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@DirtiesContext
@Testcontainers
@TestPropertySource(locations = "/application-test.properties")
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
            .withCopyFileToContainer(MountableFile.forClasspathResource("docker/postgres.properties"), "/etc/secrets/postgres.properties")
            .withEnv("CONNECT_CONFIG_PROVIDERS", "file")
            .withEnv("CONNECT_CONFIG_PROVIDERS_FILE_CLASS", "org.apache.kafka.common.config.provider.FileConfigProvider")
            .withNetwork(network)
            .withKafka(kafka)
            .withLogConsumer(new Slf4jLogConsumer(LOGGER))
            .dependsOn(kafka, postgresSQL);

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        Startables.deepStart(Stream.of(postgresSQL, kafka, connector))
                .join();

        registry.add("kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("kafka.connect-server-uri", connector::getConnectorsUri);
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
}
