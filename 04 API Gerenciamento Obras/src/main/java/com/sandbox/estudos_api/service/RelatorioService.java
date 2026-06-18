package com.sandbox.estudos_api.service;

import com.sandbox.estudos_api.repository.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private ObraRepository obraRepository;

    public List<ObraRepository.CustoPorDistribuidoraProjection> obterRelatorioCustoDistribuidora() {
        return obraRepository.obterCustoTotalPorDistribuidora();
    }
}
