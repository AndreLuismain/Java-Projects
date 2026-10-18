package br.com.andreluismain.recomendacao.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Payload de requisição para execução da esteira de recomendação.
 */
public record RunRecommendationRequest(
        @NotNull(message = "Identificador do perfil é obrigatório")
        UUID profileId,

        List<Map<String, Object>> opportunities,

        Boolean useLlm
) {
    public boolean isUseLlm() {
        return useLlm != null && useLlm;
    }
}
