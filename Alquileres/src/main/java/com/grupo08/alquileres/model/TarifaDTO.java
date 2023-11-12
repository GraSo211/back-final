package com.grupo08.alquileres.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDTO {
    private long id;
    private int tipoTarifa;
    private char definicion;
    private int diaSemana;
    private Integer diaMes;
    private Integer mes;
    private Integer anio;
    private double montoFijoAlquiler;
    private double montoMinutoFraccion;
    private double montoHora;
    private double montoKm;
}
