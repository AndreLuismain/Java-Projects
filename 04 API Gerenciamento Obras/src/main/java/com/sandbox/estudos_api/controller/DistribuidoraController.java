package com.sandbox.estudos_api.controller;

import com.sandbox.estudos_api.model.Distribuidora;
import com.sandbox.estudos_api.service.DistribuidoraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Aqui só chama o metodo pronto, lógica no service
//Aqui a gente só adiciona o que vem da internet e mapeia as rotas usando os mapping
//O que costuma ficar: metodos curto e injeção do service via construtor.
//Anotações comuns: @RestController, @RequestMapping, @GetMapping, @PostMapping, @RequestBody, @PathVariable


@RestController
@RequestMapping("/api/distribuidoras")
public class DistribuidoraController {

    @Autowired
    private DistribuidoraService distribuidoraService;

    //Lista todas
    @GetMapping
    public List<Distribuidora> listarTodas() {
        return distribuidoraService.listarTodas();
    }

    //Cria nova com validação @Valid
    @PostMapping
    public ResponseEntity<Distribuidora> criar(@Valid @RequestBody Distribuidora novaDistribuidora){
        Distribuidora criada = distribuidoraService.criarDistribuidora(novaDistribuidora);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }
}
