package br.com.andreluismain.agendamento.repository;

import br.com.andreluismain.agendamento.domain.model.Reservation;
import br.com.andreluismain.agendamento.domain.model.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repositório para gerenciamento de reservas com detecção precisa de sobreposição de intervalos.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("SELECT r FROM Reservation r WHERE r.resource.id = :resourceId AND r.status = 'ACTIVE' " +
           "AND r.startAt < :endAt AND r.endAt > :startAt")
    List<Reservation> findConflictingReservations(@Param("resourceId") UUID resourceId,
                                                 @Param("startAt") Instant startAt,
                                                 @Param("endAt") Instant endAt);

    List<Reservation> findByStatusAndStartAtBetweenOrderByStartAtAsc(ReservationStatus status, Instant start, Instant end);

    Page<Reservation> findByResourceIdOrderByStartAtDesc(UUID resourceId, Pageable pageable);
}
