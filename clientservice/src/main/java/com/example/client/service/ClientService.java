package com.example.client.service;


import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClientService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BACKEND_URL = "http://backend:8080/api/data";
    private final Logger logger = LoggerFactory.getLogger(ClientService.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ClientService(CircuitBreakerRegistry circuitBreakerRegistry){
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostConstruct
    public void registerEventListener() {
        // Access your circuit breaker instance by name
        io.github.resilience4j.circuitbreaker.CircuitBreaker breaker =
                circuitBreakerRegistry.circuitBreaker("backendServiceBreaker");

        // Listen for state changes
        breaker.getEventPublisher()
                .onStateTransition(this::logStateChange);
    }

    private void logStateChange(CircuitBreakerOnStateTransitionEvent event) {
        logger.info("CircuitBreaker '{}' transitioned from {} to {}",
                event.getCircuitBreakerName(),
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState());
    }

    @CircuitBreaker(name = "backendServiceBreaker", fallbackMethod = "fallbackResponse")
    public String callBackend(){
        logger.info("Attempting to call backend at {}", BACKEND_URL);
        return restTemplate.getForObject(BACKEND_URL,String.class);
    }

    public String fallbackResponse(Exception e){
        logger.warn("Fallback triggered: {}", e.getMessage());
        return "Fallback: Backend unavailable (" + e.getMessage() + ")";
    }
}
