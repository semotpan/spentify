package io.spentify.expenses.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_KEY;

/**
 * {@link AccountsOutboxEventsConsumer} listens for account-related events from the Kafka outbox.
 */
@Component
@RequiredArgsConstructor
@Slf4j
class AccountsOutboxEventsConsumer {

    private final AccountEventHandler accountEventHandler;

    /**
     * Listens for incoming Kafka messages and handles account creation events.
     *
     * @param key       The key of the Kafka message.
     * @param eventId   Unique ID for each message that ensures exactly once semantic
     * @param eventType The type of the event. (eg. "AccountCreated")
     * @param event     The {@link AccountCreated} event payload.
     */
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${kafka.topic.accounts.outbox.events.name}",
            containerFactory = "accountCreatedKLCFactory"
    )
    void on(@Header(RECEIVED_KEY) UUID key,
            @Header("id") String eventId,
            @Header("eventType") String eventType,
            @Payload AccountCreated event) {
        log.debug("Kafka message with key = {}, eventType {} arrived", key, eventType);
        accountEventHandler.handle(UUID.fromString(eventId), event);
    }
}
