package com.mvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class PayPalClient {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    private final String baseUrl = "https://api-m.sandbox.paypal.com";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/v1/oauth2/token",
                HttpMethod.POST,
                request,
                Map.class
        );

        return response.getBody().get("access_token").toString();
    }

    public String crearPedido(double total, String moneda) {
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("intent", "CAPTURE");

        Map<String, Object> amount = new HashMap<>();
        amount.put("currency_code", moneda);
        amount.put("value", String.format(Locale.US, "%.2f", total));


        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("amount", amount);

        body.put("purchase_units", Collections.singletonList(purchaseUnit));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders",
                HttpMethod.POST,
                request,
                Map.class
        );

        return response.getBody().get("id").toString(); // orderID
    }

    public boolean capturarPedido(String orderID) {
    String token = getAccessToken();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Void> request = new HttpEntity<>(headers);

    try {
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders/" + orderID + "/capture",
                HttpMethod.POST,
                request,
                Map.class
        );

        // Puedes revisar el estado real si necesitas más control:
        // String status = ((Map<String, Object>) response.getBody()).get("status").toString();
        return response.getStatusCode() == HttpStatus.CREATED;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

}
