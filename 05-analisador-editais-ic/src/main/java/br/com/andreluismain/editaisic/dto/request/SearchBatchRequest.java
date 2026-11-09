package br.com.andreluismain.editaisic.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Payload para requisição de processamento em lote (busca/coleta de múltiplas URLs).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchBatchRequest {

    @NotNull(message = "O ID do perfil é obrigatório")
    private UUID profileId;

    @NotEmpty(message = "Pelo menos uma URL deve ser informada")
    private List<String> urls;

    @Builder.Default
    private boolean analyze = true;
}
