package io.spentify.expenses;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class TestExpensesApplication {

    public static void main(String[] args) {
        SpringApplication.from(ExpensesApplication::main).with(TestExpensesApplication.class).run(args);
    }

    @Bean
    NewTopic accountsOutboxEvents(@Value("${kafka.topic.accounts.outbox.events.name}") String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }
}
