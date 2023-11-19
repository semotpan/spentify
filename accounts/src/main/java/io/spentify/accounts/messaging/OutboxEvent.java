package io.spentify.accounts.messaging;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.spentify.accounts.DomainEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

/**
 * Entity class representing an Outbox Event to store domain events in a persistent store.
 */
@Entity
@Table(name = "outbox_event")
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE)
class OutboxEvent {

    @Id
    private UUID id;
    private Instant timestamp;
    private String aggregateId;
    private String aggregateType;
    private String type;

    @Type(JsonType.class)
    private Object payload;

    public OutboxEvent(DomainEvent event) {
        requireNonNull(event, "event cannot be null");
        this.id = UUID.randomUUID();
        this.timestamp = requireNonNull(event.issuedOn(), "issuedOn cannot be null");
        this.aggregateId = requireNonNull(event.aggregateId(), "aggregateId cannot be null");
        this.aggregateType = requireNonNull(event.aggregateType(), "aggregateType cannot be null");
        this.type = requireNonNull(event.type(), "type cannot be null");
        this.payload = requireNonNull(event.payload(), "payload cannot be null");
    }
}
