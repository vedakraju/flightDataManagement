package com.example.flightDataManagementService.service;

import org.junit.jupiter.api.extension.ExtendWith;
import com.example.flightDataManagementService.adapters.dataBase.FlightRepository;
import com.example.flightDataManagementService.entity.Flight;
import com.example.flightDataManagementService.entity.FlightDTO;
import com.example.flightDataManagementService.entity.FlightMapper;
import com.example.flightDataManagementService.exception.GenericException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private CrazySupplierClient crazySupplierClient;

    @InjectMocks
    private FlightService flightService;

    private Flight flight;
    private FlightDTO dto;

    @BeforeEach
    void setUp() {
        flight = new Flight();
        flight.setId(1L);
        flight.setAirline("Indigo");
        flight.setSupplier("Amadeus");
        flight.setFare(new BigDecimal("150.00"));
        flight.setDepartureAirport("DEL");
        flight.setDestinationAirport("BOM");
        flight.setDepartureTime(ZonedDateTime.parse("2025-06-14T05:00:00Z"));
        flight.setArrivalTime(ZonedDateTime.parse("2025-06-14T07:00:00Z"));

        dto = new FlightDTO();
        dto.setId(1L);
        dto.setAirline("Indigo");
        dto.setSupplier("Amadeus");
        dto.setFare(new BigDecimal("150.00"));
        dto.setDepartureAirport("DEL");
        dto.setDestinationAirport("BOM");
        dto.setDepartureTime(ZonedDateTime.parse("2025-06-14T05:00:00Z"));
        dto.setArrivalTime(ZonedDateTime.parse("2025-06-14T07:00:00Z"));
    }

    @Test
    void testGetAllFlights() {
        when(flightRepository.findAll()).thenReturn(List.of(flight));
        when(flightMapper.toDtoList(anyList())).thenReturn(List.of(dto));

        List<FlightDTO> result = flightService.getAllFlights();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAirline()).isEqualTo("Indigo");
    }

    @Test
    void testSaveFlight() {
        when(flightMapper.toEntity(any())).thenReturn(flight);
        when(flightRepository.save(any())).thenReturn(flight);
        when(flightMapper.toDto(any())).thenReturn(dto);

        FlightDTO saved = flightService.saveFlight(dto);

        assertThat(saved).isNotNull();
        verify(flightRepository).save(flight);
    }

    @Test
    void testDeleteFlightSuccess() {
        when(flightRepository.existsById(1L)).thenReturn(true);

        flightService.deleteFlight(1L);

        verify(flightRepository).deleteById(1L);
    }

    @Test
    void testDeleteFlightNotFound() {
        when(flightRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> flightService.deleteFlight(1L)).isInstanceOf(ResponseStatusException.class)
                                                                .hasMessageContaining("Flight with ID1 not found.");
    }

    @Test
    void testPatchFlightWhenFlightExists() {
        when(flightRepository.findByAirlineIgnoreCaseAndDepartureAirportIgnoreCaseAndDepartureTime(eq("Indigo"),
                                                                                                   eq("DEL"),
                                                                                                   eq(dto.getDepartureTime()))).thenReturn(
                Optional.of(flight));
        when(flightRepository.save(any())).thenReturn(flight);

        Long result = flightService.patchFlight(dto);

        assertThat(result).isEqualTo(1L);
    }

    @Test
    void testPatchFlightWhenFlightNotExists() {
        when(flightRepository.findByAirlineIgnoreCaseAndDepartureAirportIgnoreCaseAndDepartureTime(anyString(),
                                                                                                   anyString(),
                                                                                                   any())).thenReturn(
                Optional.empty());
        when(flightRepository.save(any())).thenReturn(flight);

        Long result = flightService.patchFlight(dto);

        assertThat(result).isEqualTo(1L);
    }

    @Test
    void testPatchFlightMissingFields() {
        FlightDTO dto = new FlightDTO();
        Throwable thrown = catchThrowable(() -> flightService.patchFlight(dto));
        assertThat(thrown).isInstanceOf(GenericException.class).satisfies(ex -> {
            GenericException ge = (GenericException) ex;
            assertThat(ge.getCode()).isEqualTo("Flight.notIdentifiable");
            assertThat(ge.getMessage()).contains(
                    "To upsert a flight, airline, departure airport, and departure time must be provided");
            assertThat(ge.getFieldErrors()).hasSize(3); // optional: validate field error content
        });
    }

    @Test
    void testSearchFromCrazySupplier() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setSupplier("CrazySupplier");
        dto.setSupplier("CrazySupplier");
        when(crazySupplierClient.getFlightsFromCrazySupplier(criteria)).thenReturn(List.of(dto));

        List<FlightDTO> result = flightService.searchFlights(criteria);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSupplier()).isEqualTo("CrazySupplier");
    }

}
