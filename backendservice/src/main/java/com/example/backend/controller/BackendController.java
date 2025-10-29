package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class BackendController {
    private final Random random = new Random();


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
}
