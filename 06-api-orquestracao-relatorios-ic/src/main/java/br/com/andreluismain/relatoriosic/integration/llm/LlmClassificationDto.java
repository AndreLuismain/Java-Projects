package br.com.andreluismain.relatoriosic.integration.llm;

import java.util.List;

/**
 * DTO contendo a lista de atividades extraídas e classificadas pelo modelo de IA.
 */
public record LlmClassificationDto(
        List<LlmActivityDto> activities
) {}
