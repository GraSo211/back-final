package com.grupo08.alquileres.model;


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
        @Column(name = "ESTACION_RETIRO")
        private Long estacionRetiro;
        @Column(name = "ESTACION_DEVOLUCION")
        private Long estacionDevolucion;
        @Column(name = "FECHA_HORA_RETIRO")
        private LocalDateTime fechaHoraRetiro;
        @Column(name = "FECHA_HORA_DEVOLUCION")
        private LocalDateTime fechaHoraDevolucion;
        @Column(name = "MONTO")
        private Double monto;
        @Column(name = "ID_TARIFA")
        private Long idTarifa;
    }
