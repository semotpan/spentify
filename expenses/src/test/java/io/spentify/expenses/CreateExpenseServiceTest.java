package io.spentify.expenses;

import io.spentify.expenses.Category.CategoryIdentifier;
import jakarta.validation.ConstraintViolationException;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static io.spentify.expenses.CreateExpenseUseCase.CreateExpenseCommand.CreateExpenseCommandBuilder;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class CreateExpenseServiceTest {

    Expenses expenses;
    Categories categories;
    CreateExpenseService service;

    @BeforeEach
    void setUp() {
        expenses = Mockito.mock(Expenses.class);
        categories = Mockito.mock(Categories.class);
        service = new CreateExpenseService(expenses, categories);
    }

    @Test
    @DisplayName("should fail expense creation when 'accountId' is null")
    void failWhenAccountIdIsNull() {
        // when
        var thrown = catchThrowable(() -> validCommand().accountId(null).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("accountId: accountId cannot be null");
    }

    @Test
    @DisplayName("should fail expense creation when 'categoryId' is null")
    void failWhenCategoryIdIsNull() {
        // when
        var thrown = catchThrowable(() -> validCommand().categoryId(null).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("categoryId: categoryId cannot be null");
    }

    @Test
    @DisplayName("should fail expense creation when 'amount' is null")
    void failWhenAmountIsNull() {
        // when
        var thrown = catchThrowable(() -> validCommand().amount(null).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("amount: amount cannot be null");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-10.56, 0.0})
    @DisplayName("should fail expense creation when 'amount' is not positive")
    void failWhenAmountIsNotPositive(double amount) {
        // when
        var thrown = catchThrowable(() -> validCommand().amount(BigDecimal.valueOf(amount)).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("amount: amount must be greater than 0");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "_cash", "CA SH", "C A R D", "card "})
    @NullSource
    @DisplayName("should fail expense creation when 'paymentType' is invalid")
    void failWhenPaymentTypeIsInvalid(String value) {
        // when
        var thrown = catchThrowable(() -> validCommand().paymentType(value).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("paymentType: paymentType can be: 'cash', 'card'");
    }

    @Test
    @DisplayName("should fail expense creation when 'amount' is null")
    void failWhenExpenseDateIsNull() {
        // when
        var thrown = catchThrowable(() -> validCommand().expenseDate(null).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("expenseDate: expenseDate cannot be null");
    }

    @Test
    @DisplayName("should fail expense creation when 'categoryId' and 'accountId' not found")
    void failWhenCategoryAccountNotFound() {
        // given
        when(categories.existsByIdAndAccount(any(CategoryIdentifier.class), any(AccountIdentifier.class))).thenReturn(FALSE);
        var cmd = validCommand().build();

        // when
        var thrown = catchThrowable(() -> service.create(cmd));

        // then
        assertThat(thrown)
                .isInstanceOf(CategoryAccountNotFound.class)
                .hasMessageContaining("category id '%s' for account id '%s' not found".formatted(cmd.categoryId, cmd.accountId));

        // and
        verify(categories).existsByIdAndAccount(any(CategoryIdentifier.class), any(AccountIdentifier.class));
    }

    @Test
    @DisplayName("should create a new expense successfully")
    void createNewExpense() {
        // given
        var cmd = validCommand().build();
        var category = new Category("Fun", new AccountIdentifier(cmd.accountId));
        when(categories.existsByIdAndAccount(any(CategoryIdentifier.class), any(AccountIdentifier.class))).thenReturn(TRUE);
        when(categories.getReferenceById(any(CategoryIdentifier.class))).thenReturn(category);

        // when
        var expense = service.create(cmd);

        // then
        assertThat(expense).isNotNull();
        assertThat(expense.getId()).isNotNull();
        assertThat(expense.getCreationTimestamp()).isNotNull();
        assertThat(expense)
                .extracting(
                        Expense::getCategory,
                        Expense::getAccount,
                        Expense::getAmount,
                        Expense::getPaymentType,
                        Expense::getExpenseDate,
                        Expense::getDescription
                )
                .containsExactly(
                        category,
                        new AccountIdentifier(cmd.accountId),
                        Money.of(BigDecimal.TEN, "EUR"),
                        Payment.Type.CARD,
                        cmd.expenseDate,
                        "Andy's pizza lunch"
                );

        // and
        verify(categories).existsByIdAndAccount(any(CategoryIdentifier.class), any(AccountIdentifier.class));
        verify(categories).getReferenceById(any(CategoryIdentifier.class));
        verify(expenses).save(expense);
    }

    private CreateExpenseCommandBuilder validCommand() {
        return CreateExpenseUseCase.CreateExpenseCommand.builder()
                .accountId(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
                .amount(BigDecimal.TEN)
                .expenseDate(LocalDate.now())
                .paymentType(Payment.Type.CARD.value)
                .description("Andy's pizza lunch");
    }
}
