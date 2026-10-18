package br.com.andreluismain.documentador.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Documentação técnica gerada pelo agente a partir da análise de código legado.
 */
@Entity
@Table(name = "generated_documents")
public class GeneratedDocument {

    @Id
    private UUID id;

    @Column(name = "analysis_id", nullable = false)
    private UUID analysisId;

    @Lob
    @Column(name = "markdown", nullable = false)
    private String markdown;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "prompt_version", nullable = false, length = 50)
    private String promptVersion;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public GeneratedDocument() {
    }

    public GeneratedDocument(UUID id, UUID analysisId, String markdown, String modelName, String promptVersion, String contentHash) {
        this.id = id != null ? id : UUID.randomUUID();
        this.analysisId = analysisId;
        this.markdown = markdown;
        this.modelName = modelName;
        this.promptVersion = promptVersion;
        this.contentHash = contentHash;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(UUID analysisId) {
        this.analysisId = analysisId;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
