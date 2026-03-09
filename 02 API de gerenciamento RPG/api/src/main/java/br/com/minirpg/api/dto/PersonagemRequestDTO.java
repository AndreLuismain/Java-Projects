package br.com.minirpg.api.dto;

import jakarta.validation.constraints.*;

public record PersonagemRequestDTO(
        @NotBlank String nome,
        @NotBlank String classe,
        @Min(1) int nivel,
        @PositiveOrZero int pontosVida,
        @PositiveOrZero int agilidade
) {}