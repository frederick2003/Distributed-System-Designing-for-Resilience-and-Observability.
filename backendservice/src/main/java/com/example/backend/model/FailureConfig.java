package com.example.backend.model;

/**
 * class to map failure POST requests.
 */
public class FailureConfig {
    private double failure_rate;
    private int status_code;

    public FailureConfig(){}

    public FailureConfig(double failure_rate, int status_code){
        setFailure_rate(failure_rate);
        setStatus_code(status_code);
    }

    // Getters and setters
    public double getFailure_rate(){
        return failure_rate;
    }

    public void setFailure_rate(double failure_rate){
        this.failure_rate = failure_rate;
    }

    public int getStatus_code(){
        return status_code;
    }

    public void setStatus_code(int status_code){
        this.status_code = status_code;
    }
}