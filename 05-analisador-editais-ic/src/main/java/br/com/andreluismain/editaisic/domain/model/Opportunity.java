package br.com.andreluismain.editaisic.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidade que representa uma oportunidade ou edital acadêmico coletado da web.
 *
 * <p>Armazena metadados de publicação, URL canônica, conteúdo textual normalizado
 * extraído do HTML e hash SHA-256 para evitar duplicidade de análise.</p>
 */
@Entity
@Table(name = "opportunities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opportunity {

    @Id
    private UUID id;

    @Column(name = "source_url", nullable = false, unique = true, length = 2048)
    private String sourceUrl;

    @Column(name = "source_name", nullable = false)
    private String sourceName;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 255)
    private String institution;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "deadline_at")
    private Instant deadlineAt;

    @Column(name = "raw_content", nullable = false, columnDefinition = "TEXT")
    private String rawContent;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OpportunityStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.status == null) {
            this.status = OpportunityStatus.COLLECTED;
        }
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
