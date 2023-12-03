package io.spentify.expenses;

import io.spentify.expenses.Category.CategoryIdentifier;
import lombok.RequiredArgsConstructor;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
class CreateExpenseService implements CreateExpenseUseCase {

    private final Expenses expenses;
    private final Categories categories;

    @Override
    public Expense create(CreateExpenseCommand cmd) {
        requireCategoryAccountExists(cmd.categoryId, cmd.accountId);

        var expense = Expense.builder()
                .account(new AccountIdentifier(cmd.accountId))
                .paymentType(Payment.Type.fromValue(cmd.paymentType))
                .amount(Money.of(cmd.amount, "EUR"))
                .expenseDate(cmd.expenseDate)
                .description(cmd.description)
                .category(categories.getReferenceById(new CategoryIdentifier(cmd.categoryId)))
                .build();

        expenses.save(expense);
        return expense;
    }

    private void requireCategoryAccountExists(UUID categoryId, UUID accountId) {
        if (!categories.existsByIdAndAccount(new CategoryIdentifier(categoryId), new AccountIdentifier(accountId))) {
            throw new CategoryAccountNotFound("category id '%s' for account id '%s' not found".formatted(categoryId, accountId));
        }
    }
}
