package br.com.andreluismain.editaisic.dto.response;

import br.com.andreluismain.editaisic.domain.model.OpportunityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO resumido para listagem paginada de oportunidades.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityResponse {

    private UUID id;
    private String sourceUrl;
    private String sourceName;
    private String title;
    private String institution;
    private Instant publishedAt;
    private Instant deadlineAt;
    private OpportunityStatus status;
    private Instant createdAt;
}
