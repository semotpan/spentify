package io.spentify.accounts.web;

import io.spentify.accounts.AccountService;
import io.spentify.accounts.AccountService.CreateAccountCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "/accounts")
@RequiredArgsConstructor
final class AccountController implements AccountControllerDoc {

    private final AccountService accountService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody AccountResource resource) {
        var command = CreateAccountCommand.builder()
                .firstName(resource.firstName())
                .lastName(resource.lastName())
                .emailAddress(resource.emailAddress())
                .build();
        var account = accountService.create(command);
        return created(fromCurrentRequest().path("/{id}").build(account.getId().id()))
                .body(new AccountResource(
                        account.getId().id(),
                        account.getFirstName(),
                        account.getLastName(),
                        account.getEmailAddress().toString()));
    }

    record AccountResource(UUID accountId, String firstName, String lastName, String emailAddress) {}
}
