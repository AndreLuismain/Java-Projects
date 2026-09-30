package br.com.andreluismain.recomendacao.scoring;

import br.com.andreluismain.recomendacao.domain.model.ResearchOpportunity;
import br.com.andreluismain.recomendacao.domain.model.StudentProfile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

/**
 * Calculador determinístico de aderência entre perfil de estudante e oportunidade de pesquisa.
 * Representa pelo menos 60% da pontuação total de forma auditável e sem dependência de LLM.
 */
@Component
public class DeterministicScorer {

    public record DeterministicResult(
            double score,
            List<String> reasons
    ) {}

    /**
     * Calcula o score determinístico (0 a 100) e os motivos explicativos da compatibilidade.
     */
    public DeterministicResult score(StudentProfile profile, ResearchOpportunity opportunity) {
        double score = 0.0;
        List<String> reasons = new ArrayList<>();

        // 1. Compatibilidade de Tecnologias (até 40 pontos)
        List<String> studentTechs = profile.getTechnologies() != null ?
                profile.getTechnologies().stream().map(String::toLowerCase).toList() : Collections.emptyList();
        List<String> oppTechs = opportunity.getNormalizedTechnologies() != null ?
                opportunity.getNormalizedTechnologies().stream().map(String::toLowerCase).toList() : Collections.emptyList();

        if (!oppTechs.isEmpty()) {
            long matches = oppTechs.stream().filter(studentTechs::contains).count();
            if (matches > 0) {
                double techPoints = Math.min(40.0, ((double) matches / oppTechs.size()) * 40.0);
                score += techPoints;
                reasons.add(String.format("Compatibilidade técnica: %d de %d tecnologia(s) em comum (+%.1f pts)",
                        matches, oppTechs.size(), techPoints));
            }
        } else {
            score += 20.0; // Sem exigência técnica restritiva
            reasons.add("Vaga sem restrições específicas de tecnologia (+20.0 pts)");
        }

        // 2. Compatibilidade de Áreas de Pesquisa (até 30 pontos)
        List<String> studentAreas = profile.getResearchAreas() != null ?
                profile.getResearchAreas().stream().map(String::toLowerCase).toList() : Collections.emptyList();
        List<String> oppAreas = opportunity.getNormalizedAreas() != null ?
                opportunity.getNormalizedAreas().stream().map(String::toLowerCase).toList() : Collections.emptyList();

        if (!oppAreas.isEmpty()) {
            long areaMatches = oppAreas.stream().filter(studentAreas::contains).count();
            if (areaMatches > 0) {
                double areaPoints = Math.min(30.0, ((double) areaMatches / oppAreas.size()) * 30.0);
                score += areaPoints;
                reasons.add(String.format("Área de pesquisa aderente: %d área(s) coincidente(s) (+%.1f pts)",
                        areaMatches, areaPoints));
            }
        } else {
            score += 15.0;
            reasons.add("Vaga multidisciplinar sem área restritiva (+15.0 pts)");
        }

        // 3. Compatibilidade de Semestre (até 15 pontos)
        int sem = profile.getSemester() != null ? profile.getSemester() : 1;
        if (sem >= 3 && sem <= 8) {
            score += 15.0;
            reasons.add("Semestre ideal para Iniciação Científica (3º ao 8º) (+15.0 pts)");
        } else {
            score += 8.0;
            reasons.add("Semestre inicial ou final de graduação (+8.0 pts)");
        }

        // 4. Disponibilidade de Horas (até 15 pontos)
        int hours = profile.getAvailabilityHours() != null ? profile.getAvailabilityHours() : 0;
        if (hours >= 10) {
            score += 15.0;
            reasons.add("Disponibilidade de carga horária excelente (+15.0 pts)");
        } else if (hours >= 5) {
            score += 10.0;
            reasons.add("Disponibilidade de carga horária moderada (+10.0 pts)");
        }

        // 5. Verificação de Prazo
        if (opportunity.getDeadline() != null) {
            if (opportunity.getDeadline().isBefore(LocalDate.now())) {
                score = Math.max(0.0, score - 25.0);
                reasons.add("Penalidade: prazo limite da vaga expirado (-25.0 pts)");
            } else {
                reasons.add("Vaga com inscrições ativas dentro do prazo");
            }
        }

        double finalScore = Math.min(100.0, Math.max(0.0, Math.round(score * 10.0) / 10.0));
        return new DeterministicResult(finalScore, reasons);
    }
}
