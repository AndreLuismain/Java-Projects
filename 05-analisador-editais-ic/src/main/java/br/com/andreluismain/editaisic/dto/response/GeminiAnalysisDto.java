package br.com.andreluismain.editaisic.dto.response;

import br.com.andreluismain.editaisic.domain.model.MatchClassification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o contrato JSON estrito esperado como resposta do modelo Gemini Pro.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiAnalysisDto {

    private String summary;
    private BigDecimal matchScore;
    private MatchClassification classification;

    @Builder.Default
    private List<RequirementDto> requirements = new ArrayList<>();

    private String deadline;

    @Builder.Default
    private List<String> risks = new ArrayList<>();
}
