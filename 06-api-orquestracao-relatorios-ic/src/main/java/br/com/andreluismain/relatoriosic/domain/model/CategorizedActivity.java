package br.com.andreluismain.relatoriosic.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Atividade categorizada e mensurada extraída dos logs pelo agente LLM.
 */
@Entity
@Table(name = "categorized_activities")
public class CategorizedActivity {

    @Id
    private UUID id;

    @Column(name = "weekly_log_id", nullable = false)
    private UUID weeklyLogId;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityCategory category;

    @Column(name = "hours_spent", nullable = false)
    private Integer hoursSpent;

    @Column(length = 300)
    private String evidence;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CategorizedActivity() {
    }

    public CategorizedActivity(UUID id, UUID weeklyLogId, String description, ActivityCategory category, Integer hoursSpent, String evidence) {
        this.id = id != null ? id : UUID.randomUUID();
        this.weeklyLogId = weeklyLogId;
        this.description = description;
        this.category = category;
        this.hoursSpent = hoursSpent;
        this.evidence = evidence;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getWeeklyLogId() {
        return weeklyLogId;
    }

    public void setWeeklyLogId(UUID weeklyLogId) {
        this.weeklyLogId = weeklyLogId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivityCategory getCategory() {
        return category;
    }

    public void setCategory(ActivityCategory category) {
        this.category = category;
    }

    public Integer getHoursSpent() {
        return hoursSpent;
    }

    public void setHoursSpent(Integer hoursSpent) {
        this.hoursSpent = hoursSpent;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
