package com.example.flightDataManagementService.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Setter
@Getter
public class FlightDTO {

    private Long id;
    private String airline;
    private String supplier;
    private BigDecimal fare;
    private String departureAirport;
    private String destinationAirport;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;

}
