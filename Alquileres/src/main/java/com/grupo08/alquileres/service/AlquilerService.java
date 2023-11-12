package com.grupo08.alquileres.service;

import com.grupo08.alquileres.model.Alquiler;
import com.grupo08.alquileres.repository.AlquilerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;

    public List<Alquiler> getAll(){
        List<Alquiler> alquilerList = alquilerRepository.findAll();
        return alquilerList;
    }

    public Alquiler getById(long id){
        Optional<Alquiler> alquiler = alquilerRepository.findById(id);
        return alquiler.orElse(null);
    }

    public Alquiler post(Alquiler alquilerB){
        Alquiler alquiler = alquilerRepository.save(alquilerB);
        System.out.println(alquiler);
        return alquiler;
    }

    public Alquiler put(long id, Alquiler alquilerB){
        Optional<Alquiler> alquiler = alquilerRepository.findById(id);
        if(alquiler.isPresent()){
            Alquiler alquilerUpdate = alquiler.get();
            alquilerUpdate.setIdCliente(alquilerB.getIdCliente());
            alquilerUpdate.setEstado(alquilerB.getEstado());
            alquilerUpdate.setEstacionRetiro(alquilerB.getEstacionRetiro());
            alquilerUpdate.setEstacionDevolucion(alquilerB.getEstacionDevolucion());
            alquilerUpdate.setFechaHoraRetiro(alquilerB.getFechaHoraRetiro());
            alquilerUpdate.setFechaHoraDevolucion(alquilerB.getFechaHoraDevolucion());
            alquilerUpdate.setMonto(alquilerB.getMonto());
            return alquilerRepository.save(alquilerUpdate);
        }else{
            return null;
        }
    }

    public void delete(long id){
        alquilerRepository.deleteById(id);
    }


}
