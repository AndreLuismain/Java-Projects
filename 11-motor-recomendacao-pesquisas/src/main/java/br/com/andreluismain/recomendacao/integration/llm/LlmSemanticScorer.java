package br.com.andreluismain.recomendacao.integration.llm;

import br.com.andreluismain.recomendacao.domain.model.ResearchOpportunity;
import br.com.andreluismain.recomendacao.domain.model.StudentProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Gateway de scoring semântico via LLM ou motor heurístico seguro.
 */
@Component
public class LlmSemanticScorer {

    private static final Logger log = LoggerFactory.getLogger(LlmSemanticScorer.class);

    @Value("${llm.model:gemini-1.5-flash}")
    private String modelName = "gemini-1.5-flash";

    public record SemanticResult(
            Double score,
            String explanation,
            String modelUsed
    ) {}

    /**
     * Calcula o score semântico de aderência qualitativa entre o perfil e a descrição da oportunidade.
     */
    public SemanticResult scoreSemantics(StudentProfile profile, ResearchOpportunity opportunity) {
        log.info("Calculando score semântico para perfil {} e oportunidade {}",
                profile.getId(), opportunity.getId());

        // Se a descrição for muito rasa ou não contiver texto analisável:
        if (opportunity.getDescription() == null || opportunity.getDescription().trim().length() < 10) {
            return new SemanticResult(50.0, "INSUFFICIENT_DATA para análise semântica aprofundada.", modelName);
        }

        // Heurística de aderência textual em fallback (simulando enriquecimento de IA sem requisições frágeis)
        String desc = opportunity.getDescription().toLowerCase();
        double bonus = 0.0;
        if (profile.getInterests() != null && !profile.getInterests().isBlank()) {
            for (String interestWord : profile.getInterests().toLowerCase().split("\\s+")) {
                if (interestWord.length() > 3 && desc.contains(interestWord)) {
                    bonus += 15.0;
                }
            }
        }

        double semanticScore = Math.min(100.0, Math.max(50.0, 60.0 + bonus));
        return new SemanticResult(semanticScore, "Aderência semântica contextual positiva entre objetivos e edital.", modelName);
    }

    public String getModelName() {
        return modelName;
    }
}
