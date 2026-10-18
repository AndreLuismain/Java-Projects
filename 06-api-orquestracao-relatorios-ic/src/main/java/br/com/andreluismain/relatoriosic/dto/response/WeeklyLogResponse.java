package br.com.andreluismain.relatoriosic.dto.response;

import br.com.andreluismain.relatoriosic.domain.model.WeeklyLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Resposta com dados do log semanal e suas atividades extraídas.
 */
public record WeeklyLogResponse(
        UUID id,
        UUID projectId,
        Integer weekNumber,
        LocalDate startDate,
        LocalDate endDate,
        String rawNotes,
        List<CategorizedActivityResponse> activities,
        LocalDateTime createdAt
) {
    public static WeeklyLogResponse of(WeeklyLog log, List<CategorizedActivityResponse> activities) {
        return new WeeklyLogResponse(
                log.getId(),
                log.getProjectId(),
                log.getWeekNumber(),
                log.getStartDate(),
                log.getEndDate(),
                log.getRawNotes(),
                activities,
                log.getCreatedAt()
        );
    }
}
