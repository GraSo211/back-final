package com.grupo08.alquileres.model;

import com.grupo08.estaciones.model.Estacion;
import com.grupo08.tarifas.model.Tarifa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="ALQUILERES")
@NoArgsConstructor
@AllArgsConstructor
public class Alquiler {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "ID_CLIENTE")
    private long idCliente;
    @Column(name = "ESTADO")
    private int estado;
    @JoinColumn(name = "ESTACION_RETIRO", referencedColumnName = "ID")
    @OneToOne
    private Estacion estacionRetiro;
    @JoinColumn(name = "ESTACION_DEVOLUCION", referencedColumnName = "ID")
    @OneToOne
    private Estacion estacionDevolucion;
    @Column(name = "FECHA_HORA_RETIRO")
    private LocalDateTime fechaHoraRetiro;
    @Column(name = "FECHA_HORA_DEVOLUCION")
    private LocalDateTime fechaHoraDevolucion;
    @Column(name = "MONTO")
    private double monto;
    @JoinColumn(name = "ID_TARIFA", referencedColumnName = "ID")
    @OneToOne
    private Tarifa idTarifa;
}
