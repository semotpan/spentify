package io.spentify.accounts;

import io.spentify.accounts.Account.EmailAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
// TODO extend the implementation
class AccountServiceImpl implements AccountService {

    private final Accounts accounts;

    @Override
    public Account.AccountIdentifier create(CreateAccountCommand command) {
        requireUniqueEmailAddress(command.emailAddress);

        var account = new Account(command.firstName, command.lastName, new EmailAddress(command.emailAddress));
        accounts.save(account);

        return account.getId();
    }

    private void requireUniqueEmailAddress(String emailAddress) {
        if (accounts.existsByEmailAddress(new EmailAddress(emailAddress))) {
            throw new EmailAlreadyExists("emailAddress '%s' already exits".formatted(emailAddress));
        }
    }
}
