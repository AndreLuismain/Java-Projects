package br.com.andreluismain.recomendacao.domain.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade de item do ranking de recomendação com scores auditáveis e motivos.
 */
@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    private UUID id;

    @Column(name = "run_id", nullable = false)
    private UUID runId;

    @Column(name = "opportunity_id", nullable = false)
    private UUID opportunityId;

    @Column(name = "deterministic_score", nullable = false)
    private Double deterministicScore;

    @Column(name = "semantic_score")
    private Double semanticScore;

    @Column(name = "final_score", nullable = false)
    private Double finalScore;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recommendation_reasons", joinColumns = @JoinColumn(name = "recommendation_id"))
    @Column(name = "reason", length = 500)
    private List<String> reasons = new ArrayList<>();

    @Column(name = "ranking_position", nullable = false)
    private Integer rank;

    public Recommendation() {
    }

    public Recommendation(UUID id, UUID runId, UUID opportunityId, Double deterministicScore,
                          Double semanticScore, Double finalScore, List<String> reasons, Integer rank) {
        this.id = id != null ? id : UUID.randomUUID();
        this.runId = runId;
        this.opportunityId = opportunityId;
        this.deterministicScore = deterministicScore;
        this.semanticScore = semanticScore;
        this.finalScore = finalScore;
        this.reasons = reasons != null ? new ArrayList<>(reasons) : new ArrayList<>();
        this.rank = rank;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRunId() {
        return runId;
    }

    public void setRunId(UUID runId) {
        this.runId = runId;
    }

    public UUID getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(UUID opportunityId) {
        this.opportunityId = opportunityId;
    }

    public Double getDeterministicScore() {
        return deterministicScore;
    }

    public void setDeterministicScore(Double deterministicScore) {
        this.deterministicScore = deterministicScore;
    }

    public Double getSemanticScore() {
        return semanticScore;
    }

    public void setSemanticScore(Double semanticScore) {
        this.semanticScore = semanticScore;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
