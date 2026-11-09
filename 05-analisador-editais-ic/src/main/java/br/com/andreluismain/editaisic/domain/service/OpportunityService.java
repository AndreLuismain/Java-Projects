package br.com.andreluismain.editaisic.domain.service;

import br.com.andreluismain.editaisic.domain.model.Opportunity;
import br.com.andreluismain.editaisic.domain.model.OpportunityStatus;
import br.com.andreluismain.editaisic.domain.repository.OpportunityRepository;
import br.com.andreluismain.editaisic.dto.request.CollectOpportunityRequest;
import br.com.andreluismain.editaisic.dto.response.CollectOpportunityResponse;
import br.com.andreluismain.editaisic.dto.response.OpportunityDetailResponse;
import br.com.andreluismain.editaisic.dto.response.OpportunityResponse;
import br.com.andreluismain.editaisic.exception.DuplicateResourceException;
import br.com.andreluismain.editaisic.exception.ResourceNotFoundException;
import br.com.andreluismain.editaisic.integration.scraper.ExtractedContent;
import br.com.andreluismain.editaisic.integration.scraper.ScraperService;
import br.com.andreluismain.editaisic.mapper.OpportunityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço responsável pelo fluxo de coleta, normalização e consulta de oportunidades e editais.
 */
@Service
public class OpportunityService {

    private static final Logger log = LoggerFactory.getLogger(OpportunityService.class);

    private final OpportunityRepository opportunityRepository;
    private final ScraperService scraperService;
    private final OpportunityMapper opportunityMapper;

    public OpportunityService(
            OpportunityRepository opportunityRepository,
            ScraperService scraperService,
            OpportunityMapper opportunityMapper
    ) {
        this.opportunityRepository = opportunityRepository;
        this.scraperService = scraperService;
        this.opportunityMapper = opportunityMapper;
    }

    /**
     * Coleta uma oportunidade a partir de sua URL pública e a normaliza no banco de dados.
     *
     * @param request dados da oportunidade a coletar
     * @return resposta estruturada com o ID gerado
     * @throws DuplicateResourceException se a URL ou o conteúdo já existirem
     */
    @Transactional
    public CollectOpportunityResponse collectOpportunity(CollectOpportunityRequest request) {
        String url = request.getUrl().trim();

        // Idempotência e prevenção de duplicidade por URL
        if (opportunityRepository.existsBySourceUrl(url)) {
            log.warn("Tentativa de coleta de URL já cadastrada: {}", url);
            throw new DuplicateResourceException("Já existe uma oportunidade cadastrada com a URL informada: " + url);
        }

        // Executa a extração HTML e normalização
        ExtractedContent extracted = scraperService.fetchAndExtract(url);

        // Prevenção de duplicidade por hash de conteúdo
        Optional<Opportunity> byHash = opportunityRepository.findByContentHash(extracted.contentHash());
        if (byHash.isPresent()) {
            log.warn("Conteúdo com hash idêntico já cadastrado em outra oportunidade: {}", byHash.get().getId());
            throw new DuplicateResourceException("O conteúdo desta página já se encontra cadastrado sob o ID: " + byHash.get().getId());
        }

        Opportunity opportunity = Opportunity.builder()
                .sourceUrl(url)
                .sourceName(request.getSourceName().trim())
                .title(extracted.title())
                .institution(extracted.institution())
                .publishedAt(extracted.publishedAt())
                .deadlineAt(extracted.deadlineAt())
                .rawContent(extracted.rawContent())
                .contentHash(extracted.contentHash())
                .status(OpportunityStatus.COLLECTED)
                .build();

        Opportunity saved = opportunityRepository.save(opportunity);
        log.info("Oportunidade coletada com sucesso [id={}]: {}", saved.getId(), saved.getTitle());
        return opportunityMapper.toCollectResponse(saved);
    }

    /**
     * Consulta detalhada de uma oportunidade pelo ID.
     */
    @Transactional(readOnly = true)
    public OpportunityDetailResponse getOpportunityById(UUID id) {
        Opportunity opportunity = findEntityById(id);
        return opportunityMapper.toDetailResponse(opportunity);
    }

    /**
     * Busca a entidade gerenciada da oportunidade ou lança exceção.
     */
    @Transactional(readOnly = true)
    public Opportunity findEntityById(UUID id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade não encontrada com o ID: " + id));
    }

    /**
     * Consulta paginada com filtros opcionais de status e data limite.
     */
    @Transactional(readOnly = true)
    public Page<OpportunityResponse> listOpportunities(
            Pageable pageable,
            OpportunityStatus status,
            Instant deadlineBefore
    ) {
        Specification<Opportunity> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (status != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }
            if (deadlineBefore != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("deadlineAt"), deadlineBefore));
            }
            return predicates;
        };

        return opportunityRepository.findAll(spec, pageable)
                .map(opportunityMapper::toResponse);
    }
}
