package io.spentify.expenses;

import io.spentify.expenses.Failure.FieldViolation;
import io.vavr.control.Either;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.assertj.vavr.api.VavrAssertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class CategoryServiceImplTest {

    Categories categories;

    CategoryServiceImpl service;

    @BeforeEach
    void setUp() {
        categories = Mockito.mock(Categories.class);
        service = new CategoryServiceImpl(categories);
    }

    @Test
    @DisplayName("Category create fails when accountId is null")
    void createFailsWhenAccountIdIsNull() {
        // when
        var either = service.add(null, "Fun");

        // then
        assertThat(either)
                .isLeft()
                .containsOnLeft(Failure.ofValidation("Schema validation failure",
                        List.of(new FieldViolation("accountId", "accountId cannot be null", null))));
    }

    @ParameterizedTest
    @DisplayName("Category create fails when name is blank")
    @ValueSource(strings = {"", "  "})
    @NullSource
    void createFailsWhenNameIsBlank(String value) {
        // when
        var either = service.add(UUID.randomUUID(), value);

        // then
        assertThat(either)
                .isLeft()
                .containsOnLeft(Failure.ofValidation("Schema validation failure",
                        List.of(new FieldViolation("name", "name cannot be blank", value))));
    }

    @Test
    @DisplayName("Category create fails when name length overflow")
    void createFailsWhenNameLengthOverflow() {
        // given
        var value = random(101, true, true);

        // when
        var either = service.add(UUID.randomUUID(), value);

        // then
        assertThat(either)
                .isLeft()
                .containsOnLeft(Failure.ofValidation("Schema validation failure",
                        List.of(new FieldViolation("name", "name length cannot be more than 100", value))));
    }

    @Test
    @DisplayName("Category create fails when name for account exists")
    void createFailsWhenNameExists() {
        // given
        var accountId = UUID.randomUUID();
        var name = "Fun";
        when(categories.existsByAccountAndName(any(AccountIdentifier.class), anyString())).thenReturn(TRUE);

        // when
        var either = service.add(accountId, name);

        // then
        assertThat(either)
                .isLeft()
                .containsOnLeft(Failure.ofConflict("Category '%s' for account '%s' already exists".formatted(name, accountId)));

        // and
        verify(categories).existsByAccountAndName(any(AccountIdentifier.class), anyString());
    }


    @Test
    @DisplayName("Create a category")
    void creation() {
        // given
        var captor = ArgumentCaptor.forClass(Category.class);
        var accountId = UUID.randomUUID();
        var name = "Fun";

        when(categories.existsByAccountAndName(any(AccountIdentifier.class), anyString())).thenReturn(FALSE);
        when(categories.save(any(Category.class))).thenReturn(new Category(name, new AccountIdentifier(accountId)));

        // when
        Either<Failure, Category> either = service.add(accountId, name);

        // then
        assertThat(either)
                .containsRightInstanceOf(Category.class);

        // and
        verify(categories).existsByAccountAndName(any(AccountIdentifier.class), anyString());

        // and
        verify(categories).save(captor.capture());

        Assertions.assertThat(captor.getValue())
                .extracting(Category::getAccount, Category::getName)
                .containsExactly(new AccountIdentifier(accountId), name);
    }
}
