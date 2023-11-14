package com.grupo08.estaciones.service;


import com.grupo08.estaciones.model.Estacion;
import com.grupo08.estaciones.repository.EstacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.DoubleBuffer;
import java.util.List;
import java.util.Optional;

@Service
public class EstacionService {
    @Autowired
    private EstacionRepository estacionRepository;

    public List<Estacion> getAll(){
        List<Estacion> estacionList = estacionRepository.findAll();
        return estacionList;
    }

    public Estacion getById(long id){
        Optional<Estacion> estacion = estacionRepository.findById(id);
        return estacion.orElse(null);
    }

    public Estacion post(Estacion estacionB){
        Estacion estacion = estacionRepository.save(estacionB);
        return estacion;
    }

    public Estacion put(long id, Estacion estacionB){
        Optional<Estacion> estacion = estacionRepository.findById(id);
        if(estacion.isPresent()){
            Estacion estacionUpdate = estacion.get();
            estacionUpdate.setNombre(estacionB.getNombre());
            estacionUpdate.setFechaHoraCreacion(estacionB.getFechaHoraCreacion());
            estacionUpdate.setLatitud(estacionB.getLatitud());
            estacionUpdate.setLongitud(estacionB.getLongitud());
            return estacionRepository.save(estacionUpdate);
        }else{
            return null;
        }
    }
    public void delete(long id){
        estacionRepository.deleteById(id);
    }

    public Estacion getCalcularDistancia(Double latitudC, Double longitudC ){
        //calculamos la distancia de la 1ra estacion y luego la comparamos con las siguientes
        //hacemos esto hasta obtener la menor de todas
        //retornamos esa estacion

        List<Estacion> estacionList = getAll();
        System.out.println(estacionList);

        Estacion estacionMenorDistancia = null;
        Double menorDistancia = 0D;
        for (Estacion estacion:estacionList) {
            Double latitudE = estacion.getLatitud();
            Double longitudE = estacion.getLongitud();
            Double distancia = calcularDistancia(latitudC,longitudC,latitudE,longitudE);
            if(estacionMenorDistancia == null){
                estacionMenorDistancia = estacion;
                menorDistancia = distancia;
            }else{
                if (menorDistancia>distancia){
                    estacionMenorDistancia = estacion;
                    menorDistancia = distancia;
                }
            }
        }
        return estacionMenorDistancia;
    }

    public Double calcularDistancia(Double latitudC, Double longitudC, Double latitudE, Double longitudE ){
        Double distancia = Math.sqrt(Math.pow(latitudE - latitudC,2) + Math.pow(longitudE-longitudC,2));
        return distancia * 110;
    }
}
