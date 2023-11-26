package io.spentify.expenses;

import io.vavr.control.Either;

import java.util.UUID;

/**
 * Service interface for managing categories.
 */
public interface CategoryService {

    /**
     * Adds a new category with the specified name for the given account.
     *
     * @param accountId The unique identifier of the account to which the category will be added.
     * @param name      The name of the category to be added.
     * @return An {@link Either} representing the outcome of the operation.
     * - If successful, returns a {@link Category} instance.
     * - If a failure occurs, returns a {@link Failure} instance.
     */
    Either<Failure, Category> add(UUID accountId, String name);

}
