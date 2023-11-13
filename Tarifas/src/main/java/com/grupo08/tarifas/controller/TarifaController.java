package com.grupo08.tarifas.controller;


import com.grupo08.tarifas.model.Tarifa;

import com.grupo08.tarifas.service.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {
    @Autowired
    private TarifaService tarifaService ;

    @GetMapping("/")
    public ResponseEntity<List<Tarifa>> getAll(){
        List<Tarifa> tarifaList = tarifaService.getAll();
        return ResponseEntity.ok(tarifaList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> getById(@PathVariable long id){
        Tarifa tarifa = tarifaService.getById(id);
        return ResponseEntity.ok(tarifa);
    }

    @PostMapping("/")
    public ResponseEntity<Tarifa> post(@RequestBody Tarifa tarifaB){
        Tarifa tarifa = tarifaService.post(tarifaB);
        return ResponseEntity.ok(tarifa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarifa> put(@PathVariable long id, @RequestBody Tarifa tarifaB){
        Tarifa tarifa = tarifaService.put(id,tarifaB);
        if(tarifa != null) {
            return ResponseEntity.ok(tarifa);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        tarifaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
