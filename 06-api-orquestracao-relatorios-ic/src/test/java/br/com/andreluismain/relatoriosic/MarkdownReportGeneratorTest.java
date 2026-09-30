package br.com.andreluismain.relatoriosic;

import br.com.andreluismain.relatoriosic.domain.model.ActivityCategory;
import br.com.andreluismain.relatoriosic.domain.model.CategorizedActivity;
import br.com.andreluismain.relatoriosic.domain.model.ResearchProject;
import br.com.andreluismain.relatoriosic.domain.model.WeeklyLog;
import br.com.andreluismain.relatoriosic.report.MarkdownReportGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownReportGeneratorTest {

    private final MarkdownReportGenerator generator = new MarkdownReportGenerator();

    @Test
    @DisplayName("Deve gerar relatório Markdown estruturado com dados do projeto e tabela de horas")
    void shouldGenerateStructuredMarkdownReport() {
        ResearchProject project = new ResearchProject(
                UUID.randomUUID(),
                "Pesquisa em Compiladores",
                "André Luís",
                "Prof. Orientador",
                "CNPq",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        UUID logId = UUID.randomUUID();
        WeeklyLog log = new WeeklyLog(
                logId,
                project.getId(),
                1,
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 1, 11),
                "Implementação da AST e leitura de artigos"
        );

        CategorizedActivity act1 = new CategorizedActivity(
                UUID.randomUUID(), logId, "Leitura de artigos sobre grafos", ActivityCategory.STUDY, 4, "Paper IEEE"
        );
        CategorizedActivity act2 = new CategorizedActivity(
                UUID.randomUUID(), logId, "Implementação da árvore sintática", ActivityCategory.DEVELOPMENT, 8, "PR #1"
        );

        String markdown = generator.generateReport(project, List.of(log), Map.of(logId, List.of(act1, act2)));

        assertNotNull(markdown);
        assertTrue(markdown.contains("# Relatório de Iniciação Científica: Pesquisa em Compiladores"));
        assertTrue(markdown.contains("André Luís"));
        assertTrue(markdown.contains("12 horas"));
        assertTrue(markdown.contains("Semana 1"));
        assertTrue(markdown.contains("STUDY"));
        assertTrue(markdown.contains("DEVELOPMENT"));
    }
}
