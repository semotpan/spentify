package io.spentify.expenses.messaging;

import io.spentify.expenses.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Tag("integration")
@SpringBootTest(classes = TestExpensesApplication.class)
class AccountsOutboxEventsConsumerTest extends TestContainersSetup {

    @Test
    @DisplayName("should create default categories on account created event")
    void onAccountCreated(@Autowired Categories categories) {
        // given: an account created event
        var event = accountCreatedEvent();

        // when: account create event submitted
        try (KafkaProducer<UUID, AccountCreated> producer = kafkaProducer()) {
            var record = new ProducerRecord<>("account.outbox.events", randomUUID(), event);
            record.headers()
                    .add(new RecordHeader("id", randomUUID().toString().getBytes(UTF_8)))
                    .add(new RecordHeader("eventType", "AccountCreated".getBytes(UTF_8)));

            producer.send(record);
            producer.flush();
        }

        // then: database contains default categories
        var actualCategories = new HashSet<Category>();
        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
            actualCategories.addAll(categories.findAll());
            return actualCategories.size() == DefaultCategories.values().length;
        });

        // and: expected  default categories for provided account are present
        var expectedCategories = DefaultCategories.asList().stream()
                .map(c -> tuple(c, new AccountIdentifier(event.accountId())))
                .collect(Collectors.toList());

        assertThat(actualCategories)
                .extracting(Category::getName, Category::getAccount)
                .containsExactlyInAnyOrderElementsOf(expectedCategories);
    }

    private AccountCreated accountCreatedEvent() {
        return AccountCreated.builder()
                .accountId(randomUUID())
                .firstName("Jon")
                .lastName("Snow")
                .emailAddress("jonsnow@email.com")
                .build();
    }
}
