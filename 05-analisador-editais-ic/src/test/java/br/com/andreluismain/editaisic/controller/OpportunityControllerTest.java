package br.com.andreluismain.editaisic.controller;

import br.com.andreluismain.editaisic.domain.model.MatchClassification;
import br.com.andreluismain.editaisic.domain.model.OpportunityStatus;
import br.com.andreluismain.editaisic.domain.service.AnalysisService;
import br.com.andreluismain.editaisic.domain.service.OpportunityService;
import br.com.andreluismain.editaisic.dto.request.AnalyzeOpportunityRequest;
import br.com.andreluismain.editaisic.dto.request.CollectOpportunityRequest;
import br.com.andreluismain.editaisic.dto.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpportunityController.class)
@DisplayName("Testes de Controller - Oportunidades e Análise")
class OpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OpportunityService opportunityService;

    @MockitoBean
    private AnalysisService analysisService;

    @Test
    @DisplayName("POST /api/v1/opportunities/collect - Deve retornar 202 ao coletar oportunidade")
    void shouldCollectOpportunity() throws Exception {
        UUID id = UUID.randomUUID();
        CollectOpportunityRequest request = CollectOpportunityRequest.builder()
                .url("https://usp.br/edital-ic-2026")
                .sourceName("USP")
                .build();

        CollectOpportunityResponse response = CollectOpportunityResponse.builder()
                .id(id)
                .title("Edital PIBIC USP 2026")
                .status(OpportunityStatus.COLLECTED)
                .sourceUrl(request.getUrl())
                .createdAt(Instant.now())
                .build();

        Mockito.when(opportunityService.collectOpportunity(any(CollectOpportunityRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/opportunities/collect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("COLLECTED"))
                .andExpect(jsonPath("$.title").value("Edital PIBIC USP 2026"));
    }

    @Test
    @DisplayName("POST /api/v1/opportunities/{id}/analyze - Deve aceitar análise com status 202")
    void shouldTriggerAnalysis() throws Exception {
        UUID oppId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID analysisId = UUID.randomUUID();

        AnalyzeOpportunityRequest request = AnalyzeOpportunityRequest.builder()
                .profileId(profileId)
                .build();

        AnalyzeAcceptedResponse response = AnalyzeAcceptedResponse.builder()
                .analysisId(analysisId)
                .status("PROCESSING")
                .message("Análise enviada para processamento assíncrono.")
                .build();

        Mockito.when(analysisService.requestAnalysis(eq(oppId), eq(profileId))).thenReturn(response);

        mockMvc.perform(post("/api/v1/opportunities/{id}/analyze", oppId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.analysisId").value(analysisId.toString()))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    @DisplayName("GET /api/v1/opportunities/{id}/analysis - Deve retornar 200 com os dados da análise")
    void shouldGetAnalysis() throws Exception {
        UUID oppId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID analysisId = UUID.randomUUID();

        AnalysisResponse response = AnalysisResponse.builder()
                .id(analysisId)
                .opportunityId(oppId)
                .profileId(profileId)
                .summary("Excelente oportunidade para o perfil.")
                .matchScore(BigDecimal.valueOf(88.50))
                .classification(MatchClassification.HIGH_MATCH)
                .requirements(List.of(new RequirementDto("Formação", "Graduação em Computação", true)))
                .risks(List.of())
                .modelName("gemini-1.5-flash")
                .promptVersion("v1.0.0")
                .analyzedAt(Instant.now())
                .build();

        Mockito.when(analysisService.getAnalysis(eq(oppId), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/opportunities/{id}/analysis", oppId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(analysisId.toString()))
                .andExpect(jsonPath("$.classification").value("HIGH_MATCH"))
                .andExpect(jsonPath("$.matchScore").value(88.50));
    }
}
