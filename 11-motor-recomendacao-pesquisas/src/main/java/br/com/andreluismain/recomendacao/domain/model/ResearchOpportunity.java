package br.com.andreluismain.recomendacao.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Oportunidade ou edital de pesquisa acadêmica normalizado pela pipeline ETL.
 */
@Entity
@Table(name = "research_opportunities")
public class ResearchOpportunity {

    @Id
    private UUID id;

    @Column(name = "source_id", length = 100)
    private String sourceId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "institution", length = 200)
    private String institution;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "opportunity_technologies", joinColumns = @JoinColumn(name = "opportunity_id"))
    @Column(name = "technology")
    private List<String> normalizedTechnologies = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "opportunity_areas", joinColumns = @JoinColumn(name = "opportunity_id"))
    @Column(name = "area")
    private List<String> normalizedAreas = new ArrayList<>();

    @Column(name = "modality", length = 50)
    private String modality;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ResearchOpportunity() {
    }

    public ResearchOpportunity(UUID id, String sourceId, String title, String institution, String description,
                               List<String> normalizedTechnologies, List<String> normalizedAreas,
                               String modality, LocalDate deadline, String contentHash) {
        this.id = id != null ? id : UUID.randomUUID();
        this.sourceId = sourceId;
        this.title = title;
        this.institution = institution;
        this.description = description;
        this.normalizedTechnologies = normalizedTechnologies != null ? new ArrayList<>(normalizedTechnologies) : new ArrayList<>();
        this.normalizedAreas = normalizedAreas != null ? new ArrayList<>(normalizedAreas) : new ArrayList<>();
        this.modality = modality;
        this.deadline = deadline;
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

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getNormalizedTechnologies() {
        return normalizedTechnologies;
    }

    public void setNormalizedTechnologies(List<String> normalizedTechnologies) {
        this.normalizedTechnologies = normalizedTechnologies;
    }

    public List<String> getNormalizedAreas() {
        return normalizedAreas;
    }

    public void setNormalizedAreas(List<String> normalizedAreas) {
        this.normalizedAreas = normalizedAreas;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
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
