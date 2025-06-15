package com.example.flightDataManagementService.service;

import com.example.flightDataManagementService.adapters.dataBase.FlightRepository;
import com.example.flightDataManagementService.entity.Flight;
import com.example.flightDataManagementService.entity.FlightDTO;
import com.example.flightDataManagementService.entity.FlightMapper;
import com.example.flightDataManagementService.exception.GenericException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final CrazySupplierClient crazySupplierClient;

    public List<FlightDTO> getAllFlights() {
        return flightMapper.toDtoList(flightRepository.findAll());
    }

    @Transactional
    public FlightDTO saveFlight(FlightDTO dto) {
        var entity = flightMapper.toEntity(dto);
        log.info("Saving entity: {}", entity);
        var saved = flightRepository.save(entity);
        log.info("Saved entity: {}", saved);
        return flightMapper.toDto(saved);
    }

    @Transactional
    public void deleteFlight(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight with ID" + id + " not found.");
        }
        flightRepository.deleteById(id);
    }

    public List<FlightDTO> searchFlights(SearchCriteria criteria) {
        List<FlightDTO> results = new ArrayList<>();

        if (criteria.getSupplier() != null && !criteria.getSupplier().equalsIgnoreCase("CrazySupplier")) {
            List<Flight> allFlights = flightRepository.findAll();
            for (Flight flight : allFlights) {
                boolean matches = true;

                if (criteria.getAirline() != null) {
                    if (!criteria.getAirline().equalsIgnoreCase(flight.getAirline())) {
                        matches = false;
                    }
                }

                if (criteria.getDepartureAirport() != null) {
                    if (!criteria.getDepartureAirport().equalsIgnoreCase(flight.getDepartureAirport())) {
                        matches = false;
                    }
                }

                if (criteria.getDestinationAirport() != null) {
                    if (!criteria.getDestinationAirport().equalsIgnoreCase(flight.getDestinationAirport())) {
                        matches = false;
                    }
                }

                if (criteria.getSupplier() != null) {
                    if (!criteria.getSupplier().equalsIgnoreCase(flight.getSupplier())) {
                        matches = false;
                    }
                }

                if (criteria.getDepartureDate() != null) {
                    LocalDate departureDateCET = flight.getDepartureTime()
                                                       .withZoneSameInstant(ZoneId.of("CET"))
                                                       .toLocalDate();
                    if (!departureDateCET.equals(criteria.getDepartureDate())) {
                        matches = false;
                    }
                }

                if (criteria.getArrivalDate() != null) {
                    LocalDate arrivalDateCET = flight.getArrivalTime()
                                                     .withZoneSameInstant(ZoneId.of("CET"))
                                                     .toLocalDate();
                    if (!arrivalDateCET.equals(criteria.getArrivalDate())) {
                        matches = false;
                    }
                }

                if (matches) {
                    results.add(flightMapper.toDto(flight));
                }
            }
        }

        // Add CrazySupplier flights
        else {
            List<FlightDTO> crazySupplierFlights = crazySupplierClient.getFlightsFromCrazySupplier(criteria);
            results.addAll(crazySupplierFlights);
        }

        return results;
    }

    @Transactional
    public Long patchFlight(@NotNull FlightDTO dto) {
        String airline = dto.getAirline();
        String departureAirport = dto.getDepartureAirport();
        ZonedDateTime departureTime = dto.getDepartureTime();

        if (airline == null || departureAirport == null || departureTime == null) {
            throw new GenericException("Flight.notIdentifiable",
                                       "To upsert a flight, airline, departure airport, and departure time must be provided",
                                       new com.example.flightDataManagementService.exception.FieldError("airline",
                                                                                                        airline),
                                       new com.example.flightDataManagementService.exception.FieldError(
                                               "departureAirport",
                                               departureAirport),
                                       new com.example.flightDataManagementService.exception.FieldError("departureTime",
                                                                                                        departureTime));
        }

        Optional<Flight> existingFlightOpt = flightRepository.findByAirlineIgnoreCaseAndDepartureAirportIgnoreCaseAndDepartureTime(
                airline,
                departureAirport,
                departureTime);

        Flight flight = existingFlightOpt.orElse(new Flight());
        mergeFlight(dto, flight);

        return flightRepository.save(flight).getId();
    }

    private void mergeFlight(FlightDTO dto, Flight entity) {
        if (dto.getAirline() != null) {
            entity.setAirline(dto.getAirline());
        }
        if (dto.getSupplier() != null) {
            entity.setSupplier(dto.getSupplier());
        }
        if (dto.getFare() != null) {
            entity.setFare(dto.getFare());
        }
        if (dto.getDepartureAirport() != null) {
            entity.setDepartureAirport(dto.getDepartureAirport());
        }
        if (dto.getDestinationAirport() != null) {
            entity.setDestinationAirport(dto.getDestinationAirport());
        }
        if (dto.getDepartureTime() != null) {
            entity.setDepartureTime(dto.getDepartureTime());
        }
        if (dto.getArrivalTime() != null) {
            entity.setArrivalTime(dto.getArrivalTime());
        }
    }

}


