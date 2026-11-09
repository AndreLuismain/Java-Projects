package br.com.andreluismain.editaisic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * Payload para requisição de coleta de um edital ou página acadêmica.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectOpportunityRequest {

    @NotBlank(message = "A URL da oportunidade é obrigatória")
    @URL(message = "Formato de URL inválido")
    @Size(max = 2048, message = "A URL não pode exceder 2048 caracteres")
    private String url;

    @NotBlank(message = "O nome da fonte é obrigatório")
    @Size(max = 255, message = "O nome da fonte não pode exceder 255 caracteres")
    private String sourceName;
}
