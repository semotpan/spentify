package io.spentify.expenses.messaging;

import io.spentify.expenses.CategoryService;
import io.spentify.expenses.DefaultCategories;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class AccountsOutboxEventsListener {

    private final CategoryService categoryService;

    @KafkaListener(
            id = "${spring.kafka.client-id}",
            topics = "${kafka.topic.accounts.outbox.events.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    void on(AccountCreated event) {
        log.debug("New Account created: {}", event);
        DefaultCategories.asList()
                .forEach(category -> categoryService.add(event.accountId(), category));
    }

    @Builder
    record AccountCreated(UUID accountId,
                          String firstName,
                          String lastName,
                          String emailAddress) {
    }
}
