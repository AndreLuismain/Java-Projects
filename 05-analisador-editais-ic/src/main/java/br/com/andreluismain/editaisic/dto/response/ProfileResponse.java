package br.com.andreluismain.editaisic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO com dados de um perfil acadêmico retornado pela API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private UUID id;
    private String name;
    private String description;
    private List<String> keywords;
    private Instant createdAt;
    private Instant updatedAt;
}
