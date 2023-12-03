package io.spentify.expenses.messaging;

import io.spentify.expenses.*;
import org.apache.kafka.clients.producer.ProducerRecord;
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

import static io.spentify.expenses.messaging.AccountsOutboxEventsListener.AccountCreated;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Tag("integration")
@SpringBootTest(classes = TestExpensesApplication.class)
class AccountsOutboxEventsListenerTest extends TestContainersSetup {

    @Test
    @DisplayName("Should create default categories on account created event")
    void onAccountCreated(@Autowired Categories categories) {
        // given: an account created event
        var accountCreated = accountCreatedEvent();

        // when: account create event submitted
        try (var producer = kafkaProducer()) {
            var record = new ProducerRecord<UUID, Object>("accounts.outbox.events", randomUUID(), accountCreated);
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
                .map(c -> tuple(c, new AccountIdentifier(accountCreated.accountId())))
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
