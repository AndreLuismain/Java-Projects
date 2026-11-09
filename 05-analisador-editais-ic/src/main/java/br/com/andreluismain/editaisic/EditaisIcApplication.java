package br.com.andreluismain.editaisic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Classe principal de inicialização da API Analisador de Editais de IC.
 *
 * <p>Esta aplicação provê uma API REST para coleta, extração e análise de editais
 * acadêmicos (Iniciação Científica, bolsas e estágios), utilizando Spring Boot 3.4+,
 * processamento assíncrono com Spring WebFlux/CompletableFuture e inteligência
 * artificial via Google Gemini Pro.</p>
 *
 * @author André Luís
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
public class EditaisIcApplication {

    /**
     * Ponto de entrada da aplicação.
     *
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(EditaisIcApplication.class, args);
    }
}
