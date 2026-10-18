package br.com.andreluismain.agendamento;

import br.com.andreluismain.agendamento.domain.model.Reservation;
import br.com.andreluismain.agendamento.domain.model.ReservationStatus;
import br.com.andreluismain.agendamento.domain.model.Resource;
import br.com.andreluismain.agendamento.domain.model.ResourceType;
import br.com.andreluismain.agendamento.domain.service.ReservationService;
import br.com.andreluismain.agendamento.dto.request.CreateReservationRequest;
import br.com.andreluismain.agendamento.dto.response.ReservationResponse;
import br.com.andreluismain.agendamento.exception.InvalidRequestException;
import br.com.andreluismain.agendamento.exception.ReservationConflictException;
import br.com.andreluismain.agendamento.repository.ReservationRepository;
import br.com.andreluismain.agendamento.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    private ReservationService reservationService;

    private UUID resourceId;
    private Resource resource;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationRepository, resourceRepository);
        reservationService.setMinNoticeMinutes(15);
        reservationService.setMaxDurationHours(12);
        reservationService.setMaxAdvanceDays(30);

        resourceId = UUID.randomUUID();
        resource = Resource.builder()
                .id(resourceId)
                .name("Lab Informática 1")
                .type(ResourceType.LAB)
                .capacity(30)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Deve criar reserva com sucesso quando não houver conflitos")
    void shouldCreateReservationSuccessfully() {
        Instant start = Instant.now().plus(Duration.ofHours(2));
        Instant end = start.plus(Duration.ofHours(2));
        CreateReservationRequest request = new CreateReservationRequest(resourceId, UUID.randomUUID(), start, end);

        when(resourceRepository.findByIdWithLock(resourceId)).thenReturn(Optional.of(resource));
        when(reservationRepository.findConflictingReservations(resourceId, start, end)).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        ReservationResponse response = reservationService.createReservation(request);

        assertNotNull(response);
        assertEquals(resourceId, response.resourceId());
        assertEquals(ReservationStatus.ACTIVE, response.status());
    }

    @Test
    @DisplayName("Deve lançar ReservationConflictException quando houver sobreposição temporal")
    void shouldThrowWhenConflictExists() {
        Instant start = Instant.now().plus(Duration.ofHours(2));
        Instant end = start.plus(Duration.ofHours(2));
        CreateReservationRequest request = new CreateReservationRequest(resourceId, UUID.randomUUID(), start, end);

        Reservation existingConflict = Reservation.builder()
                .id(UUID.randomUUID())
                .resource(resource)
                .startAt(start.minus(Duration.ofMinutes(30)))
                .endAt(start.plus(Duration.ofMinutes(30)))
                .status(ReservationStatus.ACTIVE)
                .build();

        when(resourceRepository.findByIdWithLock(resourceId)).thenReturn(Optional.of(resource));
        when(reservationRepository.findConflictingReservations(resourceId, start, end))
                .thenReturn(List.of(existingConflict));

        assertThrows(ReservationConflictException.class, () -> reservationService.createReservation(request));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar reserva com término anterior ou igual ao início")
    void shouldRejectInvalidEndBeforeStart() {
        Instant start = Instant.now().plus(Duration.ofHours(2));
        Instant end = start.minus(Duration.ofMinutes(10));
        CreateReservationRequest request = new CreateReservationRequest(resourceId, UUID.randomUUID(), start, end);

        assertThrows(InvalidRequestException.class, () -> reservationService.createReservation(request));
    }
}
