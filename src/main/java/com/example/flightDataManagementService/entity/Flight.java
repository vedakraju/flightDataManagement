package com.example.flightDataManagementService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Setter
@Getter
@Entity
@Table(name = "flight", schema = "flight_data_management_service_schema")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String airline;
    private String supplier;
    private BigDecimal fare;

    @Column(length = 3)
    private String departureAirport;

    @Column(length = 3)
    private String destinationAirport;

    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;

}
