package br.com.andreluismain.recomendacao.repository;

import br.com.andreluismain.recomendacao.domain.model.RecommendationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório para o ciclo de vida das execuções de recomendação.
 */
@Repository
public interface RecommendationRunRepository extends JpaRepository<RecommendationRun, UUID> {
}
