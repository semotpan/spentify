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
    private final EventPublisher eventPublisher;

    @Override
    public Account create(CreateAccountCommand command) {
        requireUniqueEmailAddress(command.emailAddress);

        var account = new Account(command.firstName, command.lastName, new EmailAddress(command.emailAddress));
        accounts.save(account);

        eventPublisher.publish(account.getDomainEvents());
        account.clearEvents();

        return account;
    }

    private void requireUniqueEmailAddress(String emailAddress) {
        if (accounts.existsByEmailAddress(new EmailAddress(emailAddress))) {
            throw new EmailAlreadyExists("emailAddress '%s' already exits".formatted(emailAddress));
        }
    }
}
