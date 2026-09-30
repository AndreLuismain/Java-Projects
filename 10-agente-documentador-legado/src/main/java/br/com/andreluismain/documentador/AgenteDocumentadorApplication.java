package br.com.andreluismain.documentador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do microserviço Agente Documentador de Código Legado.
 */
@SpringBootApplication
public class AgenteDocumentadorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgenteDocumentadorApplication.class, args);
    }
}
