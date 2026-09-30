package br.com.andreluismain.agendamento.dto.response;

import br.com.andreluismain.agendamento.domain.model.Resource;
import br.com.andreluismain.agendamento.domain.model.ResourceType;

import java.util.UUID;

/**
 * Resposta com os detalhes de um recurso acadêmico.
 */
public record ResourceResponse(
        UUID id,
        String name,
        ResourceType type,
        Integer capacity,
        Boolean active
) {
    public static ResourceResponse from(Resource res) {
        return new ResourceResponse(
                res.getId(),
                res.getName(),
                res.getType(),
                res.getCapacity(),
                res.getActive()
        );
    }
}
