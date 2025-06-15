package com.example.flightDataManagementService.adapters.rest;

import com.example.flightDataManagementService.service.FlightService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FlightServiceTestConfig {

    @Bean
    public FlightService flightService() {
        return Mockito.mock(FlightService.class);
    }

}
