package io.spentify.expenses.messaging;

import lombok.Builder;

import java.util.UUID;

/**
 * {@link AccountCreated} is raiesd by the accounts service on account creation
 */
@Builder
record AccountCreated(UUID accountId,
                      String firstName,
                      String lastName,
                      String emailAddress) {
}
