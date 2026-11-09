package br.com.andreluismain.editaisic.mapper;

import br.com.andreluismain.editaisic.domain.model.Opportunity;
import br.com.andreluismain.editaisic.dto.response.CollectOpportunityResponse;
import br.com.andreluismain.editaisic.dto.response.OpportunityDetailResponse;
import br.com.andreluismain.editaisic.dto.response.OpportunityResponse;
import org.springframework.stereotype.Component;

/**
 * Conversor entre a entidade {@link Opportunity} e seus DTOs de resposta.
 */
@Component
public class OpportunityMapper {

    public CollectOpportunityResponse toCollectResponse(Opportunity entity) {
        return CollectOpportunityResponse.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .title(entity.getTitle())
                .sourceUrl(entity.getSourceUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public OpportunityResponse toResponse(Opportunity entity) {
        return OpportunityResponse.builder()
                .id(entity.getId())
                .sourceUrl(entity.getSourceUrl())
                .sourceName(entity.getSourceName())
                .title(entity.getTitle())
                .institution(entity.getInstitution())
                .publishedAt(entity.getPublishedAt())
                .deadlineAt(entity.getDeadlineAt())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public OpportunityDetailResponse toDetailResponse(Opportunity entity) {
        return OpportunityDetailResponse.builder()
                .id(entity.getId())
                .sourceUrl(entity.getSourceUrl())
                .sourceName(entity.getSourceName())
                .title(entity.getTitle())
                .institution(entity.getInstitution())
                .publishedAt(entity.getPublishedAt())
                .deadlineAt(entity.getDeadlineAt())
                .rawContent(entity.getRawContent())
                .contentHash(entity.getContentHash())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
