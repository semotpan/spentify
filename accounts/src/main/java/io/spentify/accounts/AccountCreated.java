package io.spentify.accounts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * The {@link AccountCreated} class represents a domain event indicating the creation of a new user account.
 */
public final class AccountCreated implements DomainEvent {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final Account account;
    private final Instant issuedOn;

    public AccountCreated(Account account) {
        this.account = requireNonNull(account, "account cannot be null");
        this.issuedOn = Instant.now();
    }

    public Instant issuedOn() {
        return issuedOn;
    }

    public String aggregateId() {
        return account.getId().toString();
    }

    public String aggregateType() {
        return this.getClass().getSimpleName();
    }

    public String type() {
        return this.account.getClass().getSimpleName();
    }

    public JsonNode payload() {
        return mapper.createObjectNode()
                .put("accountId", account.getId().toString())
                .put("firstName", account.getFirstName())
                .put("lastName", account.getLastName())
                .put("emailAddress", account.getEmailAddress().toString());
    }
}
