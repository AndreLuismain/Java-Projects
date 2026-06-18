package com.sandbox.estudos_api.service;


import com.sandbox.estudos_api.repository.ItemCustoRepository;
import com.sandbox.estudos_api.repository.ObraRepository;
import com.sandbox.estudos_api.model.ItemCusto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustosService {

    private final ItemCustoRepository itemCustoRepository;
    private final ObraRepository obraRepository;

    public CustosService(ItemCustoRepository itemCustoRepository, ObraRepository obraRepository) {
        this.itemCustoRepository = itemCustoRepository;
        this.obraRepository = obraRepository;
    }

    //Lista itens de custo de uma obra
    public List<ItemCusto> listarCustosPorObra(Long obraId) {
        // Validação de negócio: verifica se a obra realmente existe antes de buscar os custos
        if (!obraRepository.existsById(obraId)) {
            throw new IllegalArgumentException("Obra não encontrada com o ID informado.");
        }
        return itemCustoRepository.findByObraId(obraId);
    }

    //Retorna custo total agrupado usando @Query




}
