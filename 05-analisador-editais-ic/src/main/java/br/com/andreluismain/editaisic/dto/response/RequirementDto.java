package br.com.andreluismain.editaisic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa um requisito extraído do edital pelo modelo de IA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequirementDto {

    private String name;
    private String value;
    private boolean mandatory;
}
