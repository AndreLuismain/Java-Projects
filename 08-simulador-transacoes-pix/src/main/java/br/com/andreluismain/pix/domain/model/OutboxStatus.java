package br.com.andreluismain.pix.domain.model;

/**
 * Status de entrega do evento transactional outbox.
 */
public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
