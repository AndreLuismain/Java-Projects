package br.com.andreluismain.recomendacao.etl.load;

import br.com.andreluismain.recomendacao.domain.model.ResearchOpportunity;
import br.com.andreluismain.recomendacao.etl.transform.OpportunityDeduplicator;
import br.com.andreluismain.recomendacao.repository.ResearchOpportunityRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Carregador da pipeline ETL que persiste oportunidades deduplicadas no repositório.
 */
@Component
public class OpportunityLoader {

    private final ResearchOpportunityRepository repository;

    public OpportunityLoader(ResearchOpportunityRepository repository) {
        this.repository = repository;
    }

    /**
     * Persiste as oportunidades normalizadas e deduplicadas, reaproveitando entidades já existentes por hash.
     */
    @Transactional
    public List<ResearchOpportunity> loadAll(List<OpportunityDeduplicator.DeduplicatedItem> items) {
        List<ResearchOpportunity> results = new ArrayList<>();

        for (OpportunityDeduplicator.DeduplicatedItem item : items) {
            Optional<ResearchOpportunity> existing = repository.findByContentHash(item.contentHash());
            if (existing.isPresent()) {
                results.add(existing.get());
            } else {
                var opp = item.opportunity();
                ResearchOpportunity newOpp = new ResearchOpportunity(
                        UUID.randomUUID(),
                        opp.sourceId(),
                        opp.title(),
                        opp.institution(),
                        opp.description(),
                        opp.normalizedTechnologies(),
                        opp.normalizedAreas(),
                        opp.modality(),
                        opp.deadline(),
                        item.contentHash()
                );
                results.add(repository.save(newOpp));
            }
        }

        return results;
    }
}
