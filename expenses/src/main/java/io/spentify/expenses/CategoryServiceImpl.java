package io.spentify.expenses;

import io.spentify.expenses.Category.AccountIdentifier;
import io.spentify.expenses.Failure.FieldViolation;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static io.vavr.API.Invalid;
import static io.vavr.API.Valid;
import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional
@RequiredArgsConstructor
class CategoryServiceImpl implements CategoryService {

    private final FieldValidator validator = new FieldValidator();
    private final Categories categories;

    @Override
    public Either<Failure, Category> add(UUID accountId, String name) {
        var validation = validator.validate(accountId, name);

        if (validation.isInvalid()) {
            return Either.left(Failure.ofValidation("Schema validation failure", validation.getError().asJava()));
        }

        if (categories.existsByAccountAndName(new AccountIdentifier(accountId), name)) {
            return Either.left(Failure.ofConflict("Category '%s' for account '%s' already exists".formatted(name, accountId)));
        }

        var category = categories.save(new Category(name, new AccountIdentifier(accountId)));
        return Either.right(category);
    }

    /**
     * Schema input validator
     */
    private static final class FieldValidator {

        private Validation<Seq<FieldViolation>, Tuple2<UUID, String>> validate(UUID accountId, String name) {
            return Validation.combine(validateAccountId(accountId), validateName(name)).ap(Tuple::of);
        }

        private Validation<FieldViolation, UUID> validateAccountId(UUID accountId) {
            if (nonNull(accountId))
                return Valid(accountId);

            return Invalid(FieldViolation.builder()
                    .field("accountId")
                    .message("accountId cannot be null")
                    .build());
        }

        private Validation<FieldViolation, String> validateName(String name) {
            if (!isBlank(name) && name.length() <= Category.MAX_LENGTH)
                return Valid(name);

            var message = isBlank(name) ? "name cannot be blank" :
                    format("name length cannot be more than {0}", Category.MAX_LENGTH);

            return Invalid(FieldViolation.builder()
                    .field("name")
                    .message(message)
                    .rejectedValue(name)
                    .build());
        }
    }
}
