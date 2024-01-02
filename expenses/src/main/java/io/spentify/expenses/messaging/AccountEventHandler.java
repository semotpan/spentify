package io.spentify.expenses.messaging;

import io.spentify.expenses.CategoryService;
import io.spentify.expenses.DefaultCategories;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * {@link AccountEventHandler} processes account creation events, ensuring exactly once semantic.
 * It uses {@link EventLogs} and {@link CategoryService} for event processing and category management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
class AccountEventHandler {

    private final EventLogs eventLogs;
    private final CategoryService categoryService;

    @Transactional
    public void handle(UUID eventId, AccountCreated event) {
        // ensure exactly once semantic (:kafka at least once)
        if (eventLogs.alreadyProcessed(eventId)) {
            log.info("Event with UUID {} was already retrieved, ignoring it", eventId);
            return;
        }

        DefaultCategories.asList()
                .forEach(category -> categoryService.add(event.accountId(), category));

        eventLogs.process(eventId);
    }
}
