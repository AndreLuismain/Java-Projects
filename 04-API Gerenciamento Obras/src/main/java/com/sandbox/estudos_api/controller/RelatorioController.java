package com.sandbox.estudos_api.controller;

import com.sandbox.estudos_api.repository.ObraRepository;
import com.sandbox.estudos_api.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/custo-por-distribuidora")
    public ResponseEntity<List<ObraRepository.CustoPorDistribuidoraProjection>> custoPorDistribuidora() {
        List<ObraRepository.CustoPorDistribuidoraProjection> relatorio = relatorioService.obterRelatorioCustoDistribuidora();

        if (relatorio.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(relatorio);
    }
}
