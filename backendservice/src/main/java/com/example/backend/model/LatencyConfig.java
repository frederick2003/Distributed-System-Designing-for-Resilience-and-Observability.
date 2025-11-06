package com.example.backend.model;

/**
 * Class to map latency Post requests.
 */
public class LatencyConfig {
    private double delay_rate;
    private int delay_ms;

    public LatencyConfig(){}

    public LatencyConfig(int delay_ms, double delay_rate){
        setDelay_ms(delay_ms);
        setDelayRate(delay_rate);
    }

    // Getters and setters
    public double getDelay_rate() {
        return delay_rate;
    }

    public void setDelayRate(double delay_rate) {
        this.delay_rate = delay_rate;
    }

    public int getDelay_ms() {
        return delay_ms;
    }

    public void setDelay_ms(int delay_ms) {
        this.delay_ms = delay_ms;
    }
}
