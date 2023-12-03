package io.spentify.expenses.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Configuration
@EnableKafka
class KafkaConfig {

    @Bean
    RecordMessageConverter converter() {
        return new JsonMessageConverter();
    }
}
