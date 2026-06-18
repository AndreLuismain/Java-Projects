package com.sandbox.estudos_api.repository;


import com.sandbox.estudos_api.model.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.math.BigDecimal;

@Repository
public interface ObraRepository extends JpaRepository<Obra,Long> {

    // Busca todas as obras de uma distribuidora específica onde o custo seja maior ou igual ao informado
// O nome TEM que ter "CustoTotal" para bater com o atributo "custoTotal" da classe Obra
    List<Obra> findByDistribuidoraIdAndCustoTotalGreaterThanEqual(Long distribuidoraId, java.math.BigDecimal custoMin);

    public interface CustoPorDistribuidoraProjection {
        String getNomeDistribuidora();
        Double getCustoTotal();
    }

    // JPQL utilizando agregação e agrupamento por distribuidora
    @Query("SELECT o.distribuidora.nome AS nomeDistribuidora, SUM(o.custoTotal) AS custoTotal " +
            "FROM Obra o " +
            "GROUP BY o.distribuidora.nome")
    List<CustoPorDistribuidoraProjection> obterCustoTotalPorDistribuidora();
}
