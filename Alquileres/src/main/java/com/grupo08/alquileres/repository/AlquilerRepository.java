package com.grupo08.alquileres.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo08.alquileres.model.Alquiler;

import java.util.List;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler,Long> {
    List<Alquiler> findByEstado(int estado);

}
