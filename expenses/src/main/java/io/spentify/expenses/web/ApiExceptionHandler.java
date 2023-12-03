package io.spentify.expenses.web;

import io.spentify.expenses.CategoryAccountNotFound;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static io.spentify.expenses.web.ApiErrorResponse.*;

/**
 * Global exception handler for the API, providing customized responses for various exceptions.
 */
@Slf4j
@RestControllerAdvice
final class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles exceptions related to unreadable HTTP messages, typically caused by malformed JSON requests.
     */
    @Override
    protected ResponseEntity handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                          HttpStatusCode status, WebRequest request) {
        return badRequest("Malformed JSON request", ex.getMessage());
    }

    /**
     * Handles exceptions related to type mismatches in HTTP requests.
     */
    @Override
    protected ResponseEntity handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                HttpStatusCode status, WebRequest request) {
        return badRequest("Type mismatch request", ex.getMessage());
    }

    /**
     * Handles exceptions when no handler is found for a given HTTP request.
     */
    @Override
    protected ResponseEntity handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                           HttpStatusCode status, WebRequest request) {
        return notFound("Resource '" + ex.getRequestURL() + "' not found", ex.getMessage());
    }

    /**
     * Handles exceptions related to unsupported HTTP request methods.
     */
    @Override
    protected ResponseEntity handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                 HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var supportedMethods = ex.getSupportedHttpMethods();

        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(ex.getSupportedHttpMethods());
        }

        return methodNotAllowed(headers, "Request method '" + ex.getMethod() + "' is not supported", ex.getMessage());
    }

    /**
     * Handles exceptions related to unacceptable HTTP media types.
     */
    @Override
    protected ResponseEntity handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers,
                                                              HttpStatusCode status, WebRequest request) {
        return notAcceptable("Could not find acceptable representation", ex.getMessage());
    }

    /**
     * Handles exceptions related to unsupported HTTP media types.
     */
    @Override
    protected ResponseEntity handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers,
                                                             HttpStatusCode status, WebRequest request) {
        var mediaTypes = ex.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(ex.getSupportedMediaTypes());
        }

        return unsupportedMediaType(headers, "Content type '" + ex.getContentType() + "' is not supported", ex.getMessage());
    }

    /**
     * Handles the {@link CategoryAccountNotFound} exception, returning a not found response.
     */
    @ExceptionHandler(value = CategoryAccountNotFound.class)
    ResponseEntity<?> handle(CategoryAccountNotFound ex) {
        return notFound(ex.getMessage());
    }

    /**
     * Handles {@link ConstraintViolationException}, converting constraint violations into a list of API error fields.
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<?> handle(ConstraintViolationException ex) {
        var apiErrors = ex.getConstraintViolations().stream()
                .map(c -> new ApiErrorField(c.getPropertyPath().toString(), c.getMessage(), c.getInvalidValue()))
                .toList();
        return unprocessableEntity(apiErrors, "Schema validation failure");
    }

    /**
     * Handles unexpected exception, internal server error response.
     */
    @ExceptionHandler(Throwable.class)
    ResponseEntity<ApiErrorResponse> handleThrowable(Throwable throwable) {
        log.error("Request handling failed", throwable);

        return internalServerError("An unexpected error occurred", throwable.getMessage());
    }
}
