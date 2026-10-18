package br.com.andreluismain.pix.domain.service;

import br.com.andreluismain.pix.domain.model.*;
import br.com.andreluismain.pix.dto.request.TransferRequest;
import br.com.andreluismain.pix.dto.response.TransferResponse;
import br.com.andreluismain.pix.exception.IdempotencyConflictException;
import br.com.andreluismain.pix.exception.InsufficientBalanceException;
import br.com.andreluismain.pix.exception.InvalidTransferException;
import br.com.andreluismain.pix.repository.LedgerEntryRepository;
import br.com.andreluismain.pix.repository.OutboxEventRepository;
import br.com.andreluismain.pix.repository.TransferRepository;
import br.com.andreluismain.pix.repository.WalletAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private WalletAccountRepository accountRepository;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private TransferService transferService;

    private UUID sourceId;
    private UUID destId;
    private WalletAccount sourceAccount;
    private WalletAccount destAccount;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(
                accountRepository,
                transferRepository,
                ledgerEntryRepository,
                outboxEventRepository,
                objectMapper
        );
        transferService.setSecurityLimitInCents(500000L); // R$ 5.000,00

        sourceId = UUID.randomUUID();
        destId = UUID.randomUUID();

        sourceAccount = new WalletAccount(sourceId, UUID.randomUUID(), "source@pix.com", 100000L, "BRL");
        destAccount = new WalletAccount(destId, UUID.randomUUID(), "dest@pix.com", 20000L, "BRL");
    }

    @Test
    @DisplayName("Deve executar transferência Pix com sucesso, atualizar saldos e gravar ledger")
    void shouldExecuteTransferSuccessfully() {
        String idempotencyKey = "key-123";
        TransferRequest request = new TransferRequest(sourceId, destId, 30000L);

        when(transferRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(destAccount));

        TransferResponse response = transferService.executeTransfer(idempotencyKey, request);

        assertNotNull(response);
        assertEquals(TransferStatus.COMPLETED, response.status());
        assertEquals(70000L, sourceAccount.getBalanceInCents());
        assertEquals(50000L, destAccount.getBalanceInCents());

        // Verifica lançamentos contábeis (débito e crédito)
        verify(ledgerEntryRepository, times(2)).save(any(LedgerEntry.class));
        verify(transferRepository).save(any(Transfer.class));
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }

    @Test
    @DisplayName("Deve retornar transferência existente ao repetir chave de idempotência com mesmos dados")
    void shouldReturnExistingTransferOnDuplicateIdempotencyKey() {
        String idempotencyKey = "key-idem-duplicate";
        TransferRequest request = new TransferRequest(sourceId, destId, 15000L);

        Transfer existingTransfer = new Transfer(
                UUID.randomUUID(),
                idempotencyKey,
                sourceId,
                destId,
                15000L,
                TransferStatus.COMPLETED,
                "corr-1"
        );

        when(transferRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingTransfer));

        TransferResponse response = transferService.executeTransfer(idempotencyKey, request);

        assertNotNull(response);
        assertEquals(existingTransfer.getId(), response.id());
        verify(accountRepository, never()).save(any());
        verify(ledgerEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar conflito se chave de idempotência for reutilizada com parâmetros divergentes")
    void shouldThrowConflictWhenIdempotencyKeyReusedWithDifferentParams() {
        String idempotencyKey = "key-conflict";
        TransferRequest request = new TransferRequest(sourceId, destId, 99999L);

        Transfer existingTransfer = new Transfer(
                UUID.randomUUID(),
                idempotencyKey,
                sourceId,
                destId,
                1000L, // valor diferente
                TransferStatus.COMPLETED,
                "corr-1"
        );

        when(transferRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingTransfer));

        assertThrows(IdempotencyConflictException.class, () ->
                transferService.executeTransfer(idempotencyKey, request));
    }

    @Test
    @DisplayName("Deve rejeitar transferência quando saldo da conta de origem for insuficiente")
    void shouldRejectWhenInsufficientBalance() {
        String idempotencyKey = "key-insufficient";
        TransferRequest request = new TransferRequest(sourceId, destId, 200000L); // Saldo é 100000L

        when(transferRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(destAccount));

        assertThrows(InsufficientBalanceException.class, () ->
                transferService.executeTransfer(idempotencyKey, request));
    }

    @Test
    @DisplayName("Deve disparar evento Outbox quando valor exceder limite de segurança")
    void shouldEmitOutboxEventWhenTransferAboveSecurityLimit() {
        String idempotencyKey = "key-security";
        sourceAccount.setBalanceInCents(2000000L);
        TransferRequest request = new TransferRequest(sourceId, destId, 800000L); // > 500000L

        when(transferRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(accountRepository.findById(sourceId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destId)).thenReturn(Optional.of(destAccount));

        transferService.executeTransfer(idempotencyKey, request);

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        OutboxEvent event = outboxCaptor.getValue();
        assertEquals("PIX_TRANSFER_ABOVE_SECURITY_LIMIT", event.getEventType());
        assertEquals(OutboxStatus.PENDING, event.getStatus());
        assertTrue(event.getPayload().contains("800000"));
    }

    @Test
    @DisplayName("Deve rejeitar transferência para a mesma conta")
    void shouldRejectTransferToSameAccount() {
        TransferRequest request = new TransferRequest(sourceId, sourceId, 1000L);

        assertThrows(InvalidTransferException.class, () ->
                transferService.executeTransfer("key-same", request));
    }
}
