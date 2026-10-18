package br.com.andreluismain.recomendacao.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * Resposta completa contendo o ranking de recomendações ordenado.
 */
public record RecommendationResponse(
        UUID runId,
        List<RecommendationItemResponse> items
) {}
