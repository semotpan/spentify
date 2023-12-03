package io.spentify.expenses.web;

import io.spentify.expenses.TestContainersSetup;
import io.spentify.expenses.TestExpensesApplication;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import static io.spentify.expenses.web.JsonPayloads.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestExpensesApplication.class)
class ExpenseControllerTest extends TestContainersSetup {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("should create a new expense")
    @Sql("/persistence/create-expense-category.sql")
    void createNewExpense() throws JSONException {
        // given
        var request = validCreateExpenseRequest();

        // when
        var resp = postNewExpense(request);

        // then
        assertThat(resp.getStatusCode()).isEqualTo(CREATED);

        // and
        assertThat(resp.getHeaders().getLocation()).isNotNull();

        // and
        JSONAssert.assertEquals(expectedCreatedExpense(), resp.getBody(), LENIENT);
    }

    @Test
    @DisplayName("should fail expense creation when request has validation failures")
    void failCreationWhenValidationFailures() throws JSONException {
        // given
        var request = invalidCreateRequest();

        // when
        var resp = postNewExpense(request);

        // then
        assertThat(resp.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY);

        // and
        JSONAssert.assertEquals(expectedCreateValidationFailure(), resp.getBody(), LENIENT);
    }

    private ResponseEntity<String> postNewExpense(String request) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return restTemplate.postForEntity("/v1/expenses", new HttpEntity<>(request, headers), String.class);
    }
}
