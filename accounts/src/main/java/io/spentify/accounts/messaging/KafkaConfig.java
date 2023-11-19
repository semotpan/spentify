package io.spentify.accounts.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
class KafkaConfig {

    @Bean
    @ConfigurationProperties(prefix = "kafka.topic.outbox.events")
    KafkaTopicProperties outboxEventsTopicProperties() {
        return new KafkaTopicProperties();
    }

    @Bean
    RestTemplate kafkaConnectRestClient() {
        return new RestTemplateBuilder()
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();
    }
}
