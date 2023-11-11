package io.spentify.accounts;

import io.spentify.accounts.Account.AccountIdentifier;
import io.spentify.accounts.Account.EmailAddress;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import static io.spentify.accounts.AccountService.CreateAccountCommand;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
class AccountServiceImplTest {

    Accounts accounts;

    AccountServiceImpl service;

    @BeforeEach
    void setUp() {
        accounts = Mockito.mock(Accounts.class);
        service = new AccountServiceImpl(accounts);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    @DisplayName("Should fail account creation when 'firstName' is blank")
    void failWhenFirstNameIsBlank(String firstName) {
        // when
        var thrown = catchThrowable(() -> validAccount().firstName(firstName).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("firstName: firstName cannot be blank");
    }

    @Test
    @DisplayName("Should fail account creation when 'firstName' overflow")
    void failWhenFirstNameOverflow() {
        // given
        var firstName = RandomStringUtils.random(101);

        // when
        var thrown = catchThrowable(() -> validAccount().firstName(firstName).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("firstName: firstName overflow, max allowed 100 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    @DisplayName("Should fail account creation when 'lastName' is blank")
    void failWhenLastNameIsBlank(String lastName) {
        // when
        var thrown = catchThrowable(() -> validAccount().lastName(lastName).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("lastName: lastName cannot be blank");
    }

    @Test
    @DisplayName("Should fail account creation when 'lastName' overflow")
    void failWhenLastNameOverflow() {
        // given
        var lastName = RandomStringUtils.random(101);

        // when
        var thrown = catchThrowable(() -> validAccount().lastName(lastName).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("lastName: lastName overflow, max allowed 100 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    @DisplayName("Should fail account creation when 'emailAddress' is null")
    void failWhenEmailAddressIsNull(String emailAddress) {
        // when
        var thrown = catchThrowable(() -> validAccount().emailAddress(emailAddress).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("emailAddress: emailAddress cannot be blank");
    }

    @Test
    @DisplayName("Should fail account creation when 'emailAddress' overflow")
    void failWhenEmailAddressOverflow() {
        // given
        var emailAddress = RandomStringUtils.random(101);

        // when
        var thrown = catchThrowable(() -> validAccount().emailAddress(emailAddress).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("emailAddress: emailAddress overflow, max allowed 100 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {"value+@@gmail.com", "value@gmail..com", "@3mail", "1111@", "a3.com",
            "aa123@b23", "@qwe123.er", "jon.doe@gmailcom", "MOzaRT54@", "Abc.example.com", "A@b@c@example.com",
            "_underscore_u@domain_com.con", "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com", "this is\"not\\allowed@example.com",
            "this\\ still\\\"not\\\\allowed@example.com"})
    @DisplayName("Should fail account creation when 'emailAddress' don't follow RFC standard")
    void failWhenEmailAddressPatternUnmatched(String emailAddress) {
        // when
        var thrown = catchThrowable(() -> validAccount().emailAddress(emailAddress).build());

        // then
        assertThat(thrown)
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("emailAddress: emailAddress must follow RFC standard");
    }

    @Test
    @DisplayName("Should fail account creation when 'emailAddress' already exists")
    void failWhenEmailAddressExists() {
        // given: an existing email address
        when(accounts.existsByEmailAddress(any(EmailAddress.class))).thenReturn(TRUE);
        var command = validAccount().build();

        // when
        var thrown = catchThrowable(() -> service.create(command));

        // then
        assertThat(thrown)
                .isInstanceOf(EmailAlreadyExists.class)
                .hasMessageContaining("emailAddress '%s' already exits".formatted(command.emailAddress));

        // and
        verify(accounts).existsByEmailAddress(any(EmailAddress.class));
    }

    @Test
    @DisplayName("Should create a new account successfully")
    void createNewAccount() {
        // given: a non-existing email address
        when(accounts.existsByEmailAddress(any(EmailAddress.class))).thenReturn(FALSE);
        var command = validAccount().build();

        // when
        var accountIdentifier = service.create(command);

        // then
        assertThat(accountIdentifier).isNotNull();
        assertThat(accountIdentifier).isInstanceOf(AccountIdentifier.class);

        // and
        verify(accounts).existsByEmailAddress(any(EmailAddress.class));
    }

    private CreateAccountCommand.CreateAccountCommandBuilder validAccount() {
        return CreateAccountCommand.builder()
                .firstName("Jon")
                .lastName("Snow")
                .emailAddress("jonsnow@email.me");
    }
}
