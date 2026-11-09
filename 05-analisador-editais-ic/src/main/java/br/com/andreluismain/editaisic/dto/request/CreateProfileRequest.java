package br.com.andreluismain.editaisic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Payload de requisição para cadastro de perfil acadêmico.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {

    @NotBlank(message = "O nome do perfil é obrigatório")
    @Size(max = 255, message = "O nome não pode exceder 255 caracteres")
    private String name;

    @NotBlank(message = "A descrição do perfil é obrigatória")
    @Size(min = 10, message = "A descrição deve conter pelo menos 10 caracteres")
    private String description;

    @NotEmpty(message = "Pelo menos uma palavra-chave deve ser informada")
    private List<String> keywords;
}
