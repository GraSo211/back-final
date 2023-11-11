package com.grupo08.estaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo08.estaciones.model.Estacion;

@Repository
public interface EstacionRepository extends JpaRepository<Estacion,Long> {
}
