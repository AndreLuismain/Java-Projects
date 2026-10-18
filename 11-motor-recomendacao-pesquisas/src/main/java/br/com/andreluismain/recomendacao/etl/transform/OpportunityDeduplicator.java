package br.com.andreluismain.recomendacao.etl.transform;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Componente responsável pelo cálculo de hash de integridade e remoção de duplicatas na pipeline ETL.
 */
@Component
public class OpportunityDeduplicator {

    public record DeduplicatedItem(
            OpportunityTransformer.TransformedOpportunity opportunity,
            String contentHash
    ) {}

    /**
     * Deduplica uma lista de oportunidades transformadas com base no hash SHA-256 do conteúdo semântico.
     */
    public List<DeduplicatedItem> deduplicate(List<OpportunityTransformer.TransformedOpportunity> list) {
        if (list == null) {
            return Collections.emptyList();
        }

        Map<String, DeduplicatedItem> uniqueMap = new LinkedHashMap<>();
        for (OpportunityTransformer.TransformedOpportunity opp : list) {
            String hash = computeHash(opp);
            if (!uniqueMap.containsKey(hash)) {
                uniqueMap.put(hash, new DeduplicatedItem(opp, hash));
            }
        }

        return new ArrayList<>(uniqueMap.values());
    }

    /**
     * Calcula hash determinístico dos campos chave da oportunidade.
     */
    public String computeHash(OpportunityTransformer.TransformedOpportunity opp) {
        String canonical = String.format("%s|%s|%s|%s",
                opp.title().toLowerCase(Locale.ROOT),
                opp.institution().toLowerCase(Locale.ROOT),
                opp.description().toLowerCase(Locale.ROOT),
                String.join(",", opp.normalizedTechnologies().stream().sorted().toList())
        );

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não disponível no runtime", e);
        }
    }
}
