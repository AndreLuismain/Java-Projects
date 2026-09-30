package br.com.andreluismain.documentador.repository;

import br.com.andreluismain.documentador.domain.model.AnalysisError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório de auditoria para erros em execuções de análise.
 */
@Repository
public interface AnalysisErrorRepository extends JpaRepository<AnalysisError, UUID> {

    Optional<AnalysisError> findByAnalysisId(UUID analysisId);
}
