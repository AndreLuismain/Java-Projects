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

    //add name column
    @Column(nullable = false, length = 500)
    private String descricao;

    //add name column
    @Column(nullable = false, length = 500)
    private LocalDate dataInicio;

    //add name column
    @Column(nullable = false, length = 500)
    private LocalDate dataFim;

    //add name column
    @Column(nullable = false, length = 500)
    private BigDecimal custoTotal;

    //add name column
    @ManyToOne
    @JoinColumn(name = "distribuidora_id", nullable = false)
    private Distribuidora distribuidora;
}
