package io.spentify.expenses;

public final class CategoryAccountNotFound extends RuntimeException {

    public CategoryAccountNotFound(String message) {
        super(message);
    }
}
