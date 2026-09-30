package br.com.andreluismain.recomendacao.scoring;

import br.com.andreluismain.recomendacao.domain.model.Recommendation;
import br.com.andreluismain.recomendacao.domain.model.ResearchOpportunity;
import br.com.andreluismain.recomendacao.domain.model.StudentProfile;
import br.com.andreluismain.recomendacao.integration.llm.LlmSemanticScorer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationRankerTest {

    private RecommendationRanker ranker;

    @BeforeEach
    void setUp() {
        DeterministicScorer deterministicScorer = new DeterministicScorer();
        LlmSemanticScorer semanticScorer = new LlmSemanticScorer();
        ranker = new RecommendationRanker(deterministicScorer, semanticScorer);
        ranker.setDeterministicWeight(0.70);
        ranker.setSemanticWeight(0.30);
    }

    @Test
    @DisplayName("Deve ranquear por finalScore decrescente e atribuir posições sequenciais")
    void shouldRankByFinalScoreDescending() {
        StudentProfile profile = new StudentProfile(
                UUID.randomUUID(), 5, List.of("java"), List.of("ia"), "Interesse em IA", 10
        );

        ResearchOpportunity opp1 = new ResearchOpportunity(
                UUID.randomUUID(), "1", "Vaga Aderente Completa", "USP", "Descrição",
                List.of("java"), List.of("ia"), "REMOTO", LocalDate.now().plusMonths(3), "h1"
        );

        ResearchOpportunity opp2 = new ResearchOpportunity(
                UUID.randomUUID(), "2", "Vaga Menos Aderente", "Unicamp", "Descrição",
                List.of("python"), List.of("quimica"), "PRESENCIAL", LocalDate.now().plusMonths(3), "h2"
        );

        UUID runId = UUID.randomUUID();
        List<Recommendation> recommendations = ranker.rank(runId, profile, List.of(opp2, opp1), false);

        assertEquals(2, recommendations.size());
        assertEquals(1, recommendations.get(0).getRank());
        assertEquals(2, recommendations.get(1).getRank());
        assertEquals(opp1.getId(), recommendations.get(0).getOpportunityId());
        assertTrue(recommendations.get(0).getFinalScore() > recommendations.get(1).getFinalScore());
    }

    @Test
    @DisplayName("Deve desempatar oportunidades de mesmo score pelo prazo mais próximo")
    void shouldTieBreakByNearestDeadline() {
        StudentProfile profile = new StudentProfile(
                UUID.randomUUID(), 5, List.of("java"), List.of("ia"), "Interesse", 10
        );

        LocalDate earlier = LocalDate.now().plusDays(10);
        LocalDate later = LocalDate.now().plusDays(30);

        ResearchOpportunity oppLater = new ResearchOpportunity(
                UUID.randomUUID(), "1", "Vaga Prazo Distante", "USP", "Descrição",
                List.of("java"), List.of("ia"), "REMOTO", later, "h1"
        );

        ResearchOpportunity oppEarlier = new ResearchOpportunity(
                UUID.randomUUID(), "2", "Vaga Prazo Próximo", "USP", "Descrição",
                List.of("java"), List.of("ia"), "REMOTO", earlier, "h2"
        );

        UUID runId = UUID.randomUUID();
        List<Recommendation> recommendations = ranker.rank(runId, profile, List.of(oppLater, oppEarlier), false);

        assertEquals(1, recommendations.get(0).getRank());
        assertEquals(oppEarlier.getId(), recommendations.get(0).getOpportunityId());
    }
}
