package br.com.andreluismain.recomendacao.domain.service;

import br.com.andreluismain.recomendacao.domain.model.*;
import br.com.andreluismain.recomendacao.dto.request.CreateProfileRequest;
import br.com.andreluismain.recomendacao.dto.request.RunRecommendationRequest;
import br.com.andreluismain.recomendacao.dto.response.*;
import br.com.andreluismain.recomendacao.etl.extract.OpportunityExtractor;
import br.com.andreluismain.recomendacao.etl.extract.RawOpportunity;
import br.com.andreluismain.recomendacao.etl.load.OpportunityLoader;
import br.com.andreluismain.recomendacao.etl.transform.OpportunityDeduplicator;
import br.com.andreluismain.recomendacao.etl.transform.OpportunityTransformer;
import br.com.andreluismain.recomendacao.exception.ProfileNotFoundException;
import br.com.andreluismain.recomendacao.exception.RecommendationRunNotFoundException;
import br.com.andreluismain.recomendacao.repository.RecommendationRepository;
import br.com.andreluismain.recomendacao.repository.RecommendationRunRepository;
import br.com.andreluismain.recomendacao.repository.ResearchOpportunityRepository;
import br.com.andreluismain.recomendacao.repository.StudentProfileRepository;
import br.com.andreluismain.recomendacao.scoring.RecommendationRanker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Serviço de orquestração do motor de recomendação acadêmica e pipeline ETL.
 */
@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final StudentProfileRepository profileRepository;
    private final ResearchOpportunityRepository opportunityRepository;
    private final RecommendationRunRepository runRepository;
    private final RecommendationRepository recommendationRepository;

    private final OpportunityExtractor extractor;
    private final OpportunityTransformer transformer;
    private final OpportunityDeduplicator deduplicator;
    private final OpportunityLoader loader;
    private final RecommendationRanker ranker;

    @Value("${recomendacao.pipeline-version:v1.0.0}")
    private String pipelineVersion = "v1.0.0";

    public RecommendationService(StudentProfileRepository profileRepository,
                                 ResearchOpportunityRepository opportunityRepository,
                                 RecommendationRunRepository runRepository,
                                 RecommendationRepository recommendationRepository,
                                 OpportunityExtractor extractor,
                                 OpportunityTransformer transformer,
                                 OpportunityDeduplicator deduplicator,
                                 OpportunityLoader loader,
                                 RecommendationRanker ranker) {
        this.profileRepository = profileRepository;
        this.opportunityRepository = opportunityRepository;
        this.runRepository = runRepository;
        this.recommendationRepository = recommendationRepository;
        this.extractor = extractor;
        this.transformer = transformer;
        this.deduplicator = deduplicator;
        this.loader = loader;
        this.ranker = ranker;
    }

    /**
     * Cadastra um novo perfil de estudante.
     */
    @Transactional
    public ProfileResponse createProfile(CreateProfileRequest request) {
        StudentProfile profile = new StudentProfile(
                UUID.randomUUID(),
                request.semester(),
                request.technologies(),
                request.researchAreas(),
                request.interests(),
                request.availabilityHours()
        );
        StudentProfile saved = profileRepository.save(profile);
        log.info("Perfil acadêmico criado: id={}, semestre={}", saved.getId(), saved.getSemester());
        return ProfileResponse.from(saved);
    }

    /**
     * Localiza perfil por ID.
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(UUID id) {
        return profileRepository.findById(id)
                .map(ProfileResponse::from)
                .orElseThrow(() -> new ProfileNotFoundException(id));
    }

    /**
     * Executa esteira completa de ETL e ranqueamento de recomendações para o perfil.
     */
    @Transactional
    public RecommendationRunInitiatedResponse runRecommendation(RunRecommendationRequest request) {
        StudentProfile profile = profileRepository.findById(request.profileId())
                .orElseThrow(() -> new ProfileNotFoundException(request.profileId()));

        RecommendationRun run = new RecommendationRun(
                UUID.randomUUID(),
                profile.getId(),
                pipelineVersion,
                request.isUseLlm() ? "gemini-1.5-flash" : "none"
        );
        runRepository.save(run);

        List<ResearchOpportunity> opportunitiesToScore;

        if (request.opportunities() != null && !request.opportunities().isEmpty()) {
            // Executa ETL sobre as oportunidades enviadas no payload
            List<RawOpportunity> rawList = request.opportunities().stream()
                    .map(extractor::extractFromMap)
                    .toList();

            List<OpportunityTransformer.TransformedOpportunity> transformedList = rawList.stream()
                    .map(transformer::transform)
                    .toList();

            List<OpportunityDeduplicator.DeduplicatedItem> deduplicated = deduplicator.deduplicate(transformedList);
            opportunitiesToScore = loader.loadAll(deduplicated);
        } else {
            opportunitiesToScore = opportunityRepository.findAll();
        }

        // Executa ranqueamento e cálculo de scores
        List<Recommendation> recommendations = ranker.rank(run.getId(), profile, opportunitiesToScore, request.isUseLlm());
        recommendationRepository.saveAll(recommendations);

        run.markCompleted();
        runRepository.save(run);

        log.info("Execução de recomendação concluída com sucesso: runId={}, itens={}", run.getId(), recommendations.size());

        return new RecommendationRunInitiatedResponse(run.getId(), RunStatus.PROCESSING);
    }

    /**
     * Recupera o ranking ordenado de recomendações para uma execução.
     */
    @Transactional(readOnly = true)
    public RecommendationResponse getRecommendations(UUID runId, Double minScore, Pageable pageable) {
        if (!runRepository.existsById(runId)) {
            throw new RecommendationRunNotFoundException(runId);
        }

        Page<Recommendation> page = recommendationRepository.findByRunIdWithFilter(runId, minScore, pageable);

        Map<UUID, String> titleMap = new HashMap<>();
        for (Recommendation r : page.getContent()) {
            opportunityRepository.findById(r.getOpportunityId())
                    .ifPresent(opp -> titleMap.put(r.getOpportunityId(), opp.getTitle()));
        }

        List<RecommendationItemResponse> items = page.getContent().stream()
                .map(r -> new RecommendationItemResponse(
                        r.getRank(),
                        r.getOpportunityId(),
                        titleMap.getOrDefault(r.getOpportunityId(), "Oportunidade de Pesquisa"),
                        r.getFinalScore(),
                        r.getReasons()
                ))
                .toList();

        return new RecommendationResponse(runId, items);
    }

    /**
     * Importa e normaliza oportunidades via pipeline ETL.
     */
    @Transactional
    public ImportResultResponse importOpportunities(List<Map<String, Object>> rawList) {
        if (rawList == null || rawList.isEmpty()) {
            return new ImportResultResponse(0, 0, 0);
        }

        int total = rawList.size();
        List<RawOpportunity> raw = rawList.stream().map(extractor::extractFromMap).toList();
        List<OpportunityTransformer.TransformedOpportunity> transformed = raw.stream().map(transformer::transform).toList();
        List<OpportunityDeduplicator.DeduplicatedItem> deduplicated = deduplicator.deduplicate(transformed);

        loader.loadAll(deduplicated);

        int unique = deduplicated.size();
        int skipped = total - unique;

        return new ImportResultResponse(total, unique, skipped);
    }
}
