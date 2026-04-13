package com.cora.api.account;

import com.cora.api.account.dto.AccountResponse;
import com.cora.api.account.dto.CreateAccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Accounts", description = "Operações sobre contas bancárias")
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Criar conta", description = "Cria uma nova conta bancária e retorna o recurso criado.")
    @ApiResponse(responseCode = "201", description = "Conta criada com sucesso",
            headers = @Header(name = "Location", description = "URI da conta criada", schema = @Schema(type = "string")),
            content = @Content(schema = @Schema(implementation = AccountResponse.class)))
    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        var created = accountService.create(request);
        URI location = uriBuilder.path("/accounts/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Listar contas", description = "Retorna todas as contas bancárias cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista de contas (pode ser vazia)",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountResponse.class))))
    @GetMapping
    public ResponseEntity<List<AccountResponse>> list() {
        return ResponseEntity.ok(accountService.list());
    }
}

