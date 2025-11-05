package com.example.backend.model;

/**
 * Class to map latency Post requests.
 */
public class LatencyConfig {
    private int delay_ms;
    private double delay_rate;

    // Getters and setters
    public double getDelay_rate() {
        return delay_rate;
    }

    public void setDelayRate(double delay_rate) {
        this.delay_rate = delay_rate;
    }

    public int getDelayTime() {
        return delay_ms;
    }

    public void setDelayTime(int delay_ms) {
        this.delay_ms = delay_ms;
    }
}
