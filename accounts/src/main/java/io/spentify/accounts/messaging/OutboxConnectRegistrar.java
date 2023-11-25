package io.spentify.accounts.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Component responsible for registering the Kafka Connect Outbox Connector upon application startup.
 * The connector registration involves creating the required Kafka topic and submitting the connector configuration.
 */
@Component
@RequiredArgsConstructor
@Slf4j
// FIXME: review the connector registration
class OutboxConnectRegistrar {

    @Value("${kafka.bootstrap-servers}")
    private List<String> boostrapServers = new ArrayList<>();

    @Value("${kafka.connector.outbox.config.file}")
    private String connectorJsonPath;

    @Value("${kafka.connect-server-uri}")
    private String connectServerUri;

    private final KafkaTopicProperties outbox;
    private final RestTemplate kafkaConnectRestClient;

    @EventListener(ApplicationReadyEvent.class)
    public void on() {
        createOutboxTopic();
        createOutboxConnector();
    }

    private void createOutboxTopic() {
        var props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);

        try (var admin = Admin.create(props)) {
            var topics = admin.listTopics();
            var names = topics.names().get();
            if (!names.contains(outbox.getName())) {
                var outboxTopic = new NewTopic(outbox.getName(), outbox.getPartitions(), outbox.getReplicas())
                        .configs(outbox.getConfigs());
                var createTopicsResult = admin.createTopics(List.of(outboxTopic));
                createTopicsResult.all().get(); // ensure executed successfully
            }
        } catch (Exception e) {
            log.error(String.format("Failed to create '%s' topic", outbox.getName()), e);
            throw new IllegalStateException(e);
        }
    }

    private void createOutboxConnector() {
        try {
            var connectorJSON = new String(Files.readAllBytes(connectorJSONPath()));
            kafkaConnectRestClient.postForEntity(connectServerUri, connectorJSON, String.class);
        } catch (HttpClientErrorException.Conflict e) {
            log.trace("Connect component already exists, %s".formatted(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to registered connect component", e);
            throw new IllegalStateException(e);
        }
    }

    private Path connectorJSONPath() throws IOException {
        if (StringUtils.isBlank(connectorJsonPath)) {
            log.warn("Used default: accounts-outbox-connector.json");
            return new ClassPathResource("connector/accounts-outbox-connector.json").getFile().toPath();
        }

        return Paths.get(connectorJsonPath);
    }
}
