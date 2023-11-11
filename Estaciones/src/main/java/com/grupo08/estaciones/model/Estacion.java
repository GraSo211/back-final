package com.grupo08.estaciones.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="ESTACIONES")
@NoArgsConstructor
@AllArgsConstructor
public class Estacion {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "FECHA_HORA_CREACION")
    private LocalDateTime fechaHoraCreacion;
    @Column(name = "LATITUD")
    private Double latitud;
    @Column(name = "LONGITUD")
    private Double longitud;

    @PrePersist
    public void prePersist() {
        this.fechaHoraCreacion = LocalDateTime.now();
    }
}
