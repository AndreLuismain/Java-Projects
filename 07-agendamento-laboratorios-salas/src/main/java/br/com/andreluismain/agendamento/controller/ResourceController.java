package br.com.andreluismain.agendamento.controller;

import br.com.andreluismain.agendamento.domain.service.ResourceService;
import br.com.andreluismain.agendamento.dto.request.CreateResourceRequest;
import br.com.andreluismain.agendamento.dto.response.ResourceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para cadastro e consulta de recursos acadêmicos.
 */
@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(@Valid @RequestBody CreateResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.createResource(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResource(@PathVariable UUID id) {
        return ResponseEntity.ok(resourceService.getResourceById(id));
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> listResources() {
        return ResponseEntity.ok(resourceService.listResources());
    }
}
