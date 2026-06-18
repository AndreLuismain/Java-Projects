package com.sandbox.estudos_api.controller;

//Aqui a gente só adiciona o que vem da internet e mapeia as rotas usando os mapping
//O que costuma ficar: metodos curto e injeção do service via construtor.
//Anotações comuns: @RestController, @RequestMapping, @GetMapping, @PostMapping, @RequestBody, @PathVariable


import com.sandbox.estudos_api.model.ItemCusto;
import com.sandbox.estudos_api.model.Obra;
import com.sandbox.estudos_api.service.CustosService;
import com.sandbox.estudos_api.service.ObraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/obras")
public class ObraController {
    @Autowired
    private ObraService service;

    @Autowired
    private CustosService custosService; // Lembre-se de injetar este service corretamente

    @GetMapping
    public ResponseEntity<List<Obra>> listarObras(
            @RequestParam(name = "distribuidoraId", required = true) Long distribuidoraId,
            @RequestParam(name = "custoMin", required = false) Double custoMin) {

        // Sem try/catch!
        List<Obra> obras = service.buscarObrasComFiltro(distribuidoraId, custoMin);

        if (obras.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(obras);
    }

    @PostMapping
    public ResponseEntity<Obra> criar(@RequestBody ObraService.ObraRequestDTO dto) {
        // Sem try/catch!
        Obra obraSalva = service.criarObra(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obraSalva.getId())
                .toUri();

        return ResponseEntity.created(uri).body(obraSalva);
    }

    @GetMapping("/{id}/custos")
    public ResponseEntity<List<ItemCusto>> listarCustos(@PathVariable Long id) {
        // Sem try/catch!
        List<ItemCusto> custos = custosService.listarCustosPorObra(id);

        if (custos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(custos);
    }
}
