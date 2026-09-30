package br.com.andreluismain.documentador.dto.response;

import br.com.andreluismain.documentador.domain.model.Analysis;
import br.com.andreluismain.documentador.domain.model.AnalysisStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Resposta com metadados e status da análise de código legado.
 */
public record AnalysisResponse(
        UUID id,
        String fileName,
        String language,
        Long sizeBytes,
        String contentHash,
        AnalysisStatus status,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {
    public static AnalysisResponse from(Analysis analysis) {
        return new AnalysisResponse(
                analysis.getId(),
                analysis.getFileName(),
                analysis.getLanguage(),
                analysis.getSizeBytes(),
                analysis.getContentHash(),
                analysis.getStatus(),
                analysis.getCreatedAt(),
                analysis.getCompletedAt()
        );
    }
}
