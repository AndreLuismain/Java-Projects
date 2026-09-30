package br.com.andreluismain.recomendacao.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Payload de requisição para cadastro de perfil do estudante.
 */
public record CreateProfileRequest(
        @NotNull(message = "Semestre é obrigatório")
        @Min(value = 1, message = "Semestre mínimo é 1")
        @Max(value = 12, message = "Semestre máximo é 12")
        Integer semester,

        List<String> technologies,
        List<String> researchAreas,
        String interests,
        Integer availabilityHours
) {}
