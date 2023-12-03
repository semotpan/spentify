package io.spentify.expenses.web;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
final class JsonPayloads {

    public static String validCreateExpenseRequest() {
        return """
                {
                    "accountId"  : "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca",
                    "categoryId" : "3b257779-a5db-4e87-9365-72c6f8d4977d",
                    "paymentType": "Cash",
                    "amount"     : 10.0,
                    "expenseDate": "2023-10-13",
                    "description": "Books buying"
                }
                """;
    }

    public static String expectedCreatedExpense() {
        return """
                {
                    "accountId"  : "e2709aa2-7907-4f78-98b6-0f36a0c1b5ca",
                    "categoryId" : "3b257779-a5db-4e87-9365-72c6f8d4977d",
                    "paymentType": "Cash",
                    "amount"     : 10.0,
                    "currency"   : "EUR",
                    "expenseDate": "2023-10-13",
                    "description": "Books buying"
                }
                """;
    }

    public static String invalidCreateRequest() {
        return """
                {
                    "paymentType": "C a s h",
                    "amount"     : -10.0
                }
                """;
    }

    public static String expectedCreateValidationFailure() {
        return """
                {
                  "status": 422,
                  "errorCode": "UNPROCESSABLE_ENTITY",
                  "message": "Schema validation failure",
                  "errors": [
                    {
                      "field": "accountId",
                      "message": "accountId cannot be null"
                    },
                    {
                      "field": "categoryId",
                      "message": "categoryId cannot be null"
                    },
                    {
                      "field": "paymentType",
                      "message": "paymentType can be: 'cash', 'card'",
                      "rejectedValue": "C a s h"
                    },
                    {
                      "field": "amount",
                      "message": "amount must be greater than 0",
                      "rejectedValue": -10.0
                    },
                    {
                      "field": "expenseDate",
                      "message": "expenseDate cannot be null"
                    }
                  ]
                }
                """;
    }
}
