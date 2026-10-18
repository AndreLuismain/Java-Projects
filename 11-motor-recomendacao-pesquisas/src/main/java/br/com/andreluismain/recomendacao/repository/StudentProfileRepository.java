package br.com.andreluismain.recomendacao.repository;

import br.com.andreluismain.recomendacao.domain.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório para persistência de perfis acadêmicos de estudantes.
 */
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, UUID> {
}
