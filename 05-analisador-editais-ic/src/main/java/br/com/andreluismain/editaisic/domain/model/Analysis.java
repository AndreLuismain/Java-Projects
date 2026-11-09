package br.com.andreluismain.editaisic.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entidade que armazena a análise semântica realizada pela IA sobre um edital.
 *
 * <p>Relaciona uma {@link Opportunity} com um {@link Profile} específico,
 * guardando pontuação de match, requisitos extraídos, riscos de elegibilidade e resumo.</p>
 */
@Entity
@Table(
    name = "analyses",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_opportunity_profile", columnNames = {"opportunity_id", "profile_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analysis {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "match_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal matchScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MatchClassification classification;

    /**
     * Requisitos estruturados serializados em JSON.
     */
    @Column(columnDefinition = "TEXT")
    private String requirements;

    /**
     * Riscos ou alertas de elegibilidade serializados em JSON.
     */
    @Column(columnDefinition = "TEXT")
    private String risks;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "prompt_version", nullable = false, length = 50)
    private String promptVersion;

    @Column(name = "analyzed_at", nullable = false)
    private Instant analyzedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.analyzedAt == null) {
            this.analyzedAt = Instant.now();
        }
    }
}
