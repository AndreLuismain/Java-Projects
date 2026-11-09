package com.sandbox.estudos_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorDeErrosGlobais {

    // 1. Trata a exceção que você usou nos seus Services (IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroPadrao> tratarRegraDeNegocio(IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // Retorna 400

        ErroPadrao erro = new ErroPadrao(
                LocalDateTime.now(),
                status.value(),
                "Erro de Validação/Regra de Negócio",
                ex.getMessage(), // Pega a mensagem exata que você escreveu no Service
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }

    // 2. Trata os erros do @Valid (ex: quando tentar criar Distribuidora sem nome)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErrosDeValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();

        // Pega todos os campos que falharam e suas mensagens
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }
}