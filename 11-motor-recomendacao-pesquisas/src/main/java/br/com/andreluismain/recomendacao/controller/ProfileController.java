package br.com.andreluismain.recomendacao.controller;

import br.com.andreluismain.recomendacao.domain.service.RecommendationService;
import br.com.andreluismain.recomendacao.dto.request.CreateProfileRequest;
import br.com.andreluismain.recomendacao.dto.response.ProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para cadastro e consulta de perfis acadêmicos.
 */
@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final RecommendationService recommendationService;

    public ProfileController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Cadastra um novo perfil de estudante.
     */
    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        ProfileResponse response = recommendationService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Consulta o perfil do estudante pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(recommendationService.getProfileById(id));
    }
}
