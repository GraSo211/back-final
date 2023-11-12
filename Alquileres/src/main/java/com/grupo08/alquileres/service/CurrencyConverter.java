package com.grupo08.alquileres.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrencyConverter {
    public static void main(String[] args) {
        // Cambiar estas variables, esta hardcodeado para testear
        String destino = "USD";
        double importe = 1000;

        String apiUrl = "http://34.82.105.125:8080/convertir";

        String requestBody = String.format("{\"moneda_destino\":\"%s\",\"importe\":%f}", destino, importe);

        String response = sendPostRequest(apiUrl, requestBody);

        System.out.println("API Response: " + response);
    }

    private static String sendPostRequest(String apiUrl, String requestBody) {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return httpResponse.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
