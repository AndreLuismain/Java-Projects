package br.com.andreluismain.editaisic.domain.repository;

import br.com.andreluismain.editaisic.domain.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para persistência de análises semânticas de editais.
 */
@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    /**
     * Busca uma análise para a combinação única de oportunidade e perfil.
     *
     * @param opportunityId ID da oportunidade
     * @param profileId ID do perfil
     * @return Optional contendo a análise encontrada
     */
    Optional<Analysis> findByOpportunityIdAndProfileId(UUID opportunityId, UUID profileId);

    /**
     * Busca a primeira análise vinculada à oportunidade indicada.
     *
     * @param opportunityId ID da oportunidade
     * @return Optional com a análise
     */
    Optional<Analysis> findFirstByOpportunityIdOrderByAnalyzedAtDesc(UUID opportunityId);

    /**
     * Verifica se já existe análise persistida para a oportunidade e perfil.
     *
     * @param opportunityId ID da oportunidade
     * @param profileId ID do perfil
     * @return true se já existir
     */
    boolean existsByOpportunityIdAndProfileId(UUID opportunityId, UUID profileId);
}
