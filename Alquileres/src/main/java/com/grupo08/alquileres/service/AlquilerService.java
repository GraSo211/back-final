package com.grupo08.alquileres.service;

import com.grupo08.alquileres.model.Alquiler;
import com.grupo08.alquileres.model.EstacionDTO;
import com.grupo08.alquileres.model.ResponseDTO;
import com.grupo08.alquileres.model.TarifaDTO;
import com.grupo08.alquileres.repository.AlquilerRepository;
import com.grupo08.estaciones.model.Estacion;
import com.grupo08.tarifas.model.Tarifa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlquilerService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AlquilerRepository alquilerRepository;

    public ResponseEntity<List<ResponseDTO>> getAll(){
        List<ResponseDTO> responseDTOList = new ArrayList<>();
        List<Alquiler> alquilerList = alquilerRepository.findAll();

        if(!alquilerList.isEmpty()){
            for(Alquiler alquiler : alquilerList){
                ResponseEntity<EstacionDTO> responseEntity = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + alquiler.getEstacionRetiro(), EstacionDTO.class);
                EstacionDTO estacionDTO = responseEntity.getBody();

                ResponseEntity<EstacionDTO> responseEntity2 = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + alquiler.getEstacionDevolucion(), EstacionDTO.class);
                EstacionDTO estacionDTO2 = responseEntity2.getBody();

                ResponseEntity<TarifaDTO> responseEntity3 = restTemplate.getForEntity("http://localhost:8083/api/tarifas/" + alquiler.getIdTarifa(), TarifaDTO.class);
                TarifaDTO tarifaDTO = responseEntity3.getBody();

                ResponseDTO responseDTO = new ResponseDTO();
                responseDTO.setAlquiler(alquiler);
                responseDTO.setEstacionRetDTO(estacionDTO);
                responseDTO.setEstacionDevDTO(estacionDTO2);
                responseDTO.setTarifaDTO(tarifaDTO);
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

            ResponseEntity<TarifaDTO> responseEntity3 = restTemplate.getForEntity("http://localhost:8083/api/tarifas/" + alquiler.getIdTarifa(), TarifaDTO.class);
            TarifaDTO tarifaDTO = responseEntity3.getBody();

            responseDTO.setAlquiler(alquiler);
            responseDTO.setEstacionRetDTO(estacionDTO);
            responseDTO.setEstacionDevDTO(estacionDTO2);
            responseDTO.setTarifaDTO(tarifaDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public Alquiler postIniciarAlquiler(Alquiler alquilerB){
        Alquiler alquiler = alquilerRepository.save(alquilerB);
        System.out.println(alquiler);
        return alquiler;
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

    public Alquiler finalizarAlquiler(long idAlquiler, String moneda) {
        Optional<Alquiler> alquilerOptional = alquilerRepository.findById(idAlquiler);
        if (alquilerOptional.isPresent()) {
            Alquiler alquiler = alquilerOptional.get();
            alquiler.setFechaHoraDevolucion(LocalDateTime.now());
            alquiler.setEstado(1); // Estado finalizado

            Optional<Tarifa> tarifaOptional = (Optional<Tarifa>) invocarServicio("tarifas", alquiler.getIdTarifa());

            if (tarifaOptional.isPresent()) {
                Tarifa tarifa = tarifaOptional.get();
                double monto = calcularMonto(alquiler, tarifa.getId());
                alquiler.setMonto(monto);
                if (!moneda.equals("ARS")) {
                    double conversion = new CurrencyConverter().cambioDeMoneda(moneda, monto);
                    alquiler.setMonto(Double.parseDouble(String.valueOf(conversion)));
                }
                alquilerRepository.save(alquiler);
                return alquiler;
            }
        }
        return null;
    }

    private double calcularMonto(Alquiler alquiler, long id) {
        double monto = 0;
        long duracionEnMinutos = ChronoUnit.MINUTES.between(alquiler.getFechaHoraRetiro(), alquiler.getFechaHoraDevolucion());

        // Se solicita un Alquiler al Microservicio de Tarifas una tarifa en particular
        // y almacena la respuesta (tarifa solicitada)
        Tarifa tarifa = (Tarifa) invocarServicio("tarifas", id);

        // Costo fijo por realizar el alquiler
        monto += tarifa.getMontoFijoAlquiler();

        // Costo por hora completa o fraccionado por minuto
        if (duracionEnMinutos > 30) {
            long duracionEnHoras = ChronoUnit.HOURS.between(alquiler.getFechaHoraRetiro(), alquiler.getFechaHoraDevolucion());
            monto += duracionEnHoras * tarifa.getMontoHora();
        } else {
            monto += duracionEnMinutos * tarifa.getMontoMinutoFraccion();
        }

        // Monto adicional por cada KM que separe la estación de retiro de la estación de devolución
        // double distancia = calcularDistancia(alquiler.getEstacionRetiro(), alquiler.getEstacionDevolucion());
        // monto += distancia * tarifa.getMontoKm();

        // Descuento para los días promocionales
        // No encuentro los dias de la semana en los que hay descuento en la bse de datos..
        /*if (esDiaPromocional(alquiler.getFechaHoraRetiro())) {
            double descuento = obtenerDescuento(alquiler.getFechaHoraRetiro());
            monto -= monto * descuento;
        }*/

        return monto;
    }

    private double calcularDistancia(long idEstacionRetiro, long idEstacionDevolucion) {
        Optional<Estacion> estacionR = (Optional<Estacion>) invocarServicio("estaciones", idEstacionRetiro);
        Optional<Estacion> estacionD = (Optional<Estacion>) invocarServicio("estaciones", idEstacionDevolucion);

        if (estacionR.isPresent() && estacionD.isPresent()) {
            Estacion estacionRetiro = estacionR.get();
            Estacion estacionDevolucion = estacionD.get();

            double latitud1 = estacionRetiro.getLatitud();
            double longitud1 = estacionRetiro.getLongitud();
            double latitud2 = estacionDevolucion.getLatitud();
            double longitud2 = estacionDevolucion.getLongitud();

            // Calcular la distancia euclídea en grados
            double distancia = Math.sqrt(Math.pow(latitud2 - latitud1, 2) + Math.pow(longitud2 - longitud1, 2));

            // Convertir la distancia a KM (1 grado = 110000 m)
            distancia *= 110000;

            return distancia;
        }

        return 0;
    }


    public Object invocarServicio(String tipoServicio, long id) {

        // Creación de una instancia de RestTemplate
        try {
            // Creación de la instancia de RequestTemplate
            RestTemplate template = new RestTemplate();

            switch (tipoServicio) {
                case "tarifas":
                    // Se realiza una petición a http://localhost:8082/api/tarifas/{id}, indicando que id
                    // respuesta de la petición tendrá en su cuerpo a un objeto del tipo Tarifa.
                    ResponseEntity<Tarifa> resTarifa = template.getForEntity(
                            "http://localhost:8083/api/tarifas/{id}", Tarifa.class, id
                    );
                    // Se comprueba si el código de repuesta es de la familia 200 y entrega el objeto de tipo Tarifa
                    // para su disposición
                    if (resTarifa.getStatusCode().is2xxSuccessful()) {
                        return resTarifa.getBody();
                    } else {
                        return resTarifa.getHeaders();
                    }

                case "alquileres":
                    // Se realiza una petición a http://localhost:8082/api/alquileres/{id}, indicando que id
                    // respuesta de la petición tendrá en su cuerpo a un objeto del tipo Alquiler.
                    ResponseEntity<Alquiler> resAlquiler = template.getForEntity(
                            "http://localhost:8082/api/alquileres/{id}", Alquiler.class, id
                    );
                    // Se comprueba si el código de repuesta es de la familia 200 y entrega el objeto de tipo Tarifa
                    // para su disposición
                    if (resAlquiler.getStatusCode().is2xxSuccessful()) {
                        return resAlquiler.getBody();
                    } else {
                        return resAlquiler.getHeaders();
                    }
                case "estaciones":
                    // Se realiza una petición a http://localhost:8082/api/alquileres/{id}, indicando que id
                    // respuesta de la petición tendrá en su cuerpo a un objeto del tipo Alquiler.
                    ResponseEntity<Estacion> resEstacion = template.getForEntity(
                            "http://localhost:8081/api/estaciones/{id}", Estacion.class, id
                    );
                    // Se comprueba si el código de repuesta es de la familia 200 y entrega el objeto de tipo Tarifa
                    // para su disposición
                    if (resEstacion.getStatusCode().is2xxSuccessful()) {
                        return resEstacion.getBody();
                    } else {
                        return resEstacion.getHeaders();
                    }
                default:
                    return null;
            }

        } catch (HttpClientErrorException ex) {
            // La repuesta no es exitosa.
            return null;
        }
    }


}

