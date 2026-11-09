package br.com.minirpg.api.controller;

import br.com.minirpg.api.dto.PersonagemRequestDTO;
import br.com.minirpg.api.dto.PersonagemUpdateDTO;
import br.com.minirpg.api.model.Personagem;
import br.com.minirpg.api.repository.PersonagemRepository;
import br.com.minirpg.api.service.PersonagemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personagens")
public class PersonagemController {

    @Autowired
    private PersonagemRepository repository;

    @Autowired
    private PersonagemService personagemService; // <-- Adicione esta linha

    @GetMapping
    public List<Personagem> listarTodos() {
        return repository.findAll();
    }

    @PostMapping
    public Personagem criar(@Valid @RequestBody PersonagemRequestDTO dto) {
        Personagem personagem = new Personagem();
        personagem.setNome(dto.nome());
        personagem.setClasse(dto.classe());
        personagem.setNivel(dto.nivel());
        personagem.setPontosVida(dto.pontosVida());
        personagem.setAgilidade(dto.agilidade());

        return repository.save(personagem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Personagem> atualizar(@PathVariable Long id, @Valid @RequestBody PersonagemUpdateDTO dto) {
        return repository.findById(id).map(personagem -> {
            personagem.setNome(dto.nome());
            personagem.setNivel(dto.nivel());
            personagem.setPontosVida(dto.pontosVida());
            personagem.setAgilidade(dto.agilidade());

            Personagem atualizado = repository.save(personagem);
            return ResponseEntity.ok(atualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    // A MÁGICA DA BATALHA: Rota customizada
    @PostMapping("/{idAtacante}/atacar/{idAlvo}")
    public ResponseEntity<String> atacar(@PathVariable Long idAtacante, @PathVariable Long idAlvo) {
        // O garçom repassa o pedido para o cozinheiro (Service)
        String resultadoDaBatalha = personagemService.atacar(idAtacante, idAlvo);

        return ResponseEntity.ok(resultadoDaBatalha);
    }
}