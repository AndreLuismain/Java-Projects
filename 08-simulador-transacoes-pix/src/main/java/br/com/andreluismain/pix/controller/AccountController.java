package br.com.andreluismain.pix.controller;

import br.com.andreluismain.pix.domain.service.AccountService;
import br.com.andreluismain.pix.domain.service.LedgerService;
import br.com.andreluismain.pix.dto.request.CreateAccountRequest;
import br.com.andreluismain.pix.dto.response.AccountResponse;
import br.com.andreluismain.pix.dto.response.LedgerEntryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para gerenciamento de contas virtuais e consulta de extratos.
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final LedgerService ledgerService;

    public AccountController(AccountService accountService, LedgerService ledgerService) {
        this.accountService = accountService;
        this.ledgerService = ledgerService;
    }

    /**
     * Cria uma nova conta virtual Pix.
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtém detalhes de saldo e cadastro da conta.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    /**
     * Obtém o extrato bancário paginado e imutável da conta.
     */
    @GetMapping("/{id}/statement")
    public ResponseEntity<Page<LedgerEntryResponse>> getStatement(
            @PathVariable UUID id,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ledgerService.getStatement(id, pageable));
    }
}
