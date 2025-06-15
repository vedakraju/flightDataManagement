package com.example.flightDataManagementService.adapters.rest;

import com.example.flightDataManagementService.entity.FlightDTO;
import com.example.flightDataManagementService.exception.FieldError;
import com.example.flightDataManagementService.exception.GenericException;
import com.example.flightDataManagementService.service.FlightService;
import com.example.flightDataManagementService.service.SearchCriteria;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/flight")
@Slf4j
@AllArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public List<FlightDTO> getAllFlights() {
        List<FlightDTO> flights = flightService.getAllFlights();
        if (flights.isEmpty()) {
            throw new GenericException("NOT_FOUND", "No flight records found.");
        }
        return flights;
    }

    @PostMapping
    public FlightDTO createFlight(@RequestBody FlightDTO flightDTO) {
        log.info("FlightDTO Information : {}", flightDTO);
        if (flightDTO.getAirline() == null) {
            throw new GenericException("BAD_REQUEST", "Validation failed", new FieldError("airline", null));
        }
        if (flightDTO.getDepartureTime() == null || !isValidTimestamp(String.valueOf(flightDTO.getDepartureTime()))) {
            throw new GenericException("BAD_REQUEST",
                                       "Invalid or missing departureTime",
                                       new FieldError("departureTime", flightDTO.getDepartureTime()));
        }
        if (flightDTO.getArrivalTime() == null || !isValidTimestamp(String.valueOf(flightDTO.getArrivalTime()))) {
            throw new GenericException("BAD_REQUEST",
                                       "Invalid or missing arrivalTime",
                                       new FieldError("arrivalTime", flightDTO.getArrivalTime()));
        }

        return flightService.saveFlight(flightDTO);
    }

    @PatchMapping
    public String patchFlight(@RequestBody FlightDTO dto) {
        log.info("FlightDTO Updated Information : {}", dto);
        if (dto.getId() == null) {
            throw new GenericException("BAD_REQUEST",
                                       "Flight ID must be provided for update",
                                       new FieldError("flightId", null));
        }

        Long updatedId = flightService.patchFlight(dto);
        if (updatedId == null) {
            throw new GenericException("NOT_FOUND",
                                       "Flight not found for update",
                                       new FieldError("flightId", dto.getId()));
        }

        return "Updated Flight Information : " + updatedId;
    }

    @DeleteMapping("/{id}")
    public String deleteFlight(@PathVariable Long id) {
        log.info("FlightDTO Id to Delete : {}", id);
        if (id == null || id <= 0) {
            throw new GenericException("BAD_REQUEST", "Invalid Flight ID", new FieldError("id", id));
        }

        try {
            flightService.deleteFlight(id);
        } catch (Exception e) {
            throw new GenericException("NOT_FOUND", "Flight not found for deletion", new FieldError("id", id));
        }

        return "Deleted Flight Id : " + id;
    }

    @GetMapping("/search")
    public List<FlightDTO> searchFlights(@RequestParam String airline,
                                         @RequestParam String supplier,
                                         @RequestParam(required = false) String departureAirport,
                                         @RequestParam(required = false) String destinationAirport,
                                         @RequestParam(required = false) ZonedDateTime departureTime,
                                         @RequestParam(required = false) ZonedDateTime arrivalTime) {

        ZoneId cet = ZoneId.of("CET");
        LocalDate departureDate = null;
        LocalDate arrivalDate = null;
        if (departureTime != null) {
            departureDate = departureTime.withZoneSameInstant(cet).toLocalDate();
        }
        if (arrivalTime != null) {
            arrivalDate = arrivalTime.withZoneSameInstant(cet).toLocalDate();
        }
        SearchCriteria criteria = new SearchCriteria();
        criteria.setAirline(airline);
        criteria.setSupplier(supplier);
        criteria.setDepartureAirport(departureAirport);
        criteria.setDestinationAirport(destinationAirport);
        criteria.setDepartureDate(departureDate);
        criteria.setArrivalDate(arrivalDate);

        return flightService.searchFlights(criteria);
    }

    private boolean isValidTimestamp(String timestamp) {
        try {
            ZonedDateTime.parse(timestamp);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
