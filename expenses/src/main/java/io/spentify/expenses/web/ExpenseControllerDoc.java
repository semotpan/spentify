package io.spentify.expenses.web;

import io.spentify.expenses.web.ExpenseController.ExpenseResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface ExpenseControllerDoc {

    String TAG = "expenses";

    @Operation(summary = "Add a new expense in the Spentify",
            description = "Operation to add a new expense for the current logged-in account, the expense must be attached to an expense category",
            security = {@SecurityRequirement(name = "openId")},
            tags = {TAG})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful Operation",
                    headers = @Header(name = LOCATION), content = @Content(mediaType = APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid Input",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found Failure",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation Failure",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    ResponseEntity<?> create(@RequestBody(description = "Expense Resource to be created", required = true) ExpenseResource resource);


}
