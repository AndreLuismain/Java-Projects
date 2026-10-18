package br.com.andreluismain.documentador.repository;

import br.com.andreluismain.documentador.domain.model.Analysis;
import br.com.andreluismain.documentador.domain.model.AnalysisStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para persistência e consulta do ciclo de vida das análises.
 */
@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    Optional<Analysis> findFirstByContentHashAndStatusOrderByCreatedAtDesc(String contentHash, AnalysisStatus status);

    @Query("SELECT a FROM Analysis a WHERE " +
           "(:language IS NULL OR LOWER(a.language) = LOWER(:language)) AND " +
           "(:status IS NULL OR a.status = :status) " +
           "ORDER BY a.createdAt DESC")
    Page<Analysis> findByFilter(@Param("language") String language,
                               @Param("status") AnalysisStatus status,
                               Pageable pageable);
}
