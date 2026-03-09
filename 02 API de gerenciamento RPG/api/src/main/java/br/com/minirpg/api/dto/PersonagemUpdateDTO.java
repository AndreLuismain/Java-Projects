package br.com.minirpg.api.dto;

import jakarta.validation.constraints.*;

public record PersonagemUpdateDTO(
        @NotBlank String nome,
        @Min(1) int nivel,
        @PositiveOrZero int pontosVida,
        @PositiveOrZero int agilidade
) {}