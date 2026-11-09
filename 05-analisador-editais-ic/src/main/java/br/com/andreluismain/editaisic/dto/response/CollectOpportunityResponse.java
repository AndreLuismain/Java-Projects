package br.com.andreluismain.editaisic.dto.response;

import br.com.andreluismain.editaisic.domain.model.OpportunityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Resposta de sucesso ao submeter e coletar uma oportunidade.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectOpportunityResponse {

    private UUID id;
    private OpportunityStatus status;
    private String title;
    private String sourceUrl;
    private Instant createdAt;
}
