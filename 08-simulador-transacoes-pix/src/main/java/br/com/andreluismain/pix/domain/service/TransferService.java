package br.com.andreluismain.pix.domain.service;

import br.com.andreluismain.pix.domain.event.PixSecurityLimitEvent;
import br.com.andreluismain.pix.domain.model.*;
import br.com.andreluismain.pix.dto.request.TransferRequest;
import br.com.andreluismain.pix.dto.response.TransferResponse;
import br.com.andreluismain.pix.exception.*;
import br.com.andreluismain.pix.repository.LedgerEntryRepository;
import br.com.andreluismain.pix.repository.OutboxEventRepository;
import br.com.andreluismain.pix.repository.TransferRepository;
import br.com.andreluismain.pix.repository.WalletAccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço transacional de execução de transferências Pix.
 * Aplica bloqueio pessimista determinístico por UUID para evitar deadlocks,
 * garante atomicidade, preenche o livro-razão imutável (ledger) e emite outbox events.
 */
@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final WalletAccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${pix.security-limit:500000}")
    private long securityLimitInCents = 500000L;

    public TransferService(WalletAccountRepository accountRepository,
                           TransferRepository transferRepository,
                           LedgerEntryRepository ledgerEntryRepository,
                           OutboxEventRepository outboxEventRepository,
                           ObjectMapper objectMapper) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = (objectMapper != null ? objectMapper : new ObjectMapper()).findAndRegisterModules();
    }

    public void setSecurityLimitInCents(long securityLimitInCents) {
        this.securityLimitInCents = securityLimitInCents;
    }

    /**
     * Executa transferência Pix idempotente e consistente.
     *
     * @param idempotencyKey chave de idempotência obrigatória do header
     * @param request dados da transferência
     * @return detalhes da transferência concluída
     */
    @Transactional
    public TransferResponse executeTransfer(String idempotencyKey, TransferRequest request) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new InvalidTransferException("Cabeçalho Idempotency-Key é obrigatório");
        }

        // 1. Verificação de idempotência prévia
        Optional<Transfer> existingTransfer = transferRepository.findByIdempotencyKey(idempotencyKey);
        if (existingTransfer.isPresent()) {
            Transfer existing = existingTransfer.get();
            if (!Objects.equals(existing.getSourceAccountId(), request.sourceAccountId()) ||
                !Objects.equals(existing.getDestinationAccountId(), request.destinationAccountId()) ||
                !Objects.equals(existing.getAmountInCents(), request.amountInCents())) {
                throw new IdempotencyConflictException("A chave de idempotência informada já foi usada para parâmetros diferentes");
            }
            log.info("Transferência recuperada por chave de idempotência: id={}", existing.getId());
            return TransferResponse.from(existing);
        }

        // 2. Validações preliminares
        UUID sourceId = request.sourceAccountId();
        UUID destId = request.destinationAccountId();
        long amount = request.amountInCents();

        if (sourceId.equals(destId)) {
            throw new InvalidTransferException("Conta de origem e destino não podem ser iguais");
        }
        if (amount <= 0) {
            throw new InvalidTransferException("Valor da transferência deve ser maior que zero");
        }

        // 3. Bloqueio determinístico de contas ordenado lexicograficamente por UUID
        UUID firstId = sourceId.compareTo(destId) < 0 ? sourceId : destId;
        UUID secondId = sourceId.compareTo(destId) < 0 ? destId : sourceId;

        accountRepository.findByIdWithLock(firstId);
        accountRepository.findByIdWithLock(secondId);

        // 4. Recuperação das contas atualizadas
        WalletAccount sourceAccount = accountRepository.findById(sourceId)
                .orElseThrow(() -> new AccountNotFoundException(sourceId));
        WalletAccount destAccount = accountRepository.findById(destId)
                .orElseThrow(() -> new AccountNotFoundException(destId));

        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountStatusException("Conta de origem está inativa ou bloqueada");
        }
        if (destAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountStatusException("Conta de destino está inativa ou bloqueada");
        }
        if (!sourceAccount.getCurrency().equalsIgnoreCase(destAccount.getCurrency())) {
            throw new InvalidTransferException("Contas devem possuir a mesma moeda para transferência Pix");
        }

        // 5. Verificação de saldo
        if (sourceAccount.getBalanceInCents() < amount) {
            throw new InsufficientBalanceException("Saldo insuficiente na conta de origem");
        }

        // 6. Atualização de saldos
        sourceAccount.debit(amount);
        destAccount.credit(amount);
        accountRepository.save(sourceAccount);
        accountRepository.save(destAccount);

        // 7. Registro da transferência
        UUID transferId = UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();
        Transfer transfer = new Transfer(
                transferId,
                idempotencyKey,
                sourceId,
                destId,
                amount,
                TransferStatus.COMPLETED,
                correlationId
        );
        transferRepository.save(transfer);

        // 8. Lançamentos contábeis no Ledger (append-only)
        LedgerEntry debitEntry = new LedgerEntry(
                UUID.randomUUID(),
                transferId,
                sourceId,
                LedgerEntryType.DEBIT,
                amount,
                sourceAccount.getBalanceInCents()
        );
        LedgerEntry creditEntry = new LedgerEntry(
                UUID.randomUUID(),
                transferId,
                destId,
                LedgerEntryType.CREDIT,
                amount,
                destAccount.getBalanceInCents()
        );
        ledgerEntryRepository.save(debitEntry);
        ledgerEntryRepository.save(creditEntry);

        // 9. Registro de evento outbox caso exceda o limite de segurança
        if (amount > securityLimitInCents) {
            registerSecurityOutboxEvent(transfer, sourceAccount);
        }

        log.info("Transferência Pix concluída: id={}, valor={} centavos, origem={}, destino={}",
                transferId, amount, sourceId, destId);

        return TransferResponse.from(transfer);
    }

    /**
     * Localiza transferência por ID.
     */
    @Transactional(readOnly = true)
    public TransferResponse getTransferById(UUID id) {
        return transferRepository.findById(id)
                .map(TransferResponse::from)
                .orElseThrow(() -> new PixException("Transferência com id '" + id + "' não encontrada",
                        org.springframework.http.HttpStatus.NOT_FOUND, "TRANSFER_NOT_FOUND"));
    }

    private void registerSecurityOutboxEvent(Transfer transfer, WalletAccount sourceAccount) {
        try {
            PixSecurityLimitEvent event = PixSecurityLimitEvent.of(
                    transfer.getId(),
                    transfer.getAmountInCents(),
                    sourceAccount.getId(),
                    "Transferência excede limite de segurança configurado de " + securityLimitInCents + " centavos"
            );
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = new OutboxEvent(
                    UUID.randomUUID(),
                    transfer.getId().toString(),
                    PixSecurityLimitEvent.EVENT_TYPE,
                    payload
            );
            outboxEventRepository.save(outboxEvent);
            log.warn("Evento de segurança registrado na outbox para transferência acima do limite: transferId={}",
                    transfer.getId());
        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar payload de evento de segurança da outbox", e);
            throw new PixException("Falha ao registrar evento de segurança",
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "OUTBOX_SERIALIZATION_ERROR");
        }
    }
}
