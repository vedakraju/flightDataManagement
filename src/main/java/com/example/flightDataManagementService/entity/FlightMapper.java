package com.example.flightDataManagementService.entity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightMapper {

    public FlightDTO toDto(Flight flight) {
        if (flight == null) {
            return null;
        }

        FlightDTO dto = new FlightDTO();
        dto.setId(flight.getId());
        dto.setAirline(flight.getAirline());
        dto.setSupplier(flight.getSupplier());
        dto.setFare(flight.getFare());
        dto.setDepartureAirport(flight.getDepartureAirport());
        dto.setDestinationAirport(flight.getDestinationAirport());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());

        return dto;
    }

    public Flight toEntity(FlightDTO dto) {
        if (dto == null) {
            return null;
        }

        Flight flight = new Flight();
        flight.setId(dto.getId());
        flight.setAirline(dto.getAirline());
        flight.setSupplier(dto.getSupplier());
        flight.setFare(dto.getFare());
        flight.setDepartureAirport(dto.getDepartureAirport());
        flight.setDestinationAirport(dto.getDestinationAirport());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());

        return flight;
    }

    public List<FlightDTO> toDtoList(List<Flight> flights) {
        return flights.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<Flight> toEntityList(List<FlightDTO> dtos) {
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

}
