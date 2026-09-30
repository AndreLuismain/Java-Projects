package br.com.andreluismain.relatoriosic.domain.repository;

import br.com.andreluismain.relatoriosic.domain.model.ResearchProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório para persistência de projetos de Iniciação Científica.
 */
@Repository
public interface ResearchProjectRepository extends JpaRepository<ResearchProject, UUID> {
}
