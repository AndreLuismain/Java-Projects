CREATE TABLE wallet_accounts (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    pix_key VARCHAR(150) NOT NULL UNIQUE,
    balance_in_cents BIGINT NOT NULL DEFAULT 0,
    currency VARCHAR(10) NOT NULL DEFAULT 'BRL',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE transfers (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    source_account_id UUID NOT NULL,
    destination_account_id UUID NOT NULL,
    amount_in_cents BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    correlation_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    transfer_id UUID NOT NULL,
    account_id UUID NOT NULL,
    entry_type VARCHAR(10) NOT NULL,
    amount_in_cents BIGINT NOT NULL,
    balance_after_in_cents BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_ledger_account_created ON ledger_entries(account_id, created_at DESC);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload CLOB NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP
);
