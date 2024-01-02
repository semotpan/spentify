package io.spentify.expenses.messaging;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * {@link EventLogs} is a repository interface for managing {@link EventLog} entities.
 */
@Repository
interface EventLogs extends CrudRepository<EventLog, UUID> {

    /**
     * Marks the specified event as processed by saving a new {@link EventLog} entry.
     */
    default void process(UUID eventId) {
        save(new EventLog(eventId));
    }

    /**
     * Checks if an event with the given ID has already been processed.
     */
    @Query("""
            SELECT COUNT (cm.eventId)=1
            FROM EventLog cm
            WHERE cm.eventId = :eventId
            """)
    boolean alreadyProcessed(UUID eventId);
}
