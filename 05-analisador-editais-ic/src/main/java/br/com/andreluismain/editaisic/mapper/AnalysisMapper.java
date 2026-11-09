package br.com.andreluismain.editaisic.mapper;

import br.com.andreluismain.editaisic.domain.model.Analysis;
import br.com.andreluismain.editaisic.dto.response.AnalysisResponse;
import br.com.andreluismain.editaisic.dto.response.RequirementDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversor para a entidade {@link Analysis}, manipulando campos estruturados serializados em JSON.
 */
@Component
public class AnalysisMapper {

    private static final Logger log = LoggerFactory.getLogger(AnalysisMapper.class);

    private final ObjectMapper objectMapper;

    public AnalysisMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AnalysisResponse toResponse(Analysis entity) {
        List<RequirementDto> requirements = new ArrayList<>();
        if (entity.getRequirements() != null && !entity.getRequirements().isBlank()) {
            try {
                requirements = objectMapper.readValue(entity.getRequirements(), new TypeReference<List<RequirementDto>>() {});
            } catch (Exception e) {
                log.warn("Erro ao desserializar requisitos da análise {}: {}", entity.getId(), e.getMessage());
            }
        }

        List<String> risks = new ArrayList<>();
        if (entity.getRisks() != null && !entity.getRisks().isBlank()) {
            try {
                risks = objectMapper.readValue(entity.getRisks(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                log.warn("Erro ao desserializar riscos da análise {}: {}", entity.getId(), e.getMessage());
            }
        }

        return AnalysisResponse.builder()
                .id(entity.getId())
                .opportunityId(entity.getOpportunity().getId())
                .profileId(entity.getProfile().getId())
                .summary(entity.getSummary())
                .matchScore(entity.getMatchScore())
                .classification(entity.getClassification())
                .requirements(requirements)
                .risks(risks)
                .modelName(entity.getModelName())
                .promptVersion(entity.getPromptVersion())
                .analyzedAt(entity.getAnalyzedAt())
                .build();
    }
}
