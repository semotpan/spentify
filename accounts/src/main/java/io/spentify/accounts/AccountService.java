package io.spentify.accounts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public interface AccountService {

    Account.AccountIdentifier create(CreateAccountCommand command);

    @Builder
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

        public CreateAccountCommand(String firstName, String lastName, String emailAddress) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailAddress = emailAddress;
            this.validateSelf();
        }
    }
}
