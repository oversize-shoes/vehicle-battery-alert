package com.wzh.vehicle_battery_alert;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleBatteryAlertApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testInsertSignal() throws Exception {
        String json = "{ \"vin\": 1001, \"signalType\": \"电压差报警\", \"signalValue\": \"{\\\"Mx\\\":4.2,\\\"Mi\\\":3.1}\", \"reportTime\": \"2025-05-19T20:00:00\" }";
        mockMvc.perform(post("/api/signal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    public void testDeleteSignal() throws Exception {
        mockMvc.perform(delete("/api/signal/{id}", 1001))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    public void testUpdateSignal() throws Exception {
        String json = "{\"vin\": 1002, \"signalType\": \"电压差报警\", \"signalValue\": \"{\\\"Mx\\\":4.5,\\\"Mi\\\":3.0}\", \"reportTime\": \"2025-05-19T21:00:00\", \"version\": 0 }";
        mockMvc.perform(put("/api/signal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    public void testGetSignal() throws Exception {
        mockMvc.perform(get("/api/signal/{id}", 1001))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllSignals() throws Exception {
        mockMvc.perform(get("/api/signal/all/{id}", 1002))
                .andExpect(status().isOk());
    }

}
