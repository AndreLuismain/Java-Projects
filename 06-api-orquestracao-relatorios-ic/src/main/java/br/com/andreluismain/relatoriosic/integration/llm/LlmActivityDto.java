package br.com.andreluismain.relatoriosic.integration.llm;

import br.com.andreluismain.relatoriosic.domain.model.ActivityCategory;

/**
 * DTO para representação de atividade individual inferida pelo modelo LLM.
 */
public record LlmActivityDto(
        String description,
        ActivityCategory category,
        Integer hoursSpent,
        String evidence
) {}
