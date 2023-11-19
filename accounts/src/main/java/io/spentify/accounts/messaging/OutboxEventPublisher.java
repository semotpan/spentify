package io.spentify.accounts.messaging;

import io.spentify.accounts.DomainEvent;
import io.spentify.accounts.EventPublisher;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
class OutboxEventPublisher implements EventPublisher {

    private final EntityManager entityManager;

    @Override
    public void publish(List<DomainEvent> events) {
        requireNonNull(events, "events cannot be null");
        events.forEach(event -> entityManager.persist(new OutboxEvent(event)));
    }
}
