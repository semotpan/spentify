package io.spentify.expenses.web;

import io.spentify.expenses.CreateExpenseUseCase;
import io.spentify.expenses.CreateExpenseUseCase.CreateExpenseCommand;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "/v1/expenses")
@RequiredArgsConstructor
final class ExpenseController implements ExpenseControllerDoc {

    private final CreateExpenseUseCase createExpenseUseCase;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody ExpenseResource resource) {
        var cmd = CreateExpenseCommand.builder()
                .accountId(resource.accountId())
                .categoryId(resource.categoryId())
                .amount(resource.amount())
                .expenseDate(resource.expenseDate())
                .paymentType(resource.paymentType())
                .description(resource.description())
                .build();

        var expense = createExpenseUseCase.create(cmd);
        return created(fromCurrentRequest().path("/{id}").build(expense.getId()))
                .body(ExpenseResource.builder()
                        .expenseId(expense.getId().id())
                        .accountId(expense.getAccount().id())
                        .categoryId(cmd.categoryId)
                        .creationTimestamp(expense.getCreationTimestamp())
                        .amount(expense.getAmountNumber())
                        .currency(expense.getCurrencyCode())
                        .paymentType(expense.getPaymentType().value)
                        .expenseDate(expense.getExpenseDate())
                        .description(expense.getDescription())
                        .build());
    }

    @Builder
    record ExpenseResource(UUID expenseId,
                           UUID accountId,
                           UUID categoryId,
                           Instant creationTimestamp,
                           BigDecimal amount,
                           String currency,
                           String paymentType,
                           LocalDate expenseDate,
                           String description) {}
}
