package com.sandbox.estudos_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data // Gera todos os Getters, Setters, toString, etc.
@NoArgsConstructor // Construtor vazio (obrigatório para o Spring/Hibernate)
@AllArgsConstructor // Construtor com todos os argumentos
@Entity
@Table(name = "obra") //Add nome do csv
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // length faz sentido aqui pois é uma String (VARCHAR)
    @Column(nullable = false, length = 500)
    private String descricao;

    // Removido o length 500 (não se aplica a datas)
    @Column(nullable = false)
    private LocalDate dataInicio;

    // Removido o length 500 (não se aplica a datas)
    @Column(nullable = false)
    private LocalDate dataFim;

    // Removido o length 500 (para BigDecimal, use precision/scale se necessário)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal custoTotal;

    @ManyToOne
    @JoinColumn(name = "distribuidora_id", nullable = false)
    private Distribuidora distribuidora;

    //Apostila Task 1.3
    public void atualizarCusto(BigDecimal novoCusto){
        if (novoCusto == null || novoCusto.compareTo(BigDecimal.ZERO) <= 0 ){
            throw new IllegalArgumentException("O custo tem que ser maior que zero!");
        }
        this.custoTotal = novoCusto;
    }
}
