package br.com.andreluismain.documentador.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de erro seguro para auditoria caso o processamento da análise falhe.
 */
@Entity
@Table(name = "analysis_errors")
public class AnalysisError {

    @Id
    private UUID id;

    @Column(name = "analysis_id", nullable = false)
    private UUID analysisId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "message_safe", nullable = false, length = 1000)
    private String messageSafe;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AnalysisError() {
    }

    public AnalysisError(UUID id, UUID analysisId, String code, String messageSafe) {
        this.id = id != null ? id : UUID.randomUUID();
        this.analysisId = analysisId;
        this.code = code;
        this.messageSafe = messageSafe;
        this.createdAt = LocalDateTime.now();
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getAnalysisId() {
        return analysisId;
    }

    public String getCode() {
        return code;
    }

    public String getMessageSafe() {
        return messageSafe;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
