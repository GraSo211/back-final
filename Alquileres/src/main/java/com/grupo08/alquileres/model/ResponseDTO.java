package com.grupo08.alquileres.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private Alquiler alquiler;
    private EstacionDTO estacionRetDTO;
    private EstacionDTO estacionDevDTO;
    private TarifaDTO tarifaDTO;
}
