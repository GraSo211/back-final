package com.grupo08.alquileres.service;

import com.grupo08.alquileres.model.Alquiler;
import com.grupo08.alquileres.model.EstacionDTO;
import com.grupo08.alquileres.model.ResponseDTO;
import com.grupo08.alquileres.model.TarifaDTO;
import com.grupo08.alquileres.repository.AlquilerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
                Long estacionRetiro = alquiler.getEstacionRetiro();
                Long estacionDevolucion = alquiler.getEstacionDevolucion();
                Long tarifa = alquiler.getIdTarifa();
                if(estacionRetiro == null){
                    estacionRetiro = 0L;
                }
                if(estacionDevolucion == null){
                    estacionDevolucion = 0L;
                }
                if(tarifa == null){
                    tarifa = 0L;
                }

                ResponseEntity<EstacionDTO> responseEntity = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + estacionRetiro, EstacionDTO.class);
                EstacionDTO estacionDTO = responseEntity.getBody();


                ResponseEntity<EstacionDTO> responseEntity2 = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + estacionDevolucion, EstacionDTO.class);
                EstacionDTO estacionDTO2 = responseEntity2.getBody();


                ResponseEntity<TarifaDTO> responseEntity3 = restTemplate.getForEntity("http://localhost:8083/api/tarifas/" + tarifa, TarifaDTO.class);
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
            Long estacionRetiro = alquiler.getEstacionRetiro();
            Long estacionDevolucion = alquiler.getEstacionDevolucion();
            Long tarifa = alquiler.getIdTarifa();
            if(estacionRetiro == null){
                estacionRetiro = 0L;
            }
            if(estacionDevolucion == null){
                estacionDevolucion = 0L;
            }
            if(tarifa == null){
                tarifa = 0L;
            }
            ResponseEntity<EstacionDTO> responseEntity = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + estacionRetiro, EstacionDTO.class);
            EstacionDTO estacionDTO = responseEntity.getBody();

            ResponseEntity<EstacionDTO> responseEntity2 = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + estacionDevolucion, EstacionDTO.class);
            EstacionDTO estacionDTO2 = responseEntity2.getBody();

            ResponseEntity<TarifaDTO> responseEntity3 = restTemplate.getForEntity("http://localhost:8083/api/tarifas/" + tarifa, TarifaDTO.class);
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

    public Alquiler post(Alquiler alquilerB){
        Alquiler alquiler = alquilerRepository.save(alquilerB);
        System.out.println(alquiler);
        return alquiler;
    }

    public Alquiler postIniciarAlquiler(Alquiler alquilerB){
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

    public Alquiler putFinalizarAlquiler(long idAlquiler, String moneda, Alquiler alquilerB) {
        Optional<Alquiler> alquilerOptional = alquilerRepository.findById(idAlquiler);
        if (alquilerOptional.isPresent()) {
            Alquiler alquiler = alquilerOptional.get();
            alquiler.setFechaHoraDevolucion(LocalDateTime.now());
            alquiler.setEstado(2); // Estado finalizado
            alquiler.setEstacionDevolucion(alquilerB.getEstacionDevolucion());
            Long idTarifa = obtenerIdTarifa(alquiler.getFechaHoraRetiro());
            alquiler.setIdTarifa(idTarifa);

            double minutos = calcularMinutos(alquiler.getFechaHoraRetiro(), alquiler.getFechaHoraDevolucion());
            double distancia = calcularDistancia(alquiler.getEstacionRetiro(), alquiler.getEstacionDevolucion());
            TarifaDTO tarifaDTO = obtenerTarifa(idTarifa);

            alquiler.setMonto(calcularMonto(minutos, distancia, tarifaDTO, moneda));

            return alquilerRepository.save(alquiler);

        }
        return null;
    }

    public Double calcularMonto(double minutos, double distancia, TarifaDTO tarifaDTO, String moneda){
        // INICIARLIZAR MONTO
        Double monto = 0D;

        // SUMAR MONTO FIJO
        monto+= tarifaDTO.getMontoFijoAlquiler();

        // CALCULAR HORAS
        double horas;
        if (minutos >=   31 && minutos <= 60){
            horas = 1;
        }else {
            horas = minutos/60;
        }

        // SUMAR MONTO POR HORAS / MINUTOS
        if(minutos<31){
            monto += minutos * tarifaDTO.getMontoMinutoFraccion();
        }else{
            monto += horas * tarifaDTO.getMontoHora();
        }

        // SUMAR MONTO KM
        monto += distancia * tarifaDTO.getMontoKm();

        // CONVERTIR A MONEDA EXTRANJERA
        if (!moneda.equals("ARS")) {
            double conversion = new CurrencyConverter().cambioDeMoneda(moneda, monto);
            conversion = Double.parseDouble(String.valueOf(conversion));
            return conversion;
        }
        return monto;
    }

    public double calcularMinutos(LocalDateTime fecha1, LocalDateTime fecha2){
        double diferenciaEnMinutos = ChronoUnit.MINUTES.between(fecha1, fecha2);
        return diferenciaEnMinutos;

    }

    public TarifaDTO obtenerTarifa(long idTarifa){
        ResponseEntity<TarifaDTO> responseEntity = restTemplate.getForEntity("http://localhost:8083/api/tarifas/" + idTarifa, TarifaDTO.class);
        TarifaDTO tarifaDTO = responseEntity.getBody();
        return tarifaDTO;
    }

    private double calcularDistancia(long idEstacionRetiro, long idEstacionDevolucion) {
        ResponseEntity<EstacionDTO> responseEntity = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + idEstacionRetiro, EstacionDTO.class);
        EstacionDTO estacionRetiroDTO = responseEntity.getBody();
        ResponseEntity<EstacionDTO> responseEntity2 = restTemplate.getForEntity("http://localhost:8081/api/estaciones/" + idEstacionDevolucion, EstacionDTO.class);
        EstacionDTO estacionDevolucionDTO = responseEntity2.getBody();


        if (estacionDevolucionDTO != null && estacionRetiroDTO != null) {
            double latitud1 = estacionRetiroDTO.getLatitud();
            double longitud1 = estacionRetiroDTO.getLongitud();
            double latitud2 = estacionDevolucionDTO.getLatitud();
            double longitud2 = estacionDevolucionDTO.getLongitud();
            // Calcular la distancia euclídea en grados
            double distancia = Math.sqrt(Math.pow(latitud2 - latitud1, 2) + Math.pow(longitud2 - longitud1, 2));
            // Convertir la distancia a KM (1 grado = 110000 m)
            distancia *= 110;

            return distancia;
        }

        return 0;
    }


    public Long obtenerIdTarifa(LocalDateTime fechaRetiro){
        // OBTENEMOS TODAS LAS TARIFAS POSIBLES:
        ResponseEntity<TarifaDTO[]> responseEntity = restTemplate.getForEntity("http://localhost:8083/api/tarifas/", TarifaDTO[].class);
        TarifaDTO[] tarifasDTOArray = responseEntity.getBody();
        List<TarifaDTO> tarifaDTOList = Arrays.asList(tarifasDTOArray);
        int anio = fechaRetiro.getYear();
        int mes = fechaRetiro.getMonthValue();
        int dia = fechaRetiro.getDayOfMonth();
        List<TarifaDTO> tarifasDescuento = new ArrayList<>();
        List<TarifaDTO> tarifasNormales = new ArrayList<>();

        // SEPARAMOS EN 2 LISTAS SEGUN SEAN DE DESCUENTO O NORMALES
        for (TarifaDTO tarifaDTO:
             tarifaDTOList) {
            // AVERIGUAMOS SI ES TARIFA DE DESCUENTO O NORMAL
            if (tarifaDTO.getTipoTarifa() == 1){
                tarifasNormales.add(tarifaDTO);
            }else{
                tarifasDescuento.add(tarifaDTO);
            }
        }

        // ANALIZAMOS PRIMERO LAS DE DESCUENTO PORQUE REQUIEREN DIA ESPECIFICO
        for (TarifaDTO tarifaDTODescuento:
             tarifasDescuento) {
            // AVERIGUAMOS SI EL DIA DE LA FECHA DE RETIRO ERA DIA DE DESCUENTO

            // COINCIDEN EN AÑO
            if(tarifaDTODescuento.getAnio().equals(anio)){
                // COINCIDEN EN MES
                if (tarifaDTODescuento.getMes().equals(mes)){
                    // COINCIDEN EN DIA
                    if (tarifaDTODescuento.getDiaMes().equals(dia)){
                        return tarifaDTODescuento.getId();
                    }
                }
            }

        }
        // CON LO ANTERIOR GARANTIZAMOS QUE SI CONSEGUIMOS SALIR DEL BUCLE FOREACH ENTONCES EL DIA DEL RETIRO NO ERA DIA DE DESCUENTO
        // AVERIGUAMOS EN QUE DIA DE LA SEMANA ESTAMOS
        int diaSemana = fechaRetiro.getDayOfWeek().getValue();
        for (TarifaDTO tarifaDTONormal:
             tarifasNormales) {
            if ( tarifaDTONormal.getDiaSemana() == diaSemana){
                return tarifaDTONormal.getId();
            }
        }
        return null;
    }

}



