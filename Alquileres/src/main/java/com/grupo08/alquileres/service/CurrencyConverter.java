package com.grupo08.alquileres.service;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CurrencyConverter {
    // Este es un llamado a una api externa para pasasr de una moneda a otra.

    public double cambioDeMoneda(String moneda, double importe) {
        RestTemplate restTemplate = new RestTemplate();

        // Crear el objeto de solicitud
        String jsonRequest = "{\"moneda_destino\":\"" + moneda + "\",\"importe\":" + importe + "}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

        // Hacer la solicitud POST
        ResponseEntity<String> response = restTemplate.exchange("http://34.82.105.125:8080/convertir", HttpMethod.POST, entity, String.class);

        // Parsear la respuesta
        String jsonResponse = response.getBody();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        double importeConvertido = jsonObject.getDouble("importe");

        return importeConvertido;
    }
}
