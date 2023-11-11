package io.spentify.accounts;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Accounts extends CrudRepository<Account, Account.AccountIdentifier> {

    boolean existsByEmailAddress(Account.EmailAddress emailAddress);

}
