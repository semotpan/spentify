package io.spentify.expenses;

import lombok.Builder;

import java.util.Collection;

import static io.spentify.expenses.Failure.*;
import static java.util.Objects.requireNonNull;

/**
 * A marker interface for representing failure scenarios in an application.
 * Implementations of this interface encapsulate details about different types of failures.
 */
public sealed interface Failure permits ValidationFailure, NotFoundFailure, ConflictFailure {

    String message();

    static Failure ofValidation(String message, Collection<FieldViolation> fieldViolations) {
        return new ValidationFailure(message, fieldViolations);
    }

    static Failure ofNotFound(String message) {
        return new NotFoundFailure(message);
    }

    static Failure ofConflict(String message) {
        return new ConflictFailure(message);
    }

    /**
     * Represents a validation failure with specific field violations.
     */
    record ValidationFailure(String message, Collection<FieldViolation> fieldViolations) implements Failure {

        public ValidationFailure {
            requireNonNull(fieldViolations, "fieldViolations cannot be null");
        }
    }

    /**
     * Represents a "not found" failure.
     */
    record NotFoundFailure(String message) implements Failure {}

    /**
     * Represents a conflict failure.
     */
    record ConflictFailure(String message) implements Failure {}

    /**
     * Represents a field violation in the context of validation failure.
     */
    @Builder
    record FieldViolation(String field, String message, Object rejectedValue) {}
}
