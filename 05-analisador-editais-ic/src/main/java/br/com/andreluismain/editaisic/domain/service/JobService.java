package br.com.andreluismain.editaisic.domain.service;

import br.com.andreluismain.editaisic.domain.model.Job;
import br.com.andreluismain.editaisic.domain.model.JobStatus;
import br.com.andreluismain.editaisic.domain.model.Opportunity;
import br.com.andreluismain.editaisic.domain.model.Profile;
import br.com.andreluismain.editaisic.domain.repository.JobRepository;
import br.com.andreluismain.editaisic.dto.request.CollectOpportunityRequest;
import br.com.andreluismain.editaisic.dto.request.SearchBatchRequest;
import br.com.andreluismain.editaisic.dto.response.CollectOpportunityResponse;
import br.com.andreluismain.editaisic.dto.response.JobResponse;
import br.com.andreluismain.editaisic.exception.ResourceNotFoundException;
import br.com.andreluismain.editaisic.mapper.JobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Serviço de processamento em lote (Batch Jobs) para coleta e análise de múltiplos editais simultaneamente.
 */
@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final ProfileService profileService;
    private final OpportunityService opportunityService;
    private final AnalysisService analysisService;
    private final JobMapper jobMapper;

    public JobService(
            JobRepository jobRepository,
            ProfileService profileService,
            OpportunityService opportunityService,
            AnalysisService analysisService,
            JobMapper jobMapper
    ) {
        this.jobRepository = jobRepository;
        this.profileService = profileService;
        this.opportunityService = opportunityService;
        this.analysisService = analysisService;
        this.jobMapper = jobMapper;
    }

    /**
     * Registra e inicializa um novo job de busca em lote em background.
     *
     * @param request lista de URLs e ID do perfil
     * @return {@link JobResponse} com status QUEUED
     */
    @Transactional
    public JobResponse createAndStartJob(SearchBatchRequest request) {
        Profile profile = profileService.findEntityById(request.getProfileId());

        Job job = Job.builder()
                .profile(profile)
                .status(JobStatus.QUEUED)
                .totalUrls(request.getUrls().size())
                .processedUrls(0)
                .failedUrls(0)
                .build();

        Job saved = jobRepository.save(job);
        log.info("Job em lote criado [id={}] com {} URLs para processamento", saved.getId(), request.getUrls().size());

        // Disparo em background
        processBatchAsync(saved.getId(), request.getUrls(), request.isAnalyze());

        return jobMapper.toResponse(saved);
    }

    /**
     * Executa o processamento do lote em thread assíncrona.
     */
    @Async("taskExecutor")
    public void processBatchAsync(UUID jobId, List<String> urls, boolean analyze) {
        log.info("Iniciando execução em background do Job {}", jobId);
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) {
            return;
        }

        job.setStatus(JobStatus.PROCESSING);
        jobRepository.save(job);

        List<String> errors = new ArrayList<>();
        int processed = 0;
        int failed = 0;

        for (String url : urls) {
            try {
                CollectOpportunityResponse collected = opportunityService.collectOpportunity(
                        CollectOpportunityRequest.builder()
                                .url(url)
                                .sourceName("Batch Scraping")
                                .build()
                );

                if (analyze) {
                    Opportunity opp = opportunityService.findEntityById(collected.getId());
                    analysisService.performAnalysis(opp, job.getProfile(), UUID.randomUUID());
                }

                processed++;
            } catch (Exception e) {
                failed++;
                String err = String.format("Erro na URL '%s': %s", url, e.getMessage());
                log.warn(err);
                errors.add(err);
            }

            // Atualiza progresso intermediário a cada URL
            job.setProcessedUrls(processed);
            job.setFailedUrls(failed);
            job.setUpdatedAt(Instant.now());
            jobRepository.save(job);
        }

        job.setStatus(failed == urls.size() ? JobStatus.FAILED : JobStatus.COMPLETED);
        if (!errors.isEmpty()) {
            job.setErrorDetails(String.join("\n", errors));
        }
        jobRepository.save(job);
        log.info("Job {} concluído. Processados com sucesso: {}, Falhas: {}", jobId, processed, failed);
    }

    /**
     * Consulta o estado e métricas de execução de um job pelo ID.
     */
    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job não encontrado com o ID: " + jobId));
        return jobMapper.toResponse(job);
    }
}
