package com.example.flightDataManagementService.adapters.rest;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.example.flightDataManagementService.entity.FlightDTO;
import com.example.flightDataManagementService.service.FlightService;
import com.example.flightDataManagementService.service.SearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
@Import({FlightServiceTestConfig.class, SecurityTestConfig.class})
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    private FlightDTO sampleFlight() {
        FlightDTO dto = new FlightDTO();
        dto.setId(1L);
        dto.setAirline("Indigo");
        dto.setSupplier("Amadeus");
        dto.setFare(new BigDecimal("150.00"));
        dto.setDepartureAirport("DEL");
        dto.setDestinationAirport("BOM");
        dto.setDepartureTime(ZonedDateTime.parse("2025-06-14T05:00:00Z"));
        dto.setArrivalTime(ZonedDateTime.parse("2025-06-14T07:00:00Z"));
        return dto;
    }

    @Test
    void testGetAllFlights() throws Exception {
        Mockito.when(flightService.getAllFlights()).thenReturn(List.of(sampleFlight()));

        mockMvc.perform(get("/flight"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.size()").value(1))
               .andExpect(jsonPath("$[0].airline").value("Indigo"));
    }

    @Test
    void testCreateFlight() throws Exception {
        FlightDTO flight = sampleFlight();
        flight.setId(null);
        Mockito.when(flightService.saveFlight(any())).thenReturn(sampleFlight());

        mockMvc.perform(post("/flight").contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(flight)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.airline").value("Indigo"));
    }

    @Test
    void testDeleteFlight() throws Exception {
        mockMvc.perform(delete("/flight/1"))
               .andExpect(status().isOk())
               .andExpect(content().string("Deleted Flight Id : 1"));
    }

    @Test
    void testPatchFlight() throws Exception {
        Mockito.when(flightService.patchFlight(any())).thenReturn(1L);

        mockMvc.perform(patch("/flight").contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(sampleFlight())))
               .andExpect(status().isOk())
               .andExpect(content().string("Updated Flight Information : 1"));
    }

    @Test
    void testSearchFlights() throws Exception {
        Mockito.when(flightService.searchFlights(any(SearchCriteria.class))).thenReturn(List.of(sampleFlight()));

        mockMvc.perform(get("/flight/search").param("airline", "Indigo")
                                             .param("supplier", "Amadeus")
                                             .param("departureAirport", "DEL")
                                             .param("destinationAirport", "BOM")
                                             .param("departureTime", "2025-06-14T05:00:00Z")
                                             .param("arrivalTime", "2025-06-14T07:00:00Z"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].departureAirport").value("DEL"))
               .andExpect(jsonPath("$[0].destinationAirport").value("BOM"));
    }

}
