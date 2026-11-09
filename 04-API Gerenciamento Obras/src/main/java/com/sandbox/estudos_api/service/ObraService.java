package com.sandbox.estudos_api.service;


import com.sandbox.estudos_api.repository.DistribuidoraRepository;
import com.sandbox.estudos_api.repository.ObraRepository;
import com.sandbox.estudos_api.model.Distribuidora;
import com.sandbox.estudos_api.model.Obra;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;


@Service
public class ObraService {

    private final ObraRepository obrarepository;
    private final DistribuidoraRepository distribuidoraRepository;

    //Injeção de contrutor
    public ObraService(ObraRepository repository,  DistribuidoraRepository distribuidoraRepository) {
        this.obrarepository = repository;
        this.distribuidoraRepository = distribuidoraRepository;
    }

    //ENDPOINTS
    //Lista com Filtros
    // O metodo recebe as variáveis prontas, sem precisar do Scanner
    public List<Obra> buscarObrasComFiltro(Long distribuidoraId, Double custoMin) {

        if (distribuidoraId == null) {
            throw new IllegalArgumentException("O ID da distribuidora é obrigatório para a busca.");
        }

        // Converte o Double (da URL) para BigDecimal
        java.math.BigDecimal custoFiltro = (custoMin != null) ? java.math.BigDecimal.valueOf(custoMin) : java.math.BigDecimal.ZERO;

        // Chamando o método com o nome corrigido
        return obrarepository.findByDistribuidoraIdAndCustoTotalGreaterThanEqual(distribuidoraId, custoFiltro);
    }

    public record ObraRequestDTO(
            String descricao,
            java.time.LocalDate dataInicio,
            java.time.LocalDate dataFim,
            BigDecimal custoTotal,
            Long distribuidoraId) {}

    //Cria Obra vinculada a distribuidora
    @Transactional // Garante a consistência do banco de dados
    public Obra criarObra(ObraRequestDTO dto) {

        // 1. Busca a distribuidora pelo ID enviado no DTO
        // Se não encontrar, lança uma exceção de negócio
        Distribuidora distribuidora = distribuidoraRepository.findById(dto.distribuidoraId())
                .orElseThrow(() -> new IllegalArgumentException("Distribuidora não encontrada com o ID informado."));

        // 2. Instancia a nova Obra e mapeia os dados do DTO para a Entidade
        Obra novaObra = new Obra();
        novaObra.setDescricao(dto.descricao());
        novaObra.setDataInicio(dto.dataInicio());
        novaObra.setDataFim(dto.dataFim());
        novaObra.setCustoTotal(dto.custoTotal());

        // 3. Faz o vínculo do relacionamento
        novaObra.setDistribuidora(distribuidora);

        // 4. Salva no banco de dados com o vínculo estabelecido
        return obrarepository.save(novaObra);
    }
}
