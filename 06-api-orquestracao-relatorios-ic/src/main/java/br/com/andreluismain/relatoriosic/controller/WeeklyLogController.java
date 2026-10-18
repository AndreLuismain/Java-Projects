package br.com.andreluismain.relatoriosic.controller;

import br.com.andreluismain.relatoriosic.domain.service.WeeklyLogService;
import br.com.andreluismain.relatoriosic.dto.request.CreateWeeklyLogRequest;
import br.com.andreluismain.relatoriosic.dto.response.WeeklyLogResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para submissão e consulta de logs semanais de atividades.
 */
@RestController
@RequestMapping("/api/v1")
public class WeeklyLogController {

    private final WeeklyLogService weeklyLogService;

    public WeeklyLogController(WeeklyLogService weeklyLogService) {
        this.weeklyLogService = weeklyLogService;
    }

    @PostMapping("/weekly-logs")
    public ResponseEntity<WeeklyLogResponse> submitWeeklyLog(@Valid @RequestBody CreateWeeklyLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(weeklyLogService.submitWeeklyLog(request));
    }

    @GetMapping("/weekly-logs/{id}")
    public ResponseEntity<WeeklyLogResponse> getWeeklyLog(@PathVariable UUID id) {
        return ResponseEntity.ok(weeklyLogService.getWeeklyLog(id));
    }

    @GetMapping("/projects/{projectId}/weekly-logs")
    public ResponseEntity<List<WeeklyLogResponse>> listWeeklyLogsByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(weeklyLogService.listLogsByProject(projectId));
    }
}
