package br.com.andreluismain.relatoriosic.controller;

import br.com.andreluismain.relatoriosic.domain.service.ProjectService;
import br.com.andreluismain.relatoriosic.domain.service.ReportService;
import br.com.andreluismain.relatoriosic.dto.request.CreateProjectRequest;
import br.com.andreluismain.relatoriosic.dto.response.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para cadastro de projetos de pesquisa e geração de relatório em Markdown.
 */
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ReportService reportService;

    public ProjectController(ProjectService projectService, ReportService reportService) {
        this.projectService = projectService;
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listProjects() {
        return ResponseEntity.ok(projectService.listProjects());
    }

    @GetMapping(value = "/{id}/report", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> generateReport(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.generateReport(id));
    }
}
