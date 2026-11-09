package com.sandbox.estudos_api.exception;

import java.time.LocalDateTime;

// Usando record (Java 14+) para criar um DTO de erro limpo e imutável
public record ErroPadrao(
        LocalDateTime timestamp,
        Integer status,
        String erro,
        String mensagem,
        String path
) {}