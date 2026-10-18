package br.com.andreluismain.documentador.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que rastreia uma solicitação de análise de código legado.
 */
@Entity
@Table(name = "analyses")
public class Analysis {

    @Id
    private UUID id;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "language", nullable = false, length = 50)
    private String language;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Lob
    @Column(name = "source_code", nullable = false)
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AnalysisStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Analysis() {
    }

    public Analysis(UUID id, String fileName, String language, Long sizeBytes, String contentHash, String sourceCode) {
        this.id = id != null ? id : UUID.randomUUID();
        this.fileName = fileName;
        this.language = language;
        this.sizeBytes = sizeBytes;
        this.contentHash = contentHash;
        this.sourceCode = sourceCode;
        this.status = AnalysisStatus.RECEIVED;
        this.createdAt = LocalDateTime.now();
    }

    public void markProcessing() {
        this.status = AnalysisStatus.PROCESSING;
    }

    public void markCompleted() {
        this.status = AnalysisStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = AnalysisStatus.FAILED;
        this.completedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
