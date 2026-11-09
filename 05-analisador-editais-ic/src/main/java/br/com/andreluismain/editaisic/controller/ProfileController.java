package br.com.andreluismain.editaisic.controller;

import br.com.andreluismain.editaisic.domain.service.ProfileService;
import br.com.andreluismain.editaisic.dto.request.CreateProfileRequest;
import br.com.andreluismain.editaisic.dto.response.ErrorResponse;
import br.com.andreluismain.editaisic.dto.response.ProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para cadastro e consulta de perfis acadêmicos de estudantes/pesquisadores.
 */
@RestController
@RequestMapping("/api/v1/profiles")
@Tag(name = "Perfis Acadêmicos", description = "Endpoints para gerenciamento do perfil do pesquisador ou estudante")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    @Operation(summary = "Criar novo perfil acadêmico", description = "Cadastra um perfil com interesses e palavras-chave para matching com editais.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        ProfileResponse response = profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar perfil por ID", description = "Recupera os detalhes completos de um perfil cadastrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable UUID id) {
        ProfileResponse response = profileService.getProfileById(id);
        return ResponseEntity.ok(response);
    }
}
