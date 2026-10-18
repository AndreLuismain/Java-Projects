package br.com.andreluismain.pix.controller;

import br.com.andreluismain.pix.domain.service.TransferService;
import br.com.andreluismain.pix.dto.request.TransferRequest;
import br.com.andreluismain.pix.dto.response.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para execução e consulta de transferências Pix.
 */
@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Executa transferência Pix idempotente entre duas contas virtuais.
     */
    @PostMapping
    public ResponseEntity<TransferResponse> executeTransfer(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TransferRequest request) {
        TransferResponse response = transferService.executeTransfer(idempotencyKey, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Consulta o status e detalhes de uma transferência Pix pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransferResponse> getTransfer(@PathVariable UUID id) {
        return ResponseEntity.ok(transferService.getTransferById(id));
    }
}
