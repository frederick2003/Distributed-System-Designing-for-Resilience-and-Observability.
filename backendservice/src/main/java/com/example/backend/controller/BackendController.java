package com.example.backend.controller;

import com.example.backend.model.FailureConfig;
import com.example.backend.model.LatencyConfig;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;

@RestController
public class BackendController {
    private final Random random = new Random();
    private FailureConfig failureConfiguration;
    private LatencyConfig latencyConfiguration;
    private final Random rand = new Random();


    @GetMapping("/api/data")
    public String getData() throws InterruptedException{
        String responseMessage = "";
        // Simulate some sort of latency spike
        if (latencyConfiguration != null){
            dealWithLatencyLag();
            responseMessage = responseMessage.concat("Latency spike introduced\n");
        }

        // Simulate a HTTP error!
        if(failureConfiguration != null){
            dealWithHttpError();
            responseMessage = responseMessage.concat("HTTP error introduced\n");
        }
        return responseMessage.concat("Backend response OK at" + System.currentTimeMillis());
    }

    @PostMapping("/config/failure")
    public String setFailureConfig(@RequestBody FailureConfig failureConfig){
        this.failureConfiguration = failureConfig;
        return "Failure accepted with failure Rate = " + failureConfiguration.getFailure_rate()
                + " status_code = " + failureConfiguration.getStatus_code();
    }

    @PostMapping("/config/latency")
    public String setLatencyConfig(@RequestBody LatencyConfig latencyConfig){
        this.latencyConfiguration = latencyConfig;
        return "Latency configuration accepted with delay in ms = " +  latencyConfiguration.getDelayTime()
                + ", latency rate = " + latencyConfiguration.getDelay_rate();
    }

    private void dealWithHttpError(){
        int httpStatusCode = failureConfiguration.getStatus_code();
        if(httpStatusCode >= 100 && httpStatusCode < 600){
            HttpStatus status = HttpStatus.resolve(httpStatusCode);
            if(status == null){
                System.out.println("Http Error code Illegal, Will throw a HTTP: 500 error instead.");
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            throw new ResponseStatusException(status, "Simulating a " + httpStatusCode + "code error");
        }
    }

    private void dealWithLatencyLag() throws InterruptedException{
        double probabilityOfLatencyLag = rand.nextDouble();
        if(probabilityOfLatencyLag < latencyConfiguration.getDelay_rate()){
            Thread.sleep(latencyConfiguration.getDelayTime());
        }
    }
}
