package io.spentify.accounts.messaging;

import io.hypersistence.utils.hibernate.type.json.JsonNodeBinaryType;
import io.spentify.accounts.DomainEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

/**
 * Entity class representing an Outbox Event to store domain events in a persistent store.
 */
@Entity
@Table(name = "outboxevent")
@NoArgsConstructor(access = PRIVATE, force = true)
final class OutboxEvent {

    @Id
    private final UUID id;
    private final Instant timestamp;
    private final String aggregateid;
    private final String aggregatetype;
    private final String type;

    @Type(JsonNodeBinaryType.class)
    private final Object payload;

    public OutboxEvent(DomainEvent event) {
        requireNonNull(event, "event cannot be null");
        this.id = UUID.randomUUID();
        this.timestamp = requireNonNull(event.issuedOn(), "issuedOn cannot be null");
        this.aggregateid = requireNonNull(event.aggregateId(), "aggregateid cannot be null");
        this.aggregatetype = requireNonNull(event.aggregateType(), "aggregatetype cannot be null");
        this.type = requireNonNull(event.type(), "type cannot be null");
        this.payload = requireNonNull(event.payload(), "payload cannot be null");
    }
}
