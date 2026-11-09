package br.com.minirpg.api.service;

import br.com.minirpg.api.model.Personagem;
import br.com.minirpg.api.repository.PersonagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // Esta anotação avisa o Spring que esta classe contém as regras de negócio
public class PersonagemService {

    @Autowired
    private PersonagemRepository repository;

    // A nossa regra de negócio: O método de atacar!
    public String atacar(Long idAtacante, Long idAlvo) {

        // 1. Buscar os dois personagens no banco
        Personagem atacante = repository.findById(idAtacante)
                .orElseThrow(() -> new RuntimeException("Atacante não encontrado com ID: " + idAtacante));

        Personagem alvo = repository.findById(idAlvo)
                .orElseThrow(() -> new RuntimeException("Alvo não encontrado com ID: " + idAlvo));

        // 2. Regra: Não pode bater em quem já está caído
        if (alvo.getPontosVida() <= 0) {
            return alvo.getNome() + " já está derrotado! Pare de bater nele!";
        }

        // 3. Regra matemática do dano (Nível * 2 + Agilidade)
        int dano = (atacante.getNivel() * 2) + atacante.getAgilidade();
        int novaVida = alvo.getPontosVida() - dano;

        // 4. Regra: A vida não pode ficar negativa
        if (novaVida < 0) {
            novaVida = 0;
        }

        // 5. Atualizar o alvo e salvar no banco
        alvo.setPontosVida(novaVida);
        repository.save(alvo);

        // 6. Retornar o "log" da batalha
        return atacante.getNome() + " atacou " + alvo.getNome() +
                " causando " + dano + " de dano! Vida restante do alvo: " + novaVida;
    }
}