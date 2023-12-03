package io.spentify.expenses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static io.spentify.expenses.Expense.ExpenseIdentifier;

/**
 * The {@link Expenses} interface serves as a repository for managing {@link Expense} entities
 */
@Repository
public interface Expenses extends JpaRepository<Expense, ExpenseIdentifier> {
}
