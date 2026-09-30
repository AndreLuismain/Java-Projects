package br.com.andreluismain.recomendacao.scoring;

import br.com.andreluismain.recomendacao.domain.model.ResearchOpportunity;
import br.com.andreluismain.recomendacao.domain.model.StudentProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicScorerTest {

    private DeterministicScorer scorer;

    @BeforeEach
    void setUp() {
        scorer = new DeterministicScorer();
    }

    @Test
    @DisplayName("Deve conceder score alto quando perfil tiver tecnologias e área coincidentes")
    void shouldScoreHighForMatchingProfile() {
        StudentProfile profile = new StudentProfile(
                UUID.randomUUID(), 5, List.of("java", "spring"), List.of("ia"), "Interesse em IA", 12
        );

        ResearchOpportunity opp = new ResearchOpportunity(
                UUID.randomUUID(), "src-1", "Pesquisa em IA com Spring", "USP", "Descrição da vaga",
                List.of("java", "spring"), List.of("ia"), "REMOTO", LocalDate.now().plusMonths(2), "hash1"
        );

        DeterministicScorer.DeterministicResult result = scorer.score(profile, opp);

        assertNotNull(result);
        assertTrue(result.score() >= 85.0);
        assertFalse(result.reasons().isEmpty());
    }

    @Test
    @DisplayName("Deve aplicar penalidade para vagas com prazo expirado")
    void shouldApplyPenaltyForExpiredDeadline() {
        StudentProfile profile = new StudentProfile(
                UUID.randomUUID(), 4, List.of("java"), List.of("ia"), "Interesse", 10
        );

        ResearchOpportunity expiredOpp = new ResearchOpportunity(
                UUID.randomUUID(), "src-exp", "Pesquisa Expirada", "UFMG", "Descrição",
                List.of("java"), List.of("ia"), "PRESENCIAL", LocalDate.now().minusDays(5), "hash-exp"
        );

        DeterministicScorer.DeterministicResult result = scorer.score(profile, expiredOpp);

        assertTrue(result.reasons().stream().anyMatch(r -> r.contains("Penalidade")));
    }
}
