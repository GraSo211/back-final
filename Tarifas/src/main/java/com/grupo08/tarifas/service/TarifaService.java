package com.grupo08.tarifas.service;


import com.grupo08.tarifas.model.Tarifa;
import com.grupo08.tarifas.repository.TarifaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TarifaService {
    @Autowired
    private TarifaRepository tarifaRepository;

    public List<Tarifa> getAll(){
        List<Tarifa> tarifaList = tarifaRepository.findAll();
        return tarifaList;
    }

    public Tarifa getById(long id){
        Optional<Tarifa> tarifa = tarifaRepository.findById(id);
        return tarifa.orElse(null);
    }

    public Tarifa post(Tarifa tarifaB){
        Tarifa tarifa = tarifaRepository.save(tarifaB);
        return tarifa;
    }

    public Tarifa put(long id, Tarifa tarifaB){
        Optional<Tarifa> tarifa = tarifaRepository.findById(id);
        if(tarifa.isPresent()){
            Tarifa tarifaUpdate = tarifa.get();

            tarifaUpdate.setTipoTarifa(tarifaB.getTipoTarifa());
            tarifaUpdate.setDefinicion(tarifaB.getDefinicion());
            tarifaUpdate.setDiaSemana(tarifaB.getDiaSemana());
            tarifaUpdate.setDiaMes(tarifaB.getDiaMes());
            tarifaUpdate.setMes(tarifaB.getMes());
            tarifaUpdate.setAnio(tarifaB.getAnio());
            tarifaUpdate.setMontoFijoAlquiler(tarifaB.getMontoFijoAlquiler());
            tarifaUpdate.setMontoMinutoFraccion(tarifaB.getMontoMinutoFraccion());
            tarifaUpdate.setMontoHora(tarifaB.getMontoHora());
            tarifaUpdate.setMontoKm(tarifaB.getMontoKm());
            return tarifaRepository.save(tarifaUpdate);
        }else{
            return null;
        }
    }



    public void delete(long id){
        tarifaRepository.deleteById(id);
    }
}
