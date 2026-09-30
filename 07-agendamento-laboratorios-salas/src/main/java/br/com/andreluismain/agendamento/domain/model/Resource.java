package br.com.andreluismain.agendamento.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entidade que representa um recurso acadêmico (laboratório, sala de estudo, auditório).
 */
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ResourceType type;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Boolean active;

    public Resource() {
    }

    public Resource(UUID id, String name, ResourceType type, Integer capacity, Boolean active) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.active = active != null ? active : true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private ResourceType type;
        private Integer capacity;
        private Boolean active = true;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(ResourceType type) {
            this.type = type;
            return this;
        }

        public Builder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Resource build() {
            return new Resource(id, name, type, capacity, active);
        }
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
