package io.spentify.accounts.web;

import io.spentify.accounts.web.AccountController.AccountResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.HttpHeaders.LOCATION;

interface AccountControllerDoc {

    String TAG = "accounts";

    @Operation(summary = "Create a new account in the Spentify", description = "Create a new account in the Spentify",
            security = {@SecurityRequirement(name = "openId")},
            tags = {TAG})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful Operation", headers = @Header(name = LOCATION)),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Duplication Failure", content = @Content),
            @ApiResponse(responseCode = "422", description = "Validation Failure", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    ResponseEntity<?> create(@RequestBody AccountResource req);

}
