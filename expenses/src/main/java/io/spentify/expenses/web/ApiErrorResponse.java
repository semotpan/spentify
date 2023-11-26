package io.spentify.expenses.web;

import lombok.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collection;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Represents an error response for API requests
 *
 * @param timestamp    The timestamp when the error occurred.
 * @param status       The HTTP status code of the error.
 * @param errorCode    The HTTP status code as an enumeration.
 * @param message      A human-readable message describing the error.
 * @param debugMessage A more detailed debug message providing additional information about the error.
 * @param errors       A collection of specific error fields associated with the request (only for 422 status).
 */
public record ApiErrorResponse(Instant timestamp,
                               Integer status,
                               HttpStatus errorCode,
                               String message,
                               String debugMessage,
                               Collection<ApiErrorField> errors) {

    @Builder
    public ApiErrorResponse(HttpStatus httpStatus, String message, String debugMessage, Collection<ApiErrorField> errors) {
        this(Instant.now(), httpStatus.value(), httpStatus, message, debugMessage, errors);
    }

    /**
     * Creates a {@link ResponseEntity} with a Bad Request (400) status.
     */
    public static ResponseEntity<ApiErrorResponse> badRequest(String message, String debugMessage) {
        return ResponseEntity.status(BAD_REQUEST)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(BAD_REQUEST)
                        .message(message)
                        .debugMessage(debugMessage)
                        .build());
    }

    /**
     * Creates a {@link ResponseEntity} with a Forbidden (401) status.
     */
    public static ResponseEntity<ApiErrorResponse> forbidden(String message, String debugMessage) {
        return ResponseEntity.status(FORBIDDEN)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(FORBIDDEN)
                        .message(message)
                        .debugMessage(debugMessage)
                        .build());
    }

    /**
     * Creates a {@link ResponseEntity} with a Not Found (404) status and a null debug message.
     */
    public static ResponseEntity<ApiErrorResponse> notFound(String message) {
        return notFound(message, null);
    }

    /**
     * Creates a {@link ResponseEntity} with a Not Found (404) status.
     */
    public static ResponseEntity<ApiErrorResponse> notFound(String message, String debugMessage) {
        return ResponseEntity.status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(NOT_FOUND)
                        .message(message)
                        .debugMessage(debugMessage)
                        .build());
    }

    /**
     * Creates a {@link ResponseEntity} with a Method Not Allowed (405) status.
     */
    public static ResponseEntity<ApiErrorResponse> methodNotAllowed(HttpHeaders headers, String message, String debugMessage) {
        return ResponseEntity.status(METHOD_NOT_ALLOWED)
                .headers(headers)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(METHOD_NOT_ALLOWED)
                        .message(message)
                        .debugMessage(debugMessage)
                        .build());
    }

    /**
     * Creates a {@link ResponseEntity} with a Not Acceptable (406) status.
     */
    public static ResponseEntity<ApiErrorResponse> notAcceptable(String message, String debugMessage) {
        return ResponseEntity.status(NOT_ACCEPTABLE)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(NOT_ACCEPTABLE)
                        .message(message)
                        .debugMessage(debugMessage)
                        .build());

    }

    /**
     * Creates a {@link ResponseEntity} with a Conflict (409) status and a null debug message.
     */
    public static ResponseEntity<ApiErrorResponse> conflict(String message) {
        return conflict(message, null);

    }

    /**
     * Creates a {@link ResponseEntity} with a Conflict (409) status.
     */
    public static ResponseEntity<ApiErrorResponse> conflict(String message, String debugMessage) {
        return ResponseEntity.status(CONFLICT)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(CONFLICT)
                        .message(message)
                        .debugMessage(debugMessage)
                        .build());

    }

    /**
     * Creates a {@link ResponseEntity} with an Unsupported Media Type (415) status.
     */
    public static ResponseEntity<ApiErrorResponse> unsupportedMediaType(HttpHeaders headers, String message, String debugMessage) {
        return ResponseEntity.status(UNSUPPORTED_MEDIA_TYPE)
                .headers(headers)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(UNSUPPORTED_MEDIA_TYPE)
                        .message(message)
                        .debugMessage(debugMessage)
                        .build());
    }

    /**
     * Creates a {@link ResponseEntity} with an Unprocessable Entity (422) status.
     */
    public static ResponseEntity<ApiErrorResponse> unprocessableEntity(Collection<ApiErrorField> errors, String message) {
        return ResponseEntity.status(UNPROCESSABLE_ENTITY)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(UNPROCESSABLE_ENTITY)
                        .message(message)
                        .errors(errors)
                        .build());
    }

    /**
     * Creates a {@link ResponseEntity} with an Internal Server Error (500) status.
     */
    public static ResponseEntity<ApiErrorResponse> internalServerError(String message, String debugMessage) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_JSON)
                .body(ApiErrorResponse.builder()
                        .httpStatus(INTERNAL_SERVER_ERROR)
                        .message(message)
                        .debugMessage(debugMessage)
                        .build());
    }

    /**
     * Represents a field-specific error in the API response.
     *
     * @param field         The field associated with the error.
     * @param message       A human-readable message describing the error.
     * @param rejectedValue The value that was rejected (if applicable).
     */
    public record ApiErrorField(String field, String message, Object rejectedValue) {}
}
