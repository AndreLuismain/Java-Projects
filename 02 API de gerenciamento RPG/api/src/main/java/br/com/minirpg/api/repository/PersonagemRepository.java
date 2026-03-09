package br.com.minirpg.api.repository;

import br.com.minirpg.api.model.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonagemRepository extends JpaRepository<Personagem, Long> {
}