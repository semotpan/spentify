package io.spentify.expenses;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public final class Payment {

    public enum Type {

        CASH("Cash"),
        CARD("Card");

        public final String value;

        Type(String value) {
            this.value = value;
        }

        public static boolean containsValue(String value) {
            for (var b : values()) {
                if (b.value.equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public static Type fromValue(String value) {
            for (var b : values()) {
                if (b.value.equalsIgnoreCase(value))
                    return b;
            }

            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = CheckPaymentTypeValidator.class)
    @Documented
    public @interface CheckPaymentType {
        String message() default "{io.spentify.expenses.Payment.Type.message}";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static final class CheckPaymentTypeValidator implements ConstraintValidator<CheckPaymentType, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return Type.containsValue(value);
        }
    }
}
