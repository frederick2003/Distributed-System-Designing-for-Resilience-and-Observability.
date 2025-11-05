package com.example.backend.controller;

import com.example.backend.model.FailureConfig;
import com.example.backend.model.LatencyConfig;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;


@RestController
public class BackendController {
    private final Random random = new Random();
    private FailureConfig failureConfiguration = new FailureConfig();
    private LatencyConfig latencyConfiguration = new LatencyConfig();


    @GetMapping("/api/data")
    public String getData() throws InterruptedException{
        int chance = random.nextInt(10);
        if (chance < 2){
            throw new RuntimeException("Simulate HTTP 500 error");
        }else{
            Thread.sleep(5000);
        }
        return "Backend response OK at " + System.currentTimeMillis();
    }

    @PostMapping("/config/failure")
    public String setFailureConfig(@RequestBody FailureConfig failureConfig){
        this.failureConfiguration = failureConfig;
        return "Failure accepted with failure Rate = " + failureConfiguration.getFailure_rate()
                + "status_code = " + failureConfiguration.getStatus_code();
    }

    @PostMapping("/config/latency")
    public String setLatencyConfig(@RequestBody LatencyConfig latencyConfig){
        this.latencyConfiguration = latencyConfig;
        return "Latency configuration accepted with delay in ms = " +  latencyConfiguration.getDelayTime()
                + "latency rate = " + latencyConfiguration.getDelay_rate();
    }
}
