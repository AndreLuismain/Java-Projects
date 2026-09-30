package br.com.andreluismain.recomendacao.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * Item individual classificado no ranking de recomendações.
 */
public record RecommendationItemResponse(
        int rank,
        UUID opportunityId,
        String title,
        Double finalScore,
        List<String> reasons
) {}
