package br.com.andreluismain.editaisic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Payload para requisição de análise de oportunidade contra um perfil acadêmico.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeOpportunityRequest {

    @NotNull(message = "O ID do perfil (profileId) é obrigatório")
    private UUID profileId;
}
