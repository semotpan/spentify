package io.spentify.accounts;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

/**
 * <p>The {@code DomainEvent} interface defines the common properties that a domain event should have.</p>
 */
public interface DomainEvent {

    /**
     * Retrieves the timestamp when the domain event was issued.
     *
     * @return The timestamp when the domain event was issued.
     */
    Instant issuedOn();

    /**
     * Retrieves the identifier of the aggregate associated with the domain event.
     *
     * @return The identifier of the aggregate.
     */
    String aggregateId();

    /**
     * Retrieves the type of the aggregate associated with the domain event (ex. AccountCreated).
     *
     * @return The type of the aggregate.
     */
    String aggregateType();

    /**
     * Retrieves the type of the domain event.
     *
     * @return The type of the domain event.
     */
    String type();

    /**
     * Retrieves the payload associated with the domain event.
     * The payload contains the specific information related to the event.
     *
     * @return The payload of the domain event.
     */
    JsonNode payload();

}
