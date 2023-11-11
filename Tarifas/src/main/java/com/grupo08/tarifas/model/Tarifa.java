package com.grupo08.tarifas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="TARIFAS")
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "TIPO_TARIFA")
    private int tipoTarifa;
    @Column(name = "DEFINICION")
    private char definicion;
    @Column(name = "DIA_SEMANA")
    private int diaSemana;
    @Column(name = "DIA_MES")
    private Integer diaMes;
    @Column(name = "MES")
    private Integer mes;
    @Column(name = "ANIO")
    private Integer anio;
    @Column(name = "MONTO_FIJO_ALQUILER")
    private double montoFijoAlquiler;
    @Column(name = "MONTO_MINUTO_FRACCION")
    private double montoMinutoFraccion;
    @Column(name = "MONTO_HORA")
    private double montoHora;
    @Column(name = "MONTO_KM")
    private double montoKm;

}
