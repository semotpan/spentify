package io.spentify.accounts.web;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class JsonPayloads {

    public static String newValidCreateAccountRequest() {
        return """
                {
                    "firstName": "Jon",
                    "lastName": "Snow",
                    "emailAddress": "jonsnow@email.me"
                }
                """;
    }

    public static String newInvalidCreateAccountRequest() {
        return """
                {
                    "firstName": null,
                    "lastName": " ",
                    "emailAddress": "jonsnow_email.me"
                }
                """;
    }

    public static String expectedCreateAccountValidationFailure() {
        return """
                {
                  "status": 422,
                  "errorCode": "UNPROCESSABLE_ENTITY",
                  "message": "Schema validation failure",
                  "errors": [
                    {
                      "field": "firstName",
                      "message": "firstName cannot be blank"
                    },
                    {
                      "field": "lastName",
                      "message": "lastName cannot be blank"
                    },
                    {
                      "field": "emailAddress",
                      "message": "emailAddress must follow RFC standard",
                      "rejectedValue": "jonsnow_email.me"
                    }
                  ]
                }
                """;
    }

}
