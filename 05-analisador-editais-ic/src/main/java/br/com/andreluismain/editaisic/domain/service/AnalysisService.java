package br.com.andreluismain.editaisic.domain.service;

import br.com.andreluismain.editaisic.domain.model.Analysis;
import br.com.andreluismain.editaisic.domain.model.Opportunity;
import br.com.andreluismain.editaisic.domain.model.OpportunityStatus;
import br.com.andreluismain.editaisic.domain.model.Profile;
import br.com.andreluismain.editaisic.domain.repository.AnalysisRepository;
import br.com.andreluismain.editaisic.domain.repository.OpportunityRepository;
import br.com.andreluismain.editaisic.dto.response.AnalysisResponse;
import br.com.andreluismain.editaisic.dto.response.AnalyzeAcceptedResponse;
import br.com.andreluismain.editaisic.dto.response.GeminiAnalysisDto;
import br.com.andreluismain.editaisic.exception.ResourceNotFoundException;
import br.com.andreluismain.editaisic.integration.gemini.LlmGateway;
import br.com.andreluismain.editaisic.mapper.AnalysisMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Serviço responsável pela orquestração de análise semântica de oportunidades via IA.
 */
@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisRepository analysisRepository;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityService opportunityService;
    private final ProfileService profileService;
    private final LlmGateway llmGateway;
    private final AnalysisMapper analysisMapper;
    private final ObjectMapper objectMapper;

    public AnalysisService(
            AnalysisRepository analysisRepository,
            OpportunityRepository opportunityRepository,
            OpportunityService opportunityService,
            ProfileService profileService,
            LlmGateway llmGateway,
            AnalysisMapper analysisMapper,
            ObjectMapper objectMapper
    ) {
        this.analysisRepository = analysisRepository;
        this.opportunityRepository = opportunityRepository;
        this.opportunityService = opportunityService;
        this.profileService = profileService;
        this.llmGateway = llmGateway;
        this.analysisMapper = analysisMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * Inicia a análise assíncrona da oportunidade para o perfil indicado.
     *
     * @param opportunityId ID da oportunidade
     * @param profileId ID do perfil acadêmico
     * @return {@link AnalyzeAcceptedResponse} com status PROCESSING
     */
    public AnalyzeAcceptedResponse requestAnalysis(UUID opportunityId, UUID profileId) {
        // Valida a existência prévia das entidades
        opportunityService.findEntityById(opportunityId);
        profileService.findEntityById(profileId);

        // Se já existe análise para o par, retorna seu identificador
        Optional<Analysis> existing = analysisRepository.findByOpportunityIdAndProfileId(opportunityId, profileId);
        if (existing.isPresent()) {
            return AnalyzeAcceptedResponse.builder()
                    .analysisId(existing.get().getId())
                    .status("COMPLETED")
                    .message("Esta oportunidade já foi analisada para o perfil informado.")
                    .build();
        }

        UUID generatedId = UUID.randomUUID();

        // Disparo assíncrono para não travar a thread HTTP
        executeAsyncAnalysis(opportunityId, profileId, generatedId);

        return AnalyzeAcceptedResponse.builder()
                .analysisId(generatedId)
                .status("PROCESSING")
                .message("Análise enviada para processamento assíncrono.")
                .build();
    }

    /**
     * Executa a análise assíncrona em background.
     */
    @Async("taskExecutor")
    public CompletableFuture<Analysis> executeAsyncAnalysis(UUID opportunityId, UUID profileId, UUID analysisId) {
        log.info("Executando análise assíncrona em background [opportunityId={}, profileId={}]", opportunityId, profileId);
        try {
            Opportunity opportunity = opportunityService.findEntityById(opportunityId);
            Profile profile = profileService.findEntityById(profileId);

            Analysis analysis = performAnalysis(opportunity, profile, analysisId);
            return CompletableFuture.completedFuture(analysis);
        } catch (Exception e) {
            log.error("Erro durante a análise assíncrona da oportunidade {}: {}", opportunityId, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Executa a análise síncrona ou no contexto transacional.
     */
    @Transactional
    public Analysis performAnalysis(Opportunity opportunity, Profile profile, UUID analysisId) {
        // Idempotência
        Optional<Analysis> existing = analysisRepository.findByOpportunityIdAndProfileId(opportunity.getId(), profile.getId());
        if (existing.isPresent()) {
            return existing.get();
        }

        GeminiAnalysisDto dto = llmGateway.analyze(profile, opportunity);

        String requirementsJson = null;
        String risksJson = null;
        try {
            if (dto.getRequirements() != null) {
                requirementsJson = objectMapper.writeValueAsString(dto.getRequirements());
            }
            if (dto.getRisks() != null) {
                risksJson = objectMapper.writeValueAsString(dto.getRisks());
            }
        } catch (JsonProcessingException e) {
            log.warn("Erro ao serializar campos adicionais da análise: {}", e.getMessage());
        }

        Analysis analysis = Analysis.builder()
                .id(analysisId != null ? analysisId : UUID.randomUUID())
                .opportunity(opportunity)
                .profile(profile)
                .summary(dto.getSummary())
                .matchScore(dto.getMatchScore())
                .classification(dto.getClassification())
                .requirements(requirementsJson)
                .risks(risksJson)
                .modelName(llmGateway.getModelName())
                .promptVersion(llmGateway.getPromptVersion())
                .analyzedAt(Instant.now())
                .build();

        Analysis saved = analysisRepository.save(analysis);

        // Atualiza o estado da oportunidade para ANALYZED
        opportunity.setStatus(OpportunityStatus.ANALYZED);
        opportunityRepository.save(opportunity);

        log.info("Análise persistida com sucesso [id={}] com classificação {}", saved.getId(), saved.getClassification());
        return saved;
    }

    /**
     * Consulta a análise associada a uma oportunidade, opcionalmente filtrada por perfil.
     */
    @Transactional(readOnly = true)
    public AnalysisResponse getAnalysis(UUID opportunityId, UUID profileId) {
        Analysis analysis;
        if (profileId != null) {
            analysis = analysisRepository.findByOpportunityIdAndProfileId(opportunityId, profileId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format("Análise não encontrada para a oportunidade '%s' e perfil '%s'", opportunityId, profileId)));
        } else {
            analysis = analysisRepository.findFirstByOpportunityIdOrderByAnalyzedAtDesc(opportunityId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format("Nenhuma análise encontrada para a oportunidade '%s'", opportunityId)));
        }
        return analysisMapper.toResponse(analysis);
    }
}
