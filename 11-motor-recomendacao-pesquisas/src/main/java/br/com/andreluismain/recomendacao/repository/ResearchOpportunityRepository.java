package br.com.andreluismain.recomendacao.repository;

import br.com.andreluismain.recomendacao.domain.model.ResearchOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para oportunidades de pesquisa acadêmica normalizadas.
 */
@Repository
public interface ResearchOpportunityRepository extends JpaRepository<ResearchOpportunity, UUID> {

    Optional<ResearchOpportunity> findByContentHash(String contentHash);
}
