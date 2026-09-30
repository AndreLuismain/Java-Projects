package br.com.andreluismain.pix.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de registro contábil append-only (livro-razão).
 * Registros de débito e crédito são imutáveis e auditáveis.
 */
@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    private UUID id;

    @Column(name = "transfer_id", nullable = false)
    private UUID transferId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 10)
    private LedgerEntryType entryType;

    @Column(name = "amount_in_cents", nullable = false)
    private Long amountInCents;

    @Column(name = "balance_after_in_cents", nullable = false)
    private Long balanceAfterInCents;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public LedgerEntry() {
    }

    public LedgerEntry(UUID id, UUID transferId, UUID accountId, LedgerEntryType entryType,
                       Long amountInCents, Long balanceAfterInCents) {
        this.id = id != null ? id : UUID.randomUUID();
        this.transferId = transferId;
        this.accountId = accountId;
        this.entryType = entryType;
        this.amountInCents = amountInCents;
        this.balanceAfterInCents = balanceAfterInCents;
        this.createdAt = LocalDateTime.now();
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getTransferId() {
        return transferId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public LedgerEntryType getEntryType() {
        return entryType;
    }

    public Long getAmountInCents() {
        return amountInCents;
    }

    public Long getBalanceAfterInCents() {
        return balanceAfterInCents;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
