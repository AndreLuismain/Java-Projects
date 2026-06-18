package com.sandbox.estudos_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Gera todos os Getters, Setters, toString, etc.
@NoArgsConstructor // Construtor vazio (obrigatório para o Spring/Hibernate)
@AllArgsConstructor // Construtor com todos os argumentos
@Entity
@Table(name = "distribuidora") //adiocionar nome do arquivo aqui
public class Distribuidora {
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Column(nullable = false, length = 500) //add name calum
    private String nome;

    @NotBlank(message = "A sigla é obrigatório.")
    @Size(min = 2, max = 5, message = "A sigla deve ter entre 2 e 5 caracteres")
    @Column(nullable = false, length = 500) //add name calum
    private String sigla;

    @NotBlank(message = "O estado é obrigatório.")
    @Column(nullable = false, length = 500) //add name calum
    private String estado;
}
