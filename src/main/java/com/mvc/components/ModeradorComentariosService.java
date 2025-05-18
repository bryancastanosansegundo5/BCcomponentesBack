package com.mvc.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import org.json.JSONObject;

@Component
public class ModeradorComentariosService {

    @Value("${perspective.api.key:}")
    private String apiKey;

    public boolean esToxico(String texto) {
        if (apiKey == null || apiKey.isBlank()) {
            return false; // No filtrar si no hay clave
        }

        try {
            String json = "{"
                    + "\"comment\": {\"text\": \"" + texto.replace("\"", "\\\"") + "\"},"
                    + "\"languages\": [\"es\", \"en\"],"
                    + "\"requestedAttributes\": {\"TOXICITY\": {}}"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Respuesta completa Perspective API:\n" + response.body());
            return extraerScoreToxicidad(response.body()) >= 0.4;

        } catch (IOException | InterruptedException e) {
            // System.err.println("Error al verificar toxicidad: " + e.getMessage());
            return false;
        }
    }

    private double extraerScoreToxicidad(String jsonResponse) {
        JSONObject json = new JSONObject(jsonResponse);
        return json
                .getJSONObject("attributeScores")
                .getJSONObject("TOXICITY")
                .getJSONObject("summaryScore")
                .getDouble("value");
    }
}
