package io.spentify.accounts;

import java.util.List;

public interface EventPublisher {

    /**
     * Publishes a list of domain events to notify subscribers about changes in the
     * application's state.
     *
     * @param events The list of domain events to be published.
     */
    void publish(List<DomainEvent> events);

}
