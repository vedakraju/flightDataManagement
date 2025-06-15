package com.example.flightDataManagementService.service;

import com.example.flightDataManagementService.entity.FlightDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class CrazySupplierClient {

    private final WebClient crazySupplierWebClient;
    private final CrazySupplierProperties crazySupplierProperties;
    private static final LocalTime DEFAULT_DEPARTURE_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_ARRIVAL_TIME = LocalTime.of(11, 0);

    public List<FlightDTO> getFlightsFromCrazySupplier(SearchCriteria criteria) {
        if (externalApiUnavailable()) {
            return List.of(mockCrazySupplierFlight());
        }
        try {
            ZoneId cet = ZoneId.of("CET");
            LocalDate today = LocalDate.now(cet);

            LocalDate outboundDate = (criteria.getDepartureDate() != null)
                                     ? criteria.getDepartureDate()
                                     : today;  // It’s a fallback assumption: if the user only specifies a departure date or nothing at all, we assume: Departure is today (or the date in criteria)

            LocalDate inboundDate = (criteria.getArrivalDate() != null)
                                    ? criteria.getArrivalDate()
                                    : today.plusDays(1); // When arrival date is not provided, this defaults to "tomorrow" (relative to today in CET).

            String from = (criteria.getDepartureAirport() != null) ? criteria.getDepartureAirport() : "";
            String to = (criteria.getDestinationAirport() != null) ? criteria.getDestinationAirport() : "";

            CrazySupplierRequest request = new CrazySupplierRequest(from, to, outboundDate, inboundDate);

            List<CrazySupplierResponse> response = crazySupplierWebClient.post()
                                                                         .uri("/flights")
                                                                         .bodyValue(request)
                                                                         .retrieve()
                                                                         .bodyToFlux(CrazySupplierResponse.class)
                                                                         .collectList()
                                                                         .block();

            if (response == null || response.isEmpty()) {
                return List.of();
            }

            return response.stream().map(this::mapToFlightDTO).toList();
        } catch (Exception e) {
            log.warn("Falling back to mock data due to error: {}", e.getMessage());
            return List.of(mockCrazySupplierFlight());
        }
    }

    private FlightDTO mapToFlightDTO(CrazySupplierResponse data) {
        FlightDTO dto = new FlightDTO();
        dto.setAirline(data.getCarrier());
        dto.setSupplier("CrazySupplier");

        BigDecimal fare = data.getBasePrice().add(data.getTax());
        dto.setFare(fare);

        dto.setDepartureAirport(data.getDepartureAirportName());
        dto.setDestinationAirport(data.getArrivalAirportName());

        ZoneId cet = ZoneId.of("CET");

        //CrazySupplier only gives you dates (without times), and your system expects full ZonedDateTime,must still provide a time
        //this method we can use to separate departure and arrival times, used named constants for clarity
//        dto.setDepartureTime(data.getOutboundDateTime()
//                                 .atTime(DEFAULT_DEPARTURE_TIME)
//                                 .atZone(cet)
//                                 .withZoneSameInstant(ZoneId.of("UTC")));

//        dto.setArrivalTime(data.getInboundDateTime()
//                               .atTime(DEFAULT_ARRIVAL_TIME)
//                               .atZone(cet)
//                               .withZoneSameInstant(ZoneId.of("UTC")));



//      This avoids assumptions gives "departureTime": "2025-06-14T00:00:00Z"
        dto.setDepartureTime(data.getOutboundDateTime()
                                 .atStartOfDay(cet)
                                 .withZoneSameInstant(ZoneId.of("UTC")));

        dto.setArrivalTime(data.getInboundDateTime()
                               .atStartOfDay(cet)
                               .withZoneSameInstant(ZoneId.of("UTC")));


        return dto;
    }

    private FlightDTO mockCrazySupplierFlight() {
        FlightDTO dto = new FlightDTO();
        dto.setId(1L);
        dto.setAirline("MockAir");
        dto.setSupplier("CrazySupplier");
        dto.setFare(BigDecimal.valueOf(200));
        dto.setDepartureAirport("CDG");
        dto.setDestinationAirport("FRA");
        dto.setDepartureTime(ZonedDateTime.now(ZoneId.of("CET")).withZoneSameInstant(ZoneId.of("UTC")));
        dto.setArrivalTime(ZonedDateTime.now(ZoneId.of("CET")).plusHours(2).withZoneSameInstant(ZoneId.of("UTC")));
        return dto;
    }

    private boolean externalApiUnavailable() {
        return crazySupplierProperties.isUnavailable();
    }
}
