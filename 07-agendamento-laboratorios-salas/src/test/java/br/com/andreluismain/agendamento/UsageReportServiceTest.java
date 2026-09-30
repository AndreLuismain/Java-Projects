package br.com.andreluismain.agendamento;

import br.com.andreluismain.agendamento.domain.model.Reservation;
import br.com.andreluismain.agendamento.domain.model.ReservationStatus;
import br.com.andreluismain.agendamento.domain.model.Resource;
import br.com.andreluismain.agendamento.domain.model.ResourceType;
import br.com.andreluismain.agendamento.report.UsageReportService;
import br.com.andreluismain.agendamento.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("Testes de Relatório - UsageReportService")
class UsageReportServiceTest {

    @Test
    @DisplayName("Deve gerar CSV contendo colunas obrigatórias e dados sumarizados")
    void shouldGenerateValidCsvReport() {
        ReservationRepository repo = Mockito.mock(ReservationRepository.class);
        UsageReportService reportService = new UsageReportService(repo);

        Resource res = Resource.builder()
                .id(UUID.randomUUID())
                .name("Sala de Estudos A")
                .type(ResourceType.STUDY_ROOM)
                .capacity(10)
                .active(true)
                .build();

        Instant start = Instant.parse("2026-10-01T10:00:00Z");
        Instant end = start.plus(Duration.ofHours(2));

        Reservation activeRes = Reservation.builder()
                .id(UUID.randomUUID())
                .resource(res)
                .userId(UUID.randomUUID())
                .startAt(start)
                .endAt(end)
                .status(ReservationStatus.ACTIVE)
                .build();

        when(repo.findByStatusAndStartAtBetweenOrderByStartAtAsc(eq(ReservationStatus.ACTIVE), any(), any()))
                .thenReturn(List.of(activeRes));

        String csv = reportService.generateWeeklyUsageCsv(LocalDate.of(2026, 10, 1), null);

        assertNotNull(csv);
        assertTrue(csv.startsWith("resourceId,resourceName,date,reservedMinutes,reservationCount,utilizationPercent"));
        assertTrue(csv.contains("Sala de Estudos A"));
        assertTrue(csv.contains("120")); // 2 hours = 120 minutes
    }
}
