package com.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EndPointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPutLatencyFailure() throws  Exception{
        String json = "{ \"delay_ms\" : \"30\", \"delay_rate\" : \"0.2\" }";
        // objectMapper.readValue() JSON - > an object
        MvcResult result = mockMvc.perform(
                        post("/config/latency")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Extract the actual string
        String response = result.getResponse().getContentAsString();
        assertEquals(response, "");
    }

    @Test
    void testPutHttpFailure() throws Exception{
        String json = "{ \"failure_rate\" : \"0.2\", \"status_code\" : \"201\" }";

        MvcResult result = mockMvc.perform(
                        post("/config/failure")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Extract the actual string
        String response = result.getResponse().getContentAsString();
        assertEquals(response, "");
    }
}