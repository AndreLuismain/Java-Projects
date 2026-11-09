package br.com.andreluismain.editaisic.domain.repository;

import br.com.andreluismain.editaisic.domain.model.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para gerenciamento e consulta de oportunidades e editais.
 */
@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID>, JpaSpecificationExecutor<Opportunity> {

    /**
     * Busca uma oportunidade pela URL canônica de origem.
     *
     * @param sourceUrl URL da oportunidade
     * @return Optional contendo a oportunidade, se encontrada
     */
    Optional<Opportunity> findBySourceUrl(String sourceUrl);

    /**
     * Verifica se já existe oportunidade cadastrada com a URL informada.
     *
     * @param sourceUrl URL a verificar
     * @return true se já existir
     */
    boolean existsBySourceUrl(String sourceUrl);

    /**
     * Busca oportunidade pelo hash de conteúdo textual para deduplicação.
     *
     * @param contentHash hash SHA-256 do conteúdo
     * @return Optional contendo a oportunidade
     */
    Optional<Opportunity> findByContentHash(String contentHash);
}
