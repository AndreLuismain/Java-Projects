package br.com.minirpg.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "personagens")
public class Personagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(nullable = false, length = 30)
    private String classe;

    private Integer nivel;

    @Column(name = "pontos_vida")
    private Integer pontosVida;

    // Construtores vazios são necessários no Spring
    public Personagem() {}

    public Personagem(String nome, String classe, Integer nivel, Integer pontosVida) {
        this.nome = nome;
        this.classe = classe;
        this.nivel = nivel;
        this.pontosVida = pontosVida;
    }
    // Setters (para podermos modificar os dados via código)
    public void setNome(String nome) { this.nome = nome; }
    public void setClasse(String classe) { this.classe = classe; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }
    public void setPontosVida(Integer pontosVida) { this.pontosVida = pontosVida; }
    // Getters (para o Spring conseguir ler e exibir os dados)
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getClasse() { return classe; }
    public Integer getNivel() { return nivel; }
    public Integer getPontosVida() { return pontosVida; }
}