package com.example.flightDataManagementService.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrazySupplierRequest {
    private String from;           // 3-letter departure airport code
    private String to;             // 3-letter destination airport code
    private LocalDate outboundDate; // ISO_LOCAL_DATE, CET timezone
    private LocalDate inboundDate;  // ISO_LOCAL_DATE, CET timezone
}
