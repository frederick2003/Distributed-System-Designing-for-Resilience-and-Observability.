package com.example.client.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Service
public class ClientService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private static final String BACKEND_URL = "http://backend:8080/api/data";
    private final Logger logger = LoggerFactory.getLogger(ClientService.class);

    public ClientService(CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry){
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }

    @PostConstruct
    public void registerCircuitBreakerLogger() {
        // Access your circuit breaker instance by name
        io.github.resilience4j.circuitbreaker.CircuitBreaker breaker =
                circuitBreakerRegistry.circuitBreaker("backendServiceBreaker");

        // Listen for state changes
        breaker.getEventPublisher()
                .onStateTransition(this::logStateChange);
    }

    @PostConstruct
    public void registerRetryLogger(){
        io.github.resilience4j.retry.Retry retry =  
                retryRegistry.retry("backendServiceRetry");

        retry.getEventPublisher().onRetry(event -> {
        Duration wait = event.getWaitInterval();
        Throwable cause = event.getLastThrowable();
        logger.info("Retry attempt #{} for '{}' after delay {} ms (cause: {})",
                event.getNumberOfRetryAttempts(),
                event.getName(),
                wait != null ? wait.toMillis() : 0,
                cause != null ? cause.getClass().getSimpleName() : "unknown");
    });
        logger.info("retry listener registered for '{}'", retry.getName());
    }

    private void logStateChange(CircuitBreakerOnStateTransitionEvent event) {
        logger.info("CircuitBreaker '{}' transitioned from {} to {}",
                event.getCircuitBreakerName(),
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState());
    }

    @CircuitBreaker(name = "backendServiceBreaker", fallbackMethod = "fallbackResponse")
    public String callBackendWithCircuitBreaker(){
        logger.info("[CB TEST] Calling backend with Circuit Breaker only");
        return restTemplate.getForObject(BACKEND_URL, String.class);
    }

    @Retry(name = "backendServiceRetry", fallbackMethod = "fallbackResponse")
    public String callBackendWithRetry(){
        logger.info("[RETRY TEST] Calling backend with Retry + Jitter only");
        return restTemplate.getForObject(BACKEND_URL, String.class);
    }

    public String fallbackResponse(Throwable e){
        logger.warn("Fallback triggered: {}", e.getMessage());
        return "Fallback: Backend unavailable (" + e.getMessage() + ")";
    }
}
