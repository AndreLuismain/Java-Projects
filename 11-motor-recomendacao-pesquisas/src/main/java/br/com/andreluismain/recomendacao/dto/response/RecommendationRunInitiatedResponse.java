package br.com.andreluismain.recomendacao.dto.response;

import br.com.andreluismain.recomendacao.domain.model.RunStatus;
import java.util.UUID;

/**
 * Resposta de início do processamento de recomendação.
 */
public record RecommendationRunInitiatedResponse(
        UUID runId,
        RunStatus status
) {}
