package br.com.andreluismain.pix.repository;

import br.com.andreluismain.pix.domain.model.WalletAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para persistência e bloqueio determinístico de contas de carteira.
 */
@Repository
public interface WalletAccountRepository extends JpaRepository<WalletAccount, UUID> {

    Optional<WalletAccount> findByPixKey(String pixKey);

    boolean existsByPixKey(String pixKey);

    /**
     * Busca conta com bloqueio pessimista de escrita (SELECT FOR UPDATE).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM WalletAccount a WHERE a.id = :id")
    Optional<WalletAccount> findByIdWithLock(@Param("id") UUID id);
}
