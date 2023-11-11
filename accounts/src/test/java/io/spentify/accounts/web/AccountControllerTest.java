package io.spentify.accounts.web;

import io.spentify.accounts.TestAccountsApplication;
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

import static io.spentify.accounts.web.JsonPayloads.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestAccountsApplication.class)
class AccountControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should create a new account successfully")
    void createNewAccount() {
        // given
        var request = newValidCreateAccountRequest();

        // when
        var resp = postNewAccount(request);

        // then
        assertThat(resp.getStatusCode()).isEqualTo(CREATED);

        // and
        assertThat(resp.getHeaders().getLocation()).isNotNull();
    }

    @Test
    @DisplayName("Should fail account creation when request has validation failures")
    void failCreationWhenRequestHasValidationFailures() throws JSONException {
        // given
        var request = newInvalidCreateAccountRequest();

        // when
        var resp = postNewAccount(request);

        // then
        assertThat(resp.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY);

        // and
        JSONAssert.assertEquals(expectedCreateAccountValidationFailure(), resp.getBody(), LENIENT);
    }

    private ResponseEntity<String> postNewAccount(String request) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return restTemplate.postForEntity("/accounts", new HttpEntity<>(request, headers), String.class);
    }
}
