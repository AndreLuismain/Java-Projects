package br.com.andreluismain.editaisic.controller;

import br.com.andreluismain.editaisic.dto.request.CreateProfileRequest;
import br.com.andreluismain.editaisic.dto.response.ProfileResponse;
import br.com.andreluismain.editaisic.exception.ResourceNotFoundException;
import br.com.andreluismain.editaisic.domain.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@DisplayName("Testes de Controller - Perfis Acadêmicos")
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfileService profileService;

    @Test
    @DisplayName("POST /api/v1/profiles - Deve criar perfil com status 201")
    void shouldCreateProfile() throws Exception {
        UUID id = UUID.randomUUID();
        CreateProfileRequest request = CreateProfileRequest.builder()
                .name("André Luís")
                .description("Estudante de Ciência da Computação na USP com foco em Engenharia de Software")
                .keywords(List.of("Java", "Spring Boot", "Inteligência Artificial"))
                .build();

        ProfileResponse response = ProfileResponse.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .keywords(request.getKeywords())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Mockito.when(profileService.createProfile(any(CreateProfileRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("André Luís"))
                .andExpect(jsonPath("$.keywords[0]").value("Java"));
    }

    @Test
    @DisplayName("POST /api/v1/profiles - Deve retornar 400 para dados inválidos")
    void shouldRejectInvalidProfile() throws Exception {
        CreateProfileRequest invalid = CreateProfileRequest.builder()
                .name("") // Vazio
                .description("curto") // Menos que 10 chars
                .keywords(List.of()) // Vazio
                .build();

        mockMvc.perform(post("/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("GET /api/v1/profiles/{id} - Deve retornar 404 para perfil inexistente")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(profileService.getProfileById(id))
                .thenThrow(new ResourceNotFoundException("Perfil não encontrado"));

        mockMvc.perform(get("/api/v1/profiles/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }
}
