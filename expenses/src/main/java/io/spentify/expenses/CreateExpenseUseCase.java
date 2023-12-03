package io.spentify.expenses;

import io.spentify.expenses.Payment.CheckPaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * The {@link CreateExpenseUseCase} interface represents a use case for creating expenses.
 */
public interface CreateExpenseUseCase {

    /**
     * Creates an expense based on the provided {@link CreateExpenseCommand}.
     *
     * @param cmd The command containing information for creating the expense.
     * @return The created {@link  Expense} or throws {@link CategoryAccountNotFound} if category id and account id not found.
     */

    Expense create(CreateExpenseCommand cmd);

    @Builder
    class CreateExpenseCommand extends SelfValidating<CreateExpenseCommand> {

        @NotNull(message = "accountId cannot be null")
        public final UUID accountId;

        @NotNull(message = "categoryId cannot be null")
        public final UUID categoryId;

        @NotNull(message = "amount cannot be null")
        @Positive(message = "amount must be greater than 0")
        public final BigDecimal amount;

        @CheckPaymentType(message = "paymentType can be: 'cash', 'card'")
        public final String paymentType;

        @NotNull(message = "expenseDate cannot be null")
        public final LocalDate expenseDate;

        public final String description;

        public CreateExpenseCommand(UUID accountId,
                                    UUID categoryId,
                                    BigDecimal amount,
                                    String paymentType,
                                    LocalDate expenseDate,
                                    String description) {
            this.accountId = accountId;
            this.categoryId = categoryId;
            this.amount = amount;
            this.paymentType = paymentType;
            this.expenseDate = expenseDate;
            this.description = description;
            this.validateSelf();
        }
    }
}
