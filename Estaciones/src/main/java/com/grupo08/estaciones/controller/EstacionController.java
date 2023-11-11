package com.grupo08.estaciones.controller;


import com.grupo08.estaciones.model.Estacion;
import com.grupo08.estaciones.service.EstacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estaciones")
public class EstacionController {
    @Autowired
    private EstacionService estacionService ;

    @GetMapping()
    public ResponseEntity<List<Estacion>> getAll(){
        List<Estacion> estacionList = estacionService.getAll();
        return ResponseEntity.ok(estacionList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estacion> getById(@PathVariable long id){
        Estacion estacion = estacionService.getById(id);
        return ResponseEntity.ok(estacion);
    }

    @PostMapping("/admin")
    public ResponseEntity<Estacion> post(@RequestBody Estacion estacionB){
        Estacion estacion = estacionService.post(estacionB);
        return ResponseEntity.ok(estacion);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<Estacion> put(@PathVariable long id, @RequestBody Estacion estacionB){
        Estacion estacion = estacionService.put(id,estacionB);
        if(estacion != null) {
            return ResponseEntity.ok(estacion);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        estacionService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/estacionMasCercana")
    public ResponseEntity<Estacion> prueba(@RequestParam(name = "latitud") Double latitudC, @RequestParam(name = "longitud") Double longitudC){
        Estacion estacion = estacionService.menorDistante(latitudC,longitudC);
        return ResponseEntity.ok(estacion);
    }
}
