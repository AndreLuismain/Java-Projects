package br.com.andreluismain.gateway.routing;

import br.com.andreluismain.gateway.dto.RouteType;

/**
 * Decisão de roteamento contendo a rota escolhida e o score de complexidade calculado.
 */
public record RouteDecision(
        RouteType route,
        int complexityScore,
        String explanation
) {}
