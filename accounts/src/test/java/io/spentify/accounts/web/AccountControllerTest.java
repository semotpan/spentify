package io.spentify.accounts.web;

import io.spentify.accounts.TestContainersSetup;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static io.spentify.accounts.web.JsonPayloads.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AccountControllerTest extends TestContainersSetup {

    @Value("${kafka.topic.account.aggregate.name}")
    String topic;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should create a new account successfully")
    void createNewAccount() throws Exception {
        // given
        setUpAccountDebeziumConnector(restTemplate, topic);
        var request = newValidCreateAccountRequest();

        // when
        var resp = postNewAccount(request);

        // then
        assertThat(resp.getStatusCode()).isEqualTo(CREATED);

        // and
        assertThat(resp.getHeaders().getLocation()).isNotNull();

        // and: {@link AccountCreated} raised to kafka
        try (var consumer = kafkaConsumer()) {
            consumer.subscribe(List.of(topic));

            var changeEvents = drain(consumer, 1);

            JSONAssert.assertEquals(resp.getBody(), changeEvents.getFirst().value(), JSONCompareMode.STRICT);

            consumer.unsubscribe();
        }
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
