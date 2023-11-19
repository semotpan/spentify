package io.spentify.accounts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * The {@link  AccountService} interface defines the contract for a service responsible for
 * managing user accounts within the application.
 */
public interface AccountService {

    /**
     * Creates a new user account based on the provided {@link CreateAccountCommand}.
     *
     * @param command The {@link CreateAccountCommand} containing information for creating the user account.
     * @return The {@link Account} object representing the newly created user account.
     */
    Account create(CreateAccountCommand command);

    /**
     * The {@link CreateAccountCommand} class represents a command for creating a new user account.
     */
    class CreateAccountCommand extends SelfValidating<CreateAccountCommand> {

        @NotBlank(message = "firstName cannot be blank")
        @Size(max = Account.MAX_LENGTH, message = "firstName overflow, max allowed 100 characters")
        public final String firstName;

        @NotBlank(message = "lastName cannot be blank")
        @Size(max = Account.MAX_LENGTH, message = "lastName overflow, max allowed 100 characters")
        public final String lastName;

        @NotBlank(message = "emailAddress cannot be blank")
        @Size(max = Account.MAX_LENGTH, message = "emailAddress overflow, max allowed 100 characters")
        @Pattern(regexp = Account.patternRFC5322, message = "emailAddress must follow RFC standard")
        public final String emailAddress;

        @Builder
        public CreateAccountCommand(String firstName, String lastName, String emailAddress) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailAddress = emailAddress;
            this.validateSelf();
        }
    }
}
