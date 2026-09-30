package br.com.andreluismain.recomendacao.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Perfil acadêmico do estudante candidato à Iniciação Científica.
 */
@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    private UUID id;

    @Column(name = "semester", nullable = false)
    private Integer semester;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_technologies", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "technology")
    private List<String> technologies = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_research_areas", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "research_area")
    private List<String> researchAreas = new ArrayList<>();

    @Column(name = "interests", length = 1000)
    private String interests;

    @Column(name = "availability_hours")
    private Integer availabilityHours;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public StudentProfile() {
    }

    public StudentProfile(UUID id, Integer semester, List<String> technologies, List<String> researchAreas,
                          String interests, Integer availabilityHours) {
        this.id = id != null ? id : UUID.randomUUID();
        this.semester = semester;
        this.technologies = technologies != null ? new ArrayList<>(technologies) : new ArrayList<>();
        this.researchAreas = researchAreas != null ? new ArrayList<>(researchAreas) : new ArrayList<>();
        this.interests = interests;
        this.availabilityHours = availabilityHours;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public List<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<String> technologies) {
        this.technologies = technologies;
    }

    public List<String> getResearchAreas() {
        return researchAreas;
    }

    public void setResearchAreas(List<String> researchAreas) {
        this.researchAreas = researchAreas;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public Integer getAvailabilityHours() {
        return availabilityHours;
    }

    public void setAvailabilityHours(Integer availabilityHours) {
        this.availabilityHours = availabilityHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
