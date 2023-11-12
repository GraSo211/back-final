package com.grupo08.alquileres.service;

import com.grupo08.alquileres.model.Alquiler;
import com.grupo08.alquileres.repository.AlquilerRepository;
import com.grupo08.estaciones.model.Estacion;
import com.grupo08.estaciones.repository.EstacionRepository;
import com.grupo08.tarifas.model.Tarifa;
import com.grupo08.tarifas.repository.TarifaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.json.JSONObject;
import java.io.StringReader;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;
    private TarifaRepository tarifaRepository;
    private EstacionRepository estacionRepository;

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

    public Alquiler finalizarAlquiler(long idAlquiler, String moneda) {
        Optional<Alquiler> alquilerOptional = alquilerRepository.findById(idAlquiler);
        if (alquilerOptional.isPresent()) {
            Alquiler alquiler = alquilerOptional.get();
            alquiler.setFechaHoraDevolucion(LocalDateTime.now());
            alquiler.setEstado(1); // Estado finalizado

            Optional<Tarifa> tarifaOptional = tarifaRepository.findById(alquiler.getIdTarifa());
            if (tarifaOptional.isPresent()) {
                Tarifa tarifa = tarifaOptional.get();
                double monto = calcularMonto(alquiler, tarifa);
                alquiler.setMonto(monto);
                if (!moneda.equals("ARS")) {
                    double tasaCambio = obtenerTasaCambio(moneda);
                    alquiler.setMonto(monto * tasaCambio);
                }
                alquilerRepository.save(alquiler);
                return alquiler;
            }
        }
        return null;
    }

    private double calcularMonto(Alquiler alquiler, Tarifa tarifa) {
        double monto = 0;
        long duracionEnMinutos = ChronoUnit.MINUTES.between(alquiler.getFechaHoraRetiro(), alquiler.getFechaHoraDevolucion());

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
        double distancia = calcularDistancia(alquiler.getEstacionRetiro(), alquiler.getEstacionDevolucion());
        monto += distancia * tarifa.getMontoKm();

        // Descuento para los días promocionales
        // No encuentro los dias de la semana en los que hay descuento en la bse de datos..
        /*if (esDiaPromocional(alquiler.getFechaHoraRetiro())) {
            double descuento = obtenerDescuento(alquiler.getFechaHoraRetiro());
            monto -= monto * descuento;
        }*/

        return monto;
    }

    public void delete(long id){
        alquilerRepository.deleteById(id);
    }

    public List<Alquiler> getAllByFilter(int estado) {
        List<Alquiler> alquilerList = alquilerRepository.findByEstado(estado);
        return alquilerList;
    }

    private double calcularDistancia(long idEstacionRetiro, long idEstacionDevolucion) {
        Optional<Estacion> estacionRetiroOptional = estacionRepository.findById(idEstacionRetiro);
        Optional<Estacion> estacionDevolucionOptional = estacionRepository.findById(idEstacionDevolucion);

        if (estacionRetiroOptional.isPresent() && estacionDevolucionOptional.isPresent()) {
            Estacion estacionRetiro = estacionRetiroOptional.get();
            Estacion estacionDevolucion = estacionDevolucionOptional.get();

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

    private double obtenerTasaCambio(String moneda) {
        RestTemplate restTemplate = new RestTemplate();

        // Crear el objeto de solicitud
        String jsonRequest = "{\"moneda_destino\":\"" + moneda + "\",\"importe\":1000}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

        // Hacer la solicitud POST
        ResponseEntity<String> response = restTemplate.exchange("http://34.82.105.125:8080/convertir", HttpMethod.POST, entity, String.class);

        // Parsear la respuesta
        String jsonResponse = response.getBody();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        double importe = jsonObject.getDouble("importe");

        // Calcular la tasa de cambio

        return importe / 1000;
    }

}
