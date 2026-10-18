package br.com.andreluismain.recomendacao.repository;

import br.com.andreluismain.recomendacao.domain.model.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório para consulta paginada dos itens rankeados de recomendações.
 */
@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {

    List<Recommendation> findByRunIdOrderByRankAsc(UUID runId);

    @Query("SELECT r FROM Recommendation r WHERE r.runId = :runId AND (:minScore IS NULL OR r.finalScore >= :minScore) ORDER BY r.rank ASC")
    Page<Recommendation> findByRunIdWithFilter(@Param("runId") UUID runId,
                                               @Param("minScore") Double minScore,
                                               Pageable pageable);
}
