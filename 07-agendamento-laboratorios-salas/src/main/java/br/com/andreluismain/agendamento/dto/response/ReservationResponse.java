package br.com.andreluismain.agendamento.dto.response;

import br.com.andreluismain.agendamento.domain.model.Reservation;
import br.com.andreluismain.agendamento.domain.model.ReservationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Resposta com os detalhes de um agendamento de reserva.
 */
public record ReservationResponse(
        UUID id,
        UUID resourceId,
        String resourceName,
        UUID userId,
        Instant startAt,
        Instant endAt,
        ReservationStatus status,
        Instant createdAt
) {
    public static ReservationResponse from(Reservation res) {
        return new ReservationResponse(
                res.getId(),
                res.getResource().getId(),
                res.getResource().getName(),
                res.getUserId(),
                res.getStartAt(),
                res.getEndAt(),
                res.getStatus(),
                res.getCreatedAt()
        );
    }
}
