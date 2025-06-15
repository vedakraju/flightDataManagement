package com.example.flightDataManagementService.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrazySupplierResponse {

    private String carrier;                 // Airline name
    private BigDecimal basePrice;          // price without tax
    private BigDecimal tax;                // tax amount
    private String departureAirportName;   // 3-letter code
    private String arrivalAirportName;     // 3-letter code
    private LocalDate outboundDateTime;    // ISO_LOCAL_DATE, CET
    private LocalDate inboundDateTime;     // ISO_LOCAL_DATE, CET

}
