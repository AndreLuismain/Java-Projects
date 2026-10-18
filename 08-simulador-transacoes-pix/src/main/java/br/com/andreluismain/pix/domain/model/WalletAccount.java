package br.com.andreluismain.pix.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa uma conta de carteira virtual com chave Pix e saldo.
 * Possui controle de concorrência otimista via @Version e invariante de saldo >= 0.
 */
@Entity
@Table(name = "wallet_accounts")
public class WalletAccount {

    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "pix_key", nullable = false, unique = true, length = 150)
    private String pixKey;

    @Column(name = "balance_in_cents", nullable = false)
    private Long balanceInCents;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public WalletAccount() {
    }

    public WalletAccount(UUID id, UUID ownerId, String pixKey, Long initialBalanceInCents, String currency) {
        this.id = id != null ? id : UUID.randomUUID();
        this.ownerId = Objects.requireNonNull(ownerId, "OwnerId não pode ser nulo");
        this.pixKey = Objects.requireNonNull(pixKey, "Chave Pix não pode ser nula");
        this.balanceInCents = initialBalanceInCents != null ? initialBalanceInCents : 0L;
        this.currency = currency != null ? currency.toUpperCase() : "BRL";
        this.status = AccountStatus.ACTIVE;
        this.version = 0L;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Efetua débito no saldo da conta caso haja saldo suficiente.
     *
     * @param amount valor em centavos a ser debitado (positivo)
     * @throws IllegalStateException se a conta não estiver ativa ou saldo insuficiente
     */
    public void debit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor do débito deve ser maior que zero");
        }
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Conta inativa para transações");
        }
        if (this.balanceInCents < amount) {
            throw new IllegalStateException("Saldo insuficiente para transferência");
        }
        this.balanceInCents -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Efetua crédito no saldo da conta.
     *
     * @param amount valor em centavos a ser creditado (positivo)
     */
    public void credit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor do crédito deve ser maior que zero");
        }
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Conta inativa para transações");
        }
        this.balanceInCents += amount;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getPixKey() {
        return pixKey;
    }

    public void setPixKey(String pixKey) {
        this.pixKey = pixKey;
    }

    public Long getBalanceInCents() {
        return balanceInCents;
    }

    public void setBalanceInCents(Long balanceInCents) {
        this.balanceInCents = balanceInCents;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
