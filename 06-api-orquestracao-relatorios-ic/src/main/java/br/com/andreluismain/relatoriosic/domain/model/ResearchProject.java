package br.com.andreluismain.relatoriosic.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de projeto acadêmico de Iniciação Científica (IC).
 */
@Entity
@Table(name = "research_projects")
public class ResearchProject {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "student_name", nullable = false, length = 150)
    private String studentName;

    @Column(name = "advisor_name", nullable = false, length = 150)
    private String advisorName;

    @Column(name = "grant_agency", length = 100)
    private String grantAgency;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ResearchProject() {
    }

    public ResearchProject(UUID id, String title, String studentName, String advisorName,
                           String grantAgency, LocalDate startDate, LocalDate endDate) {
        this.id = id != null ? id : UUID.randomUUID();
        this.title = title;
        this.studentName = studentName;
        this.advisorName = advisorName;
        this.grantAgency = grantAgency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }

    public String getGrantAgency() {
        return grantAgency;
    }

    public void setGrantAgency(String grantAgency) {
        this.grantAgency = grantAgency;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
