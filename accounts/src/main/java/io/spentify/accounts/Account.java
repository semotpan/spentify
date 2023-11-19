package io.spentify.accounts;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.compile;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * <p>The account entity is used to model user information and includes validation checks for various properties.</p>
 */
@Entity
@Table(name = "accounts")
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE)
@ToString
public final class Account {

    static final String patternRFC5322 = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    static final int MAX_LENGTH = 100;

    @ToString.Exclude
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    private @EmbeddedId AccountIdentifier id;
    private @Embedded EmailAddress emailAddress;
    private String firstName;
    private String lastName;

    public Account(String firstName, String lastName, EmailAddress emailAddress) {
        this.id = new AccountIdentifier(UUID.randomUUID());
        this.emailAddress = requireNonNull(emailAddress, "emailAddress cannot be null");

        requireNonBlank(firstName, "firstName cannot be blank");
        requireNonOverflow(firstName, "firstName overflow, max length allowed '%d'".formatted(MAX_LENGTH));
        this.firstName = firstName.trim();

        requireNonBlank(lastName, "lastName cannot be blank");
        requireNonOverflow(lastName, "lastName overflow, max length allowed '%d'".formatted(MAX_LENGTH));
        this.lastName = lastName.trim();

        domainEvents.add(new AccountCreated(this));
    }

    /**
     * Clears any domain events associated with this account.
     */
    public void clearEvents() {
        this.domainEvents.clear();
    }

    /**
     * Represents the identifier of an {@link Account}.
     */
    @Embeddable
    public record AccountIdentifier(UUID id) implements Serializable {

        public AccountIdentifier {
            requireNonNull(id, "id cannot be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }

    /**
     * Represents the email address of an {@code Account}.
     */
    @Embeddable
    public record EmailAddress(String emailAddress) implements Serializable {

        public EmailAddress {
            requireNonBlank(emailAddress, "emailAddress cannot be blank");
            requireNonOverflow(emailAddress, "emailAddress max length must be '%d'".formatted(MAX_LENGTH));

            if (!compile(patternRFC5322).matcher(emailAddress).matches()) {
                throw new IllegalArgumentException("emailAddress must match '%s'".formatted(patternRFC5322));
            }
        }

        @Override
        public String toString() {
            return emailAddress;
        }
    }

    private static void requireNonBlank(String text, String message) {
        if (isBlank(text))
            throw new IllegalArgumentException(message);
    }

    private static void requireNonOverflow(String text, String message) {
        if (StringUtils.length(text) > Account.MAX_LENGTH)
            throw new IllegalArgumentException(message);
    }
}
