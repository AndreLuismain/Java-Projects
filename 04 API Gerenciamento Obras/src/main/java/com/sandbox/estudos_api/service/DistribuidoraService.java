package com.sandbox.estudos_api.service;

import com.sandbox.estudos_api.repository.DistribuidoraRepository;
import com.sandbox.estudos_api.model.Distribuidora;
import org.springframework.stereotype.Service;
import java.util.List;

/*
Aqui fica Lógica pesada
adicionar aqui a lógica e chamar la no controller pelo return.
 */


@Service
public class DistribuidoraService {

    private final DistribuidoraRepository repository;
    //Injeção de contrutor
    public DistribuidoraService(DistribuidoraRepository repository) {this.repository = repository; }

    //Endpoints
    //Lista todas
    public List<Distribuidora> listarTodas() {
        return repository.findAll();
    }

    //Criar distribuidora com @valid
    public Distribuidora criarDistribuidora(Distribuidora distribuidora) {
        return repository.save(distribuidora);
    }
}
