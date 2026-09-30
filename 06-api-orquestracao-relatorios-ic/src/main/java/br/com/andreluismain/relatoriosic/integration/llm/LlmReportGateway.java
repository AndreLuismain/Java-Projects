package br.com.andreluismain.relatoriosic.integration.llm;

import br.com.andreluismain.relatoriosic.domain.model.WeeklyLog;

/**
 * Contrato de integração com serviço LLM para extração e classificação de notas de pesquisa.
 */
public interface LlmReportGateway {

    /**
     * Extrai e classifica atividades a partir das notas brutas do log semanal.
     */
    LlmClassificationDto classifyActivities(WeeklyLog weeklyLog);
}
