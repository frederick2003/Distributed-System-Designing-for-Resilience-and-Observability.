package com.example.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;

@RestController
public class ClientController {
    // A Synchronous client!
    private final RestTemplate restTemplate = new RestTemplate();

    // Checks if the backend service is healthy.
    @GetMapping("/api/request-backend")
    public String callBackend() {
        String backendUrl = System.getenv().getOrDefault("BACKEND_URL", "http://backend:8080/api/data");
        try {
            String response = restTemplate.getForObject(backendUrl, String.class);
            return "Client received: " + response;
        } catch (Exception e) {
            return "Client error: " + e.getMessage();
        }
    }
}
