package io.spentify.expenses;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
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

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@DirtiesContext
@Testcontainers
@TestPropertySource(locations = "/application-test.properties")
public abstract class TestContainersSetup {

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

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        Startables.deepStart(Stream.of(postgresSQL, kafka))
                .join();

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.datasource.url", postgresSQL::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSQL::getUsername);
        registry.add("spring.datasource.password", postgresSQL::getUsername);
    }

    protected <T> KafkaProducer<UUID, T> kafkaProducer() {
        return new KafkaProducer<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()
        ), new UUIDSerializer(), new JsonSerializer<>());
    }
}
