package com.example.flightDataManagementService.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

    private String airline;
    private String supplier;
    private String departureAirport;
    private String destinationAirport;
    private LocalDate departureDate;
    private LocalDate arrivalDate;

}
