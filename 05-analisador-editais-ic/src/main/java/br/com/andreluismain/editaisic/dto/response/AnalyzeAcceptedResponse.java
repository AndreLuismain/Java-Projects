package br.com.andreluismain.editaisic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Resposta assíncrona ao solicitar o processamento de análise de uma oportunidade.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeAcceptedResponse {

    private UUID analysisId;
    private String status;
    private String message;
}
