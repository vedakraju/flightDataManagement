package com.example.flightDataManagementService.entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FlightMapperTest {

    private FlightMapper flightMapper;

    @BeforeEach
    void setUp() {
        flightMapper = new FlightMapper();
    }

    private Flight createFlightEntity() {
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setAirline("Indigo");
        flight.setSupplier("Amadeus");
        flight.setFare(new BigDecimal("150.00"));
        flight.setDepartureAirport("DEL");
        flight.setDestinationAirport("BOM");
        flight.setDepartureTime(ZonedDateTime.parse("2025-06-14T05:00:00Z"));
        flight.setArrivalTime(ZonedDateTime.parse("2025-06-14T07:00:00Z"));
        return flight;
    }

    private FlightDTO createFlightDTO() {
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
    void testToDto() {
        Flight flight = createFlightEntity();
        FlightDTO dto = flightMapper.toDto(flight);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(flight.getId());
        assertThat(dto.getAirline()).isEqualTo(flight.getAirline());
        assertThat(dto.getDepartureTime()).isEqualTo(flight.getDepartureTime());
    }

    @Test
    void testToEntity() {
        FlightDTO dto = createFlightDTO();
        Flight flight = flightMapper.toEntity(dto);

        assertThat(flight).isNotNull();
        assertThat(flight.getId()).isEqualTo(dto.getId());
        assertThat(flight.getAirline()).isEqualTo(dto.getAirline());
        assertThat(flight.getArrivalTime()).isEqualTo(dto.getArrivalTime());
    }

    @Test
    void testToDtoList() {
        List<Flight> entities = List.of(createFlightEntity());
        List<FlightDTO> dtos = flightMapper.toDtoList(entities);

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getAirline()).isEqualTo("Indigo");
    }

    @Test
    void testToEntityList() {
        List<FlightDTO> dtos = List.of(createFlightDTO());
        List<Flight> entities = flightMapper.toEntityList(dtos);

        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getDestinationAirport()).isEqualTo("BOM");
    }

    @Test
    void testNullHandling() {
        assertThat(flightMapper.toDto(null)).isNull();
        assertThat(flightMapper.toEntity(null)).isNull();
    }
}
