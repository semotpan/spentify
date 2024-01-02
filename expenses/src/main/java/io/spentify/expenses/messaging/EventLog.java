package io.spentify.expenses.messaging;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * {@link EventLog} records the consumption of Kafka event messages in the system.
 */
@Entity
@Table(name = "eventlog")
@NoArgsConstructor(access = PRIVATE, force = true) // JPA compliant
class EventLog implements Serializable {

    @Id
    private final UUID eventId;

    private final Instant issuedOn;

    EventLog(UUID eventId) {
        this.eventId = eventId;
        this.issuedOn = Instant.now();
    }
}
