package br.com.andreluismain.recomendacao.dto.response;

/**
 * Resposta com o resumo do processamento ETL da importação de vagas.
 */
public record ImportResultResponse(
        int totalReceived,
        int uniqueImported,
        int duplicatesSkipped
) {}
