package br.com.andreluismain.relatoriosic.report;

import br.com.andreluismain.relatoriosic.domain.model.ActivityCategory;
import br.com.andreluismain.relatoriosic.domain.model.CategorizedActivity;
import br.com.andreluismain.relatoriosic.domain.model.ResearchProject;
import br.com.andreluismain.relatoriosic.domain.model.WeeklyLog;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Gerador de relatórios técnicos em formato Markdown para Iniciação Científica.
 */
@Component
public class MarkdownReportGenerator {

    public String generateReport(ResearchProject project,
                                 List<WeeklyLog> logs,
                                 Map<UUID, List<CategorizedActivity>> activitiesByLog) {

        StringBuilder md = new StringBuilder();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        md.append("# Relatório de Iniciação Científica: ").append(project.getTitle()).append("\n\n");
        md.append("> Gerado automaticamente pela API de Orquestração em ").append(now).append("\n\n");

        md.append("## 1. Dados do Projeto\n");
        md.append("- **Título:** ").append(project.getTitle()).append("\n");
        md.append("- **Bolsista/Estudante:** ").append(project.getStudentName()).append("\n");
        md.append("- **Orientador(a):** ").append(project.getAdvisorName()).append("\n");
        if (project.getGrantAgency() != null && !project.getGrantAgency().isBlank()) {
            md.append("- **Agência de Fomento:** ").append(project.getGrantAgency()).append("\n");
        }
        md.append("- **Período:** ").append(project.getStartDate()).append(" a ").append(project.getEndDate()).append("\n\n");

        // Cálculo de horas totais por categoria
        Map<ActivityCategory, Integer> hoursByCategory = new EnumMap<>(ActivityCategory.class);
        int totalHours = 0;

        for (List<CategorizedActivity> acts : activitiesByLog.values()) {
            for (CategorizedActivity act : acts) {
                hoursByCategory.merge(act.getCategory(), act.getHoursSpent(), Integer::sum);
                totalHours += act.getHoursSpent();
            }
        }

        md.append("## 2. Resumo da Carga Horária\n");
        md.append("- **Total de Horas Dedicadas:** ").append(totalHours).append(" horas\n\n");
        md.append("| Categoria | Horas | % do Total |\n");
        md.append("|---|---|---|\n");
        for (ActivityCategory cat : ActivityCategory.values()) {
            int h = hoursByCategory.getOrDefault(cat, 0);
            double pct = totalHours > 0 ? (h * 100.0) / totalHours : 0.0;
            md.append(String.format(Locale.ROOT, "| %s | %d h | %.1f%% |\n", cat.name(), h, pct));
        }
        md.append("\n");

        md.append("## 3. Detalhamento Semanal das Atividades\n\n");
        if (logs.isEmpty()) {
            md.append("*Nenhum registro semanal encontrado para este projeto.*\n");
        } else {
            for (WeeklyLog log : logs) {
                md.append("### Semana ").append(log.getWeekNumber())
                        .append(" (").append(log.getStartDate()).append(" a ").append(log.getEndDate()).append(")\n\n");

                List<CategorizedActivity> acts = activitiesByLog.getOrDefault(log.getId(), Collections.emptyList());
                if (acts.isEmpty()) {
                    md.append("- *Sem atividades estruturadas cadastradas nesta semana.*\n\n");
                } else {
                    for (CategorizedActivity act : acts) {
                        md.append(String.format("- `[%s]` **%s** (%d h)", act.getCategory(), act.getDescription(), act.getHoursSpent()));
                        if (act.getEvidence() != null && !act.getEvidence().isBlank()) {
                            md.append(" - *Evidência:* ").append(act.getEvidence());
                        }
                        md.append("\n");
                    }
                    md.append("\n");
                }
            }
        }

        md.append("## 4. Próximos Passos e Considerações\n");
        md.append("O projeto mantém progresso alinhado aos objetivos do plano de trabalho de Iniciação Científica.\n");

        return md.toString();
    }
}
