package com.grupo08.alquileres.controller;

import com.grupo08.alquileres.model.Alquiler;
import com.grupo08.alquileres.model.ResponseDTO;
import com.grupo08.alquileres.service.AlquilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {
    @Autowired
    private AlquilerService alquilerService;

    @GetMapping("/")
    public ResponseEntity<List<ResponseDTO>> getAll(){
        ResponseEntity<List<ResponseDTO>> responseEntity = alquilerService.getAll();
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable long id){
        return alquilerService.getById(id);
    }

    @PostMapping("/")
    public ResponseEntity<Alquiler> post(@RequestBody Alquiler alquilerB){
        Alquiler alquiler = alquilerService.post(alquilerB);
        return ResponseEntity.ok(alquiler);
    }

    @PostMapping("/iniciar")
    public ResponseEntity<Alquiler> postIniciarAlquiler(@RequestBody Alquiler alquilerB){
        alquilerB.setFechaHoraRetiro(LocalDateTime.now());
        alquilerB.setFechaHoraDevolucion(null);
        alquilerB.setEstado(1);
        alquilerB.setEstacionDevolucion(null);
        alquilerB.setMonto(null);
        alquilerB.setIdTarifa(null);
        Alquiler alquiler = alquilerService.postIniciarAlquiler(alquilerB);
        return ResponseEntity.ok(alquiler);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alquiler> put(@PathVariable long id, @RequestBody Alquiler alquilerB){
        Alquiler alquiler = alquilerService.put(id,alquilerB);
        if(alquiler != null) {
            return ResponseEntity.ok(alquiler);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/finalizar/{id}")
    public ResponseEntity<Alquiler> putFinalizarAlquiler(@PathVariable long id, @RequestParam(required = false, defaultValue = "ARS", name="moneda") String moneda, @RequestBody Alquiler alquilerB) {
        Alquiler alquiler = alquilerService.putFinalizarAlquiler(id, moneda, alquilerB);
        if (alquiler != null) {
            return ResponseEntity.ok(alquiler);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        alquilerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<Alquiler>> getFiltered(@RequestParam(name = "estado") int estado) {
        List<Alquiler> alquilerList = alquilerService.getAllByFilter(estado);
        if (alquilerList.isEmpty()) {
            return ResponseEntity.noContent().build(); // No se encontraron elementos que cumplan el filtro
        }
        return ResponseEntity.ok(alquilerList);
    }




}
