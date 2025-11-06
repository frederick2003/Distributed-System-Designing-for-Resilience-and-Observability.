package com.example.client.controller;

import com.example.client.service.ClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ClientController {

    private final ClientService clientService;

    // Constructor injection
    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    @GetMapping("/api/test-circuit")
    public String testCircuit(){
        return clientService.callBackendWithCircuitBreaker();
    }

    @GetMapping("/api/test-retry")
    public String testRetry(){
        return clientService.callBackendWithRetry();
    }
}