package br.com.andreluismain.pix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada do microserviço Simulador de Transações Pix.
 */
@SpringBootApplication
@EnableScheduling
public class SimuladorPixApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimuladorPixApplication.class, args);
    }
}
