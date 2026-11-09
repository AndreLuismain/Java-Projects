package com.sandbox.estudos_api.repository;


import com.sandbox.estudos_api.model.Distribuidora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface DistribuidoraRepository extends JpaRepository<Distribuidora,Long> {



}
