package br.com.andreluismain.editaisic.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa o perfil acadêmico de um estudante ou pesquisador.
 *
 * <p>Contém informações de formação, interesses de pesquisa e palavras-chave
 * utilizadas como base de comparação na análise de editais.</p>
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Palavras-chave armazenadas como texto delimitado por vírgula para compatibilidade
     * universal entre H2 e PostgreSQL.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String keywords;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    /**
     * Retorna a lista de palavras-chave a partir da string delimitada.
     */
    public List<String> getKeywordsList() {
        if (keywords == null || keywords.isBlank()) {
            return new ArrayList<>();
        }
        return List.of(keywords.split("\\s*,\\s*"));
    }

    /**
     * Define as palavras-chave a partir de uma lista de termos.
     */
    public void setKeywordsList(List<String> list) {
        if (list == null || list.isEmpty()) {
            this.keywords = "";
        } else {
            this.keywords = String.join(", ", list);
        }
    }
}
