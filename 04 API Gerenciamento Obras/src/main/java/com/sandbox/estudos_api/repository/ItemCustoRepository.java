package com.sandbox.estudos_api.repository;

import com.sandbox.estudos_api.model.ItemCusto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import  java.util.List;

@Repository
public interface ItemCustoRepository extends JpaRepository<ItemCusto,Long> {

    // Busca todos os itens de custo vinculados a uma obra específica
    List<ItemCusto> findByObraId(Long obraId);
}
