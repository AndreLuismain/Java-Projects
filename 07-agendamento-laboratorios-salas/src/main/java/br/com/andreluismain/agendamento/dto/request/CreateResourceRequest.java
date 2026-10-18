package br.com.andreluismain.agendamento.dto.request;

import br.com.andreluismain.agendamento.domain.model.ResourceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload de requisição para cadastro de recurso acadêmico.
 */
public record CreateResourceRequest(
        @NotBlank(message = "Nome do recurso é obrigatório")
        String name,

        @NotNull(message = "Tipo do recurso é obrigatório")
        ResourceType type,

        @NotNull(message = "Capacidade é obrigatória")
        @Min(value = 1, message = "Capacidade mínima deve ser 1")
        Integer capacity
) {}
