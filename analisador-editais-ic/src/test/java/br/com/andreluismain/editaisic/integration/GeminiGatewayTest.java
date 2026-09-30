package br.com.andreluismain.editaisic.integration;

import br.com.andreluismain.editaisic.domain.model.MatchClassification;
import br.com.andreluismain.editaisic.domain.model.Opportunity;
import br.com.andreluismain.editaisic.domain.model.Profile;
import br.com.andreluismain.editaisic.dto.response.GeminiAnalysisDto;
import br.com.andreluismain.editaisic.integration.gemini.GeminiGatewayImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Integração e Heurística - Gateway Gemini")
class GeminiGatewayTest {

    private GeminiGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        WebClient mockWebClient = Mockito.mock(WebClient.class);
        ObjectMapper objectMapper = new ObjectMapper();
        gateway = new GeminiGatewayImpl(mockWebClient, objectMapper);
    }

    @Test
    @DisplayName("Deve classificar como HIGH_MATCH quando há forte sobreposição de palavras-chave")
    void shouldClassifyHighMatch() {
        Profile profile = Profile.builder()
                .id(UUID.randomUUID())
                .name("Estudante IA")
                .description("Interesse em Inteligência Artificial e Engenharia de Software")
                .build();
        profile.setKeywordsList(List.of("Java", "Inteligência Artificial", "Spring", "Machine Learning"));

        Opportunity opportunity = Opportunity.builder()
                .id(UUID.randomUUID())
                .title("Bolsa PIBIC em Inteligência Artificial e Java")
                .rawContent("O projeto visa desenvolver modelos de inteligência artificial com Java e Spring Boot para processamento de dados acadêmicos com técnicas avançadas de machine learning.")
                .build();

        GeminiAnalysisDto result = gateway.generateHeuristicAnalysis(profile, opportunity);

        assertNotNull(result);
        assertEquals(MatchClassification.HIGH_MATCH, result.getClassification());
        assertTrue(result.getMatchScore().compareTo(BigDecimal.valueOf(75)) >= 0);
        assertNotNull(result.getSummary());
        assertFalse(result.getRequirements().isEmpty());
    }

    @Test
    @DisplayName("Deve classificar como INSUFFICIENT_DATA quando o conteúdo é excessivamente curto")
    void shouldClassifyInsufficientData() {
        Profile profile = Profile.builder()
                .name("Pesquisador")
                .description("Descrição válida")
                .build();
        profile.setKeywordsList(List.of("Física"));

        Opportunity opportunity = Opportunity.builder()
                .title("Aviso")
                .rawContent("Em breve mais informações.")
                .build();

        GeminiAnalysisDto result = gateway.generateHeuristicAnalysis(profile, opportunity);

        assertEquals(MatchClassification.INSUFFICIENT_DATA, result.getClassification());
        assertEquals(BigDecimal.ZERO, result.getMatchScore());
    }
}
