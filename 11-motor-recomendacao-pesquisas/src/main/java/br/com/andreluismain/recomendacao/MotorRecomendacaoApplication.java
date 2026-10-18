package br.com.andreluismain.recomendacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do microserviço Motor de Recomendação de Pesquisas Acadêmicas.
 */
@SpringBootApplication
public class MotorRecomendacaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotorRecomendacaoApplication.class, args);
    }
}
