package br.com.andreluismain.agendamento.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Payload de requisição para agendamento de reserva.
 */
public record CreateReservationRequest(
        @NotNull(message = "Identificador do recurso é obrigatório")
        UUID resourceId,

        @NotNull(message = "Identificador do usuário é obrigatório")
        UUID userId,

        @NotNull(message = "Data e hora de início são obrigatórias")
        Instant startAt,

        @NotNull(message = "Data e hora de término são obrigatórias")
        Instant endAt
) {}
