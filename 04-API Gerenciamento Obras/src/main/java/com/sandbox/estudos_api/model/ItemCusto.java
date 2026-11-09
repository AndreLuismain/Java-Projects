package com.sandbox.estudos_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data // Gera todos os Getters, Setters, toString, etc.
@NoArgsConstructor // Construtor vazio (obrigatório para o Spring/Hibernate)
@AllArgsConstructor // Construtor com todos os argumentos
@Entity
@Table(name = "itens_custo") // Nome amigável para o banco de dados
public class ItemCusto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Apenas removi o name="". O banco criará a coluna como "descricao"
    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCusto tipoCusto; // É boa prática que classes e Enums comecem com letra maiúscula

    // Removido o CascadeType.ALL para proteger a Obra de ser deletada acidentalmente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obra_id", nullable = false) // A chave estrangeira será "obra_id"
    private Obra obra;
}
