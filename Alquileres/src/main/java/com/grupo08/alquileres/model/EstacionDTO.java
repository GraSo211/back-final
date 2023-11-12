package com.grupo08.alquileres.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstacionDTO {
    private long id;
    private String nombre;
    private Double latitud;
    private Double longitud;

}
