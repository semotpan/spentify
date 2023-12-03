package io.spentify.expenses;

import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CompositeType;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

/**
 * The {@link Expense} class represents an expense aggregate.
 * It is mapped to the "expenses" table in the database.
 */

@Entity
@Table(name = "expenses")
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE, force = true)
public class Expense {

    @EmbeddedId
    private final ExpenseIdentifier id;
    private final Instant creationTimestamp;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "account_id"))
    private final AccountIdentifier account;

    @AttributeOverride(name = "amount", column = @Column(name = "amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    @CompositeType(MonetaryAmountType.class)
    private MonetaryAmount amount;

    @Enumerated(EnumType.STRING)
    private Payment.Type paymentType;

    private LocalDate expenseDate;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Builder
    public Expense(AccountIdentifier account,
                   MonetaryAmount amount,
                   Payment.Type paymentType,
                   LocalDate expenseDate,
                   String description,
                   Category category) {
        this.id = new ExpenseIdentifier(UUID.randomUUID());
        this.creationTimestamp = Instant.now();
        this.account = requireNonNull(account, "account cannot be null");
        this.amount = requireValidAmount(amount);
        this.category = requireNonNull(category, "category cannot be null");
        this.paymentType = paymentType == null ? Payment.Type.CARD : paymentType;
        this.expenseDate = expenseDate == null ? LocalDate.now() : expenseDate;
        this.description = description;
    }

    private MonetaryAmount requireValidAmount(MonetaryAmount amount) {
        requireNonNull(amount, "amount cannot be null");

        if (amount.isLessThanOrEqualTo(Money.of(BigDecimal.ZERO, amount.getCurrency()))) {
            throw new IllegalArgumentException("amount must be positive value");
        }

        return amount;
    }

    public BigDecimal getAmountNumber() {
        return amount.getNumber().numberValue(BigDecimal.class);
    }

    public String getCurrencyCode() {
        return amount.getCurrency().getCurrencyCode();
    }

    @Embeddable
    public record ExpenseIdentifier(UUID id) implements Serializable {

        public ExpenseIdentifier {
            requireNonNull(id, "id cannot be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
