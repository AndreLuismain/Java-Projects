package br.com.andreluismain.editaisic.domain.repository;

import br.com.andreluismain.editaisic.domain.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório para operações de persistência de perfis acadêmicos.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}
