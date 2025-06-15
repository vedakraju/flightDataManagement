package com.example.flightDataManagementService.adapters.dataBase;

import com.example.flightDataManagementService.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByAirlineIgnoreCaseAndDepartureAirportIgnoreCaseAndDepartureTime(String airline,
                                                                                          String departureAirport,
                                                                                          ZonedDateTime departureTime);

}
