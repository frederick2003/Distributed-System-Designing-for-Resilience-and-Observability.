package com.example.backend.controller;

import com.example.backend.model.FailureConfig;
import com.example.backend.model.LatencyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private static final Logger logger = LoggerFactory.getLogger(BackendController.class);

    @GetMapping("/api/data")
    public String getData() throws InterruptedException{

        StringBuilder responseMessage = new StringBuilder();

        // Simulate some sort of latency spike
        if (latencyConfiguration != null){
            dealWithLatencyLag();
            responseMessage.append("Latency spike introduced\n");
        }

        // Simulate a HTTP error!
        if(failureConfiguration != null){
            dealWithHttpError();
            if (failureConfiguration.getFailure_rate() > 0.0 ){
                responseMessage.append("HTTP error with probability " +failureConfiguration.getFailure_rate()*100 +  "% chance of occuring introduced\n");
            }
        }

        // Get response info
        long timeStamp = System.currentTimeMillis();
        String successMessage = "Backend response OK at" + timeStamp;

        // Log response info
        logger.info(successMessage);

        // Return response String
        responseMessage.append(successMessage);
        return responseMessage.toString();
    }

    @PostMapping("/config/failure")
    public String setFailureConfig(@RequestBody FailureConfig failureConfig){
        // Set the HTTP failure from the passed JSON
        this.failureConfiguration = failureConfig;

        // Log the JSON failure config
        logger.info("Failure configuration updated: rate={} status_code={}",
                failureConfig.getFailure_rate(), failureConfig.getStatus_code());

        // Return the failure config
        return "Failure configuration updated: rate= {" + failureConfiguration.getFailure_rate()
                + "} status_code = {" + failureConfiguration.getStatus_code() + "}";
    }

    @PostMapping("/config/latency")
    public ResponseEntity<String> setLatencyConfig(@RequestBody LatencyConfig latencyConfig) throws InterruptedException{
        // Set the latency error config
        this.latencyConfiguration = latencyConfig;

        // Log the latency error config
        logger.info("Latency configuration updated: delay_ms={} delay_rate={}",
                latencyConfig.getDelay_ms(), latencyConfig.getDelay_rate());

        return ResponseEntity.ok("Latency configuration updated: delay=" +
                latencyConfig.getDelay_ms() + "ms, rate=" + latencyConfig.getDelay_rate());
    }

    private void dealWithHttpError(){
        // Grab a random number from [0,1]
        double randomVal = random.nextDouble();

        // Grab the HTTP status code passed from JSON
        int httpStatusCode = failureConfiguration.getStatus_code();
        HttpStatus status = HttpStatus.resolve(httpStatusCode);

        // if The http status is not valid.
        // Then default to a 500 error.
        if (status == null){
            logger.warn("Invalid HTTP status code {} configured. Defaulting to 500.", httpStatusCode);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        if(randomVal < failureConfiguration.getFailure_rate()){
            logger.warn("Simulating HTTP {} error )", failureConfiguration.getStatus_code());
            throw new ResponseStatusException(status, "Simulated " + failureConfiguration.getStatus_code() + " error.");
        }
    }

    private void dealWithLatencyLag() throws InterruptedException{
        // Grab a random number from [0,1]
        double probabilityOfLatencyLag = random.nextDouble();

        // If the random number < probability of a latencySpike
        // Timeout the thread
        if(probabilityOfLatencyLag < latencyConfiguration.getDelay_rate()){
            int delay = latencyConfiguration.getDelay_ms();
            logger.warn("Simulating latency spike: sleeping for {} ms", delay);
            Thread.sleep(delay);
        }
    }
}