package br.com.andreluismain.relatoriosic.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que armazena os registros semanais brutos de atividades de pesquisa.
 */
@Entity
@Table(name = "weekly_logs")
public class WeeklyLog {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Lob
    @Column(name = "raw_notes", nullable = false)
    private String rawNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public WeeklyLog() {
    }

    public WeeklyLog(UUID id, UUID projectId, Integer weekNumber, LocalDate startDate, LocalDate endDate, String rawNotes) {
        this.id = id != null ? id : UUID.randomUUID();
        this.projectId = projectId;
        this.weekNumber = weekNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rawNotes = rawNotes;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getRawNotes() {
        return rawNotes;
    }

    public void setRawNotes(String rawNotes) {
        this.rawNotes = rawNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
