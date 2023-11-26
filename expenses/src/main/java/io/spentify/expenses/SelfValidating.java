package io.spentify.expenses;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * An abstract base class for self-validating entities/commands.
 * <p>
 * This class provides a mechanism to automatically validate the state of an object
 * using the Java Bean Validation (JSR 380) framework. Subclasses should call the
 * {@code validateSelf()} method within their constructors or methods to perform
 * validation based on the configured constraints.
 *
 * @param <T> The type of the self-validating entity/command.
 */
public abstract class SelfValidating<T> {

    private final Validator validator;

    public SelfValidating() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * Validates the current object based on the configured constraints.
     * <p>
     * This method should be called within the constructor or other relevant methods
     * of subclasses to trigger automatic validation.
     *
     * @throws ConstraintViolationException If the object does not satisfy the defined constraints.
     */
    protected void validateSelf() {
        var violations = validator.validate((T) this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
