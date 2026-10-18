package br.com.andreluismain.recomendacao.scoring;

import br.com.andreluismain.recomendacao.domain.model.Recommendation;
import br.com.andreluismain.recomendacao.domain.model.ResearchOpportunity;
import br.com.andreluismain.recomendacao.domain.model.StudentProfile;
import br.com.andreluismain.recomendacao.integration.llm.LlmSemanticScorer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

/**
 * Ranqueador que combina scores determinísticos e semânticos, aplicando resolução determinística de empates.
 */
@Component
public class RecommendationRanker {

    private final DeterministicScorer deterministicScorer;
    private final LlmSemanticScorer semanticScorer;

    @Value("${recomendacao.deterministic-weight:0.70}")
    private double deterministicWeight = 0.70;

    @Value("${recomendacao.semantic-weight:0.30}")
    private double semanticWeight = 0.30;

    public RecommendationRanker(DeterministicScorer deterministicScorer, LlmSemanticScorer semanticScorer) {
        this.deterministicScorer = deterministicScorer;
        this.semanticScorer = semanticScorer;
    }

    public record CandidateRanking(
            ResearchOpportunity opportunity,
            double deterministicScore,
            Double semanticScore,
            double finalScore,
            List<String> reasons
    ) {}

    /**
     * Calcula scores e ordena candidatos com desempate determinístico.
     */
    public List<Recommendation> rank(UUID runId, StudentProfile profile, List<ResearchOpportunity> opportunities, boolean useLlm) {
        List<CandidateRanking> candidates = new ArrayList<>();

        for (ResearchOpportunity opp : opportunities) {
            DeterministicScorer.DeterministicResult det = deterministicScorer.score(profile, opp);
            List<String> combinedReasons = new ArrayList<>(det.reasons());

            Double semScore = null;
            double finalScore;

            if (useLlm) {
                try {
                    LlmSemanticScorer.SemanticResult sem = semanticScorer.scoreSemantics(profile, opp);
                    semScore = sem.score();
                    finalScore = (det.score() * deterministicWeight) + (semScore * semanticWeight);
                    combinedReasons.add("Score semântico LLM: " + sem.explanation());
                } catch (Exception e) {
                    finalScore = det.score();
                    combinedReasons.add("Aviso: Falha na análise semântica; aplicando score 100% determinístico.");
                }
            } else {
                finalScore = det.score();
            }

            finalScore = Math.min(100.0, Math.max(0.0, Math.round(finalScore * 10.0) / 10.0));
            candidates.add(new CandidateRanking(opp, det.score(), semScore, finalScore, combinedReasons));
        }

        // Ordenação: 1º finalScore DESC, 2º prazo mais próximo (ASC), 3º título normalizado (ASC)
        candidates.sort((c1, c2) -> {
            int scoreCmp = Double.compare(c2.finalScore(), c1.finalScore());
            if (scoreCmp != 0) return scoreCmp;

            LocalDate d1 = c1.opportunity().getDeadline();
            LocalDate d2 = c2.opportunity().getDeadline();
            if (d1 != null && d2 != null) {
                int dateCmp = d1.compareTo(d2);
                if (dateCmp != 0) return dateCmp;
            } else if (d1 != null) {
                return -1;
            } else if (d2 != null) {
                return 1;
            }

            return c1.opportunity().getTitle().compareToIgnoreCase(c2.opportunity().getTitle());
        });

        List<Recommendation> recommendations = new ArrayList<>();
        int rank = 1;
        for (CandidateRanking c : candidates) {
            recommendations.add(new Recommendation(
                    UUID.randomUUID(),
                    runId,
                    c.opportunity().getId(),
                    c.deterministicScore(),
                    c.semanticScore(),
                    c.finalScore(),
                    c.reasons(),
                    rank++
            ));
        }

        return recommendations;
    }

    public void setDeterministicWeight(double deterministicWeight) {
        this.deterministicWeight = deterministicWeight;
    }

    public void setSemanticWeight(double semanticWeight) {
        this.semanticWeight = semanticWeight;
    }
}
