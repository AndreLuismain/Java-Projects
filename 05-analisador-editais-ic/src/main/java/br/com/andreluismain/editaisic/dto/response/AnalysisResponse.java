package br.com.andreluismain.editaisic.dto.response;

import br.com.andreluismain.editaisic.domain.model.MatchClassification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO contendo o resultado completo da análise semântica de uma oportunidade.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {

    private UUID id;
    private UUID opportunityId;
    private UUID profileId;
    private String summary;
    private BigDecimal matchScore;
    private MatchClassification classification;
    private List<RequirementDto> requirements;
    private List<String> risks;
    private String modelName;
    private String promptVersion;
    private Instant analyzedAt;
}
