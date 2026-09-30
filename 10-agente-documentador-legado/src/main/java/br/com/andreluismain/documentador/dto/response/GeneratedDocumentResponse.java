package br.com.andreluismain.documentador.dto.response;

import br.com.andreluismain.documentador.domain.model.GeneratedDocument;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resposta com metadados do documento técnico gerado.
 */
public record GeneratedDocumentResponse(
        UUID id,
        UUID analysisId,
        String markdown,
        String modelName,
        String promptVersion,
        String contentHash,
        LocalDateTime createdAt
) {
    public static GeneratedDocumentResponse from(GeneratedDocument doc) {
        return new GeneratedDocumentResponse(
                doc.getId(),
                doc.getAnalysisId(),
                doc.getMarkdown(),
                doc.getModelName(),
                doc.getPromptVersion(),
                doc.getContentHash(),
                doc.getCreatedAt()
        );
    }
}
