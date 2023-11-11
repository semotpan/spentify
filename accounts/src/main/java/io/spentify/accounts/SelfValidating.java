package io.spentify.accounts;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public abstract class SelfValidating<T> {

    private final Validator validator;

    public SelfValidating() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    protected void validateSelf() {
        var violations = validator.validate((T) this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
