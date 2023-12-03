package io.spentify.expenses;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;

@Entity
@Table(name = "expense_category")
@Getter
@ToString
@NoArgsConstructor(access = PRIVATE, force = true)
public final class Category {

    static final int MAX_LENGTH = 100;

    @EmbeddedId
    private final CategoryIdentifier id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "account_id"))
    private final AccountIdentifier account;

    private final Instant creationTimestamp;
    private String name;

    public Category(String name, AccountIdentifier account) {
        this.id = new CategoryIdentifier(UUID.randomUUID());
        this.creationTimestamp = Instant.now();
        this.account = requireNonNull(account, "account cannot be null");
        this.name = requireValidName(name);
    }

    private String requireValidName(String name) {
        if (isBlank(name)) {
            throw new IllegalArgumentException("name cannot be blank");
        }

        if (length(name) > MAX_LENGTH) {
            throw new IllegalArgumentException("name overflow, max length allowed '%d'".formatted(MAX_LENGTH));
        }

        return name;
    }

    @Embeddable
    public record CategoryIdentifier(UUID id) implements Serializable {

        public CategoryIdentifier {
            requireNonNull(id, "id cannot be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(account, category.account) &&
                Objects.equals(creationTimestamp, category.creationTimestamp) && Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, creationTimestamp, name);
    }
}
