package br.com.andreluismain.agendamento.domain.service;

import br.com.andreluismain.agendamento.domain.model.Reservation;
import br.com.andreluismain.agendamento.domain.model.ReservationStatus;
import br.com.andreluismain.agendamento.domain.model.Resource;
import br.com.andreluismain.agendamento.dto.request.CreateReservationRequest;
import br.com.andreluismain.agendamento.dto.response.ReservationResponse;
import br.com.andreluismain.agendamento.exception.BusinessException;
import br.com.andreluismain.agendamento.exception.InvalidRequestException;
import br.com.andreluismain.agendamento.exception.ReservationConflictException;
import br.com.andreluismain.agendamento.exception.ResourceNotFoundException;
import br.com.andreluismain.agendamento.repository.ReservationRepository;
import br.com.andreluismain.agendamento.repository.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Serviço de gerenciamento de reservas com controle estrito de concorrência e sobreposição temporal.
 */
@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;

    @Value("${agendamento.min-notice-minutes:15}")
    private int minNoticeMinutes = 15;

    @Value("${agendamento.max-duration-hours:12}")
    private int maxDurationHours = 12;

    @Value("${agendamento.max-advance-days:30}")
    private int maxAdvanceDays = 30;

    public ReservationService(ReservationRepository reservationRepository, ResourceRepository resourceRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
    }

    public void setMinNoticeMinutes(int minNoticeMinutes) {
        this.minNoticeMinutes = minNoticeMinutes;
    }

    public void setMaxDurationHours(int maxDurationHours) {
        this.maxDurationHours = maxDurationHours;
    }

    public void setMaxAdvanceDays(int maxAdvanceDays) {
        this.maxAdvanceDays = maxAdvanceDays;
    }

    /**
     * Cria uma reserva garantindo exclusividade de horário via bloqueio pessimista do recurso.
     */
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        Instant now = Instant.now();
        Instant startAt = request.startAt();
        Instant endAt = request.endAt();

        // 1. Validações temporais
        if (startAt.isBefore(now.plus(Duration.ofMinutes(minNoticeMinutes)))) {
            throw new InvalidRequestException("Reserva deve ser realizada com no mínimo " + minNoticeMinutes + " minutos de antecedência.");
        }
        if (!endAt.isAfter(startAt)) {
            throw new InvalidRequestException("A data/hora de término deve ser posterior à data/hora de início.");
        }
        if (Duration.between(startAt, endAt).toHours() > maxDurationHours) {
            throw new InvalidRequestException("A duração máxima permitida para uma reserva é de " + maxDurationHours + " horas.");
        }
        if (startAt.isAfter(now.plus(Duration.ofDays(maxAdvanceDays)))) {
            throw new InvalidRequestException("Não é permitido agendar reservas com mais de " + maxAdvanceDays + " dias de antecedência.");
        }

        // 2. Bloqueio pessimista de escrita no recurso (SELECT FOR UPDATE)
        Resource resource = resourceRepository.findByIdWithLock(request.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException(request.resourceId()));

        if (!Boolean.TRUE.equals(resource.getActive())) {
            throw new BusinessException("O recurso selecionado está desativado para agendamentos.",
                    HttpStatus.UNPROCESSABLE_ENTITY, "RESOURCE_INACTIVE");
        }

        // 3. Verificação de sobreposição concorrente de horários
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(resource.getId(), startAt, endAt);
        if (!conflicts.isEmpty()) {
            throw new ReservationConflictException("Já existe uma reserva ativa conflitante no período solicitado.");
        }

        // 4. Criação e persistência da reserva
        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .resource(resource)
                .userId(request.userId())
                .startAt(startAt)
                .endAt(endAt)
                .status(ReservationStatus.ACTIVE)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        log.info("Reserva confirmada: id={}, recurso={}, início={}, término={}",
                saved.getId(), resource.getName(), startAt, endAt);

        return ReservationResponse.from(saved);
    }

    /**
     * Cancela uma reserva existente.
     */
    @Transactional
    public void cancelReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Reserva com id '" + id + "' não encontrada.",
                        HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return;
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        log.info("Reserva cancelada: id={}", id);
    }

    /**
     * Consulta reserva pelo ID.
     */
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .map(ReservationResponse::from)
                .orElseThrow(() -> new BusinessException("Reserva com id '" + id + "' não encontrada.",
                        HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND"));
    }

    /**
     * Lista reservas de um recurso de forma paginada.
     */
    @Transactional(readOnly = true)
    public Page<ReservationResponse> listReservationsByResource(UUID resourceId, Pageable pageable) {
        return reservationRepository.findByResourceIdOrderByStartAtDesc(resourceId, pageable)
                .map(ReservationResponse::from);
    }
}
