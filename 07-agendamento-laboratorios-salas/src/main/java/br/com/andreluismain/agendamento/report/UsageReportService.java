package br.com.andreluismain.agendamento.report;

import br.com.andreluismain.agendamento.domain.model.Reservation;
import br.com.andreluismain.agendamento.domain.model.ReservationStatus;
import br.com.andreluismain.agendamento.domain.model.ResourceType;
import br.com.andreluismain.agendamento.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

/**
 * Serviço gerador de relatórios de utilização semanal de salas e laboratórios em conformidade com RFC 4180 (CSV).
 */
@Service
public class UsageReportService {

    private final ReservationRepository reservationRepository;

    public UsageReportService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Gera relatório semanal em formato CSV.
     *
     * @param referenceDate data de referência
     * @param filterType filtro opcional por tipo de recurso
     * @return conteúdo do relatório formatado em CSV
     */
    public String generateWeeklyUsageCsv(LocalDate referenceDate, ResourceType filterType) {
        LocalDate startOfWeek = referenceDate != null ? referenceDate : LocalDate.now();
        Instant startInstant = startOfWeek.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endInstant = startOfWeek.plusDays(7).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<Reservation> reservations = reservationRepository.findByStatusAndStartAtBetweenOrderByStartAtAsc(
                ReservationStatus.ACTIVE, startInstant, endInstant
        );

        if (filterType != null) {
            reservations = reservations.stream()
                    .filter(r -> r.getResource().getType() == filterType)
                    .toList();
        }

        StringBuilder csv = new StringBuilder();
        csv.append("resourceId,resourceName,date,reservedMinutes,reservationCount,utilizationPercent\n");

        // Agrupamento por recurso e data
        Map<String, UsageMetric> metrics = new LinkedHashMap<>();

        for (Reservation r : reservations) {
            LocalDate resDate = r.getStartAt().atZone(ZoneOffset.UTC).toLocalDate();
            String key = r.getResource().getId() + "_" + resDate;
            long minutes = Duration.between(r.getStartAt(), r.getEndAt()).toMinutes();

            metrics.computeIfAbsent(key, k -> new UsageMetric(
                    r.getResource().getId(),
                    r.getResource().getName(),
                    resDate
            )).addReservation(minutes);
        }

        for (UsageMetric m : metrics.values()) {
            // Percentual considerando 14h de operação diária (840 minutos)
            double util = Math.min(100.0, (m.totalMinutes * 100.0) / 840.0);
            csv.append(String.format(Locale.ROOT, "%s,\"%s\",%s,%d,%d,%.2f\n",
                    m.resourceId,
                    m.resourceName.replace("\"", "\"\""),
                    m.date,
                    m.totalMinutes,
                    m.count,
                    util));
        }

        return csv.toString();
    }

    private static class UsageMetric {
        final UUID resourceId;
        final String resourceName;
        final LocalDate date;
        long totalMinutes = 0;
        int count = 0;

        UsageMetric(UUID resourceId, String resourceName, LocalDate date) {
            this.resourceId = resourceId;
            this.resourceName = resourceName;
            this.date = date;
        }

        void addReservation(long minutes) {
            this.totalMinutes += minutes;
            this.count++;
        }
    }
}
