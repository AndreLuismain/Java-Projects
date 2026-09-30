package br.com.andreluismain.relatoriosic.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Payload de requisição para envio de log semanal de pesquisa.
 */
public record CreateWeeklyLogRequest(
        @NotNull(message = "ID do projeto é obrigatório")
        UUID projectId,

        @NotNull(message = "Número da semana é obrigatório")
        @Min(value = 1, message = "Número da semana deve ser maior ou igual a 1")
        Integer weekNumber,

        @NotNull(message = "Data inicial é obrigatória")
        LocalDate startDate,

        @NotNull(message = "Data final é obrigatória")
        LocalDate endDate,

        @NotBlank(message = "Notas brutas de atividades são obrigatórias")
        String rawNotes
) {}
