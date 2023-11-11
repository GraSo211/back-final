package com.grupo08.alquileres.controller;




import com.grupo08.alquileres.model.Alquiler;
import com.grupo08.alquileres.service.AlquilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {
    @Autowired
    private AlquilerService alquilerService;

    @GetMapping
    public ResponseEntity<List<Alquiler>> getAll(){
        List<Alquiler> alquilerList = alquilerService.getAll();
        return ResponseEntity.ok(alquilerList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alquiler> getById(@PathVariable long id){
        Alquiler alquiler = alquilerService.getById(id);
        return ResponseEntity.ok(alquiler);
    }

    @PostMapping
    public ResponseEntity<Alquiler> post(@RequestBody Alquiler alquilerB){
        Alquiler alquiler = alquilerService.post(alquilerB);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        alquilerService.delete(id);
        return ResponseEntity.noContent().build();
    }




}