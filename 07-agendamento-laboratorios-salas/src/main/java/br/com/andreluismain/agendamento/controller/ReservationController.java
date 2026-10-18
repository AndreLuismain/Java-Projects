package br.com.andreluismain.agendamento.controller;

import br.com.andreluismain.agendamento.domain.model.ResourceType;
import br.com.andreluismain.agendamento.domain.service.ReservationService;
import br.com.andreluismain.agendamento.dto.request.CreateReservationRequest;
import br.com.andreluismain.agendamento.dto.response.ReservationResponse;
import br.com.andreluismain.agendamento.report.UsageReportService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Controller REST para agendamento de reservas, cancelamentos e relatórios em CSV.
 */
@RestController
@RequestMapping("/api/v1")
public class ReservationController {

    private final ReservationService reservationService;
    private final UsageReportService usageReportService;

    public ReservationController(ReservationService reservationService, UsageReportService usageReportService) {
        this.reservationService = reservationService;
        this.usageReportService = usageReportService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(request));
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable UUID id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/reports/usage", produces = "text/csv;charset=UTF-8")
    public ResponseEntity<String> getUsageReport(
            @RequestParam(value = "referenceDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate,
            @RequestParam(value = "resourceType", required = false) ResourceType resourceType) {
        String csv = usageReportService.generateWeeklyUsageCsv(referenceDate, resourceType);
        return ResponseEntity.ok(csv);
    }
}
