package com.grupo08.alquileres.service;

import com.grupo08.alquileres.model.Alquiler;
import com.grupo08.alquileres.model.EstacionDTO;
import com.grupo08.alquileres.model.ResponseDTO;
import com.grupo08.alquileres.repository.AlquilerRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpResponseDecorator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;


    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<List<ResponseDTO>> getAll(){
        List<ResponseDTO> responseDTOList = new ArrayList<>();
        List<Alquiler> alquilerList = alquilerRepository.findAll();

        if(!alquilerList.isEmpty()){
            for(Alquiler alquiler : alquilerList){
                ResponseEntity<EstacionDTO> responseEntity = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + alquiler.getEstacionRetiro(), EstacionDTO.class);
                EstacionDTO estacionDTO = responseEntity.getBody();

                ResponseEntity<EstacionDTO> responseEntity2 = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + alquiler.getEstacionDevolucion(), EstacionDTO.class);
                EstacionDTO estacionDTO2 = responseEntity2.getBody();

                ResponseDTO responseDTO = new ResponseDTO();
                responseDTO.setAlquiler(alquiler);
                responseDTO.setEstacionRetDTO(estacionDTO);
                responseDTO.setEstacionDevDTO(estacionDTO2);

                responseDTOList.add(responseDTO);
            }
            return new ResponseEntity<>(responseDTOList, HttpStatus.OK);

        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseDTO> getById(long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        Optional<Alquiler> optionalAlquiler = alquilerRepository.findById(id);
        if (optionalAlquiler.isPresent()) {
            Alquiler alquiler = optionalAlquiler.get();
            ResponseEntity<EstacionDTO> responseEntity = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + alquiler.getEstacionRetiro(), EstacionDTO.class);
            EstacionDTO estacionDTO = responseEntity.getBody();
            ResponseEntity<EstacionDTO> responseEntity2 = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + alquiler.getEstacionDevolucion(), EstacionDTO.class);
            EstacionDTO estacionDTO2 = responseEntity2.getBody();
            responseDTO.setAlquiler(alquiler);
            responseDTO.setEstacionRetDTO(estacionDTO);
            responseDTO.setEstacionDevDTO(estacionDTO2);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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


    public List<Alquiler> getAllByFilter(int estado) {
        List<Alquiler> alquilerList = alquilerRepository.findByEstado(estado);
        return alquilerList;
    }



}
