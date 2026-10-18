package br.com.andreluismain.documentador.dto.response;

import br.com.andreluismain.documentador.domain.model.AnalysisStatus;
import java.util.UUID;

/**
 * Resposta de aceite imediato para processamento de análise.
 */
public record AnalysisInitiatedResponse(
        UUID id,
        AnalysisStatus status
) {}
