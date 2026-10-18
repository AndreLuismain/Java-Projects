package br.com.andreluismain.documentador.repository;

import br.com.andreluismain.documentador.domain.model.GeneratedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para armazenamento e consulta dos documentos técnicos gerados.
 */
@Repository
public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, UUID> {

    Optional<GeneratedDocument> findByAnalysisId(UUID analysisId);

    Optional<GeneratedDocument> findFirstByContentHashAndPromptVersion(String contentHash, String promptVersion);
}
