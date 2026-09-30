package br.com.andreluismain.relatoriosic.dto.response;

import br.com.andreluismain.relatoriosic.domain.model.ActivityCategory;
import br.com.andreluismain.relatoriosic.domain.model.CategorizedActivity;

import java.util.UUID;

/**
 * Resposta com os detalhes de uma atividade classificada.
 */
public record CategorizedActivityResponse(
        UUID id,
        UUID weeklyLogId,
        String description,
        ActivityCategory category,
        Integer hoursSpent,
        String evidence
) {
    public static CategorizedActivityResponse from(CategorizedActivity act) {
        return new CategorizedActivityResponse(
                act.getId(),
                act.getWeeklyLogId(),
                act.getDescription(),
                act.getCategory(),
                act.getHoursSpent(),
                act.getEvidence()
        );
    }
}
