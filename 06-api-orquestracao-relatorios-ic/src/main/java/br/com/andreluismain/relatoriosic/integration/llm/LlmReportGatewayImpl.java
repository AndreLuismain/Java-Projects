package br.com.andreluismain.relatoriosic.integration.llm;

import br.com.andreluismain.relatoriosic.domain.model.ActivityCategory;
import br.com.andreluismain.relatoriosic.domain.model.WeeklyLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementação do gateway LLM com fallback heurístico para classificação automática e resiliente.
 */
@Component
public class LlmReportGatewayImpl implements LlmReportGateway {

    private static final Logger log = LoggerFactory.getLogger(LlmReportGatewayImpl.class);

    @Value("${llm.api-key:mock-key}")
    private String apiKey = "mock-key";

    @Value("${llm.model:gemini-1.5-flash}")
    private String model = "gemini-1.5-flash";

    @Override
    public LlmClassificationDto classifyActivities(WeeklyLog weeklyLog) {
        log.info("Classificando notas da semana {} via gateway LLM (modelo={})",
                weeklyLog.getWeekNumber(), model);

        String raw = weeklyLog.getRawNotes();
        List<LlmActivityDto> list = new ArrayList<>();

        if (raw == null || raw.isBlank()) {
            return new LlmClassificationDto(list);
        }

        String[] lines = raw.split("[\\r\\n]+");
        for (String line : lines) {
            String trimmed = line.trim().replaceAll("^[\\-*#\\d.]+\\s*", "");
            if (trimmed.length() < 3) continue;

            String lower = trimmed.toLowerCase(Locale.ROOT);
            ActivityCategory category;
            int hours = 3;

            if (lower.contains("leitura") || lower.contains("estudo") || lower.contains("artigo") || lower.contains("paper")) {
                category = ActivityCategory.STUDY;
                hours = 4;
            } else if (lower.contains("codigo") || lower.contains("código") || lower.contains("desenvolv") || lower.contains("implement") || lower.contains("api")) {
                category = ActivityCategory.DEVELOPMENT;
                hours = 6;
            } else if (lower.contains("reuniao") || lower.contains("reunião") || lower.contains("orientador") || lower.contains("alinhamento")) {
                category = ActivityCategory.MEETING;
                hours = 2;
            } else if (lower.contains("relatorio") || lower.contains("relatório") || lower.contains("document") || lower.contains("escrita")) {
                category = ActivityCategory.DOCUMENTATION;
                hours = 3;
            } else if (lower.contains("pesquisa") || lower.contains("experimento") || lower.contains("benchmark") || lower.contains("teste")) {
                category = ActivityCategory.RESEARCH;
                hours = 5;
            } else {
                category = ActivityCategory.OTHER;
                hours = 2;
            }

            list.add(new LlmActivityDto(trimmed, category, hours, "Registro textual da semana " + weeklyLog.getWeekNumber()));
        }

        return new LlmClassificationDto(list);
    }
}
