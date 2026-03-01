package br.com.minirpg.api.controller;

import br.com.minirpg.api.model.Personagem;
import br.com.minirpg.api.repository.PersonagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personagens")
public class PersonagemController {

    @Autowired
    private PersonagemRepository repository;

    // O que já tínhamos (Busca todos)
    @GetMapping
    public List<Personagem> listarTodos() {
        return repository.findAll();
    }

    // A MÁGICA NOVA: Cadastrando um personagem
    @PostMapping
    public Personagem criar(@RequestBody Personagem personagem) {
        return repository.save(personagem); // O Spring faz o INSERT automático!
    }
    @PutMapping("/{id}")
    public Personagem atualizar(@PathVariable Long id, @RequestBody Personagem personagemAtualizado) {
        // 1. Busca no banco pelo ID. Se não achar, dá um erro genérico (vamos melhorar isso depois)
        Personagem existente = repository.findById(id).orElseThrow();

        // 2. Atualiza os dados do objeto em memória
        existente.setNome(personagemAtualizado.getNome());
        existente.setClasse(personagemAtualizado.getClasse());
        existente.setNivel(personagemAtualizado.getNivel());
        existente.setPontosVida(personagemAtualizado.getPontosVida());

        // 3. Salva por cima (O Spring sabe que é UPDATE porque o ID já existe)
        return repository.save(existente);
    }

    // A MÁGICA NOVA: Deletar (DELETE)
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id); // Monta o "DELETE FROM personagens WHERE id = ?"
    }
}
