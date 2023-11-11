package com.grupo08.tarifas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo08.tarifas.model.Tarifa;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa,Long> {

}
