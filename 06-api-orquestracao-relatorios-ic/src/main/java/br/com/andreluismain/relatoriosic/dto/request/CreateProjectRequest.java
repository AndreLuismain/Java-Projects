package br.com.andreluismain.relatoriosic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Payload de requisição para cadastro de projeto de Iniciação Científica.
 */
public record CreateProjectRequest(
        @NotBlank(message = "Título do projeto é obrigatório")
        String title,

        @NotBlank(message = "Nome do estudante é obrigatório")
        String studentName,

        @NotBlank(message = "Nome do orientador é obrigatório")
        String advisorName,

        String grantAgency,

        @NotNull(message = "Data de início é obrigatória")
        LocalDate startDate,

        @NotNull(message = "Data de término é obrigatória")
        LocalDate endDate
) {}
