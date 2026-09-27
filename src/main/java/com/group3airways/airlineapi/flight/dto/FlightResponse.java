package com.group3airways.airlineapi.flight.dto;

import com.group3airways.airlineapi.flight.entity.Flight;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record FlightResponse(
        Long id,
        String airline,
        String flightNumber,
        String origin,
        String originCity,
        String destination,
        String destinationCity,
        LocalDate departureDate,
        LocalTime departureTime,
        LocalTime arrivalTime,
        String duration,
        Integer stops,
        BigDecimal price,
        Integer availableSeats,
        String status,
        String gate
) {

    public static FlightResponse fromEntity(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getAirline(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getOriginCity(),
                flight.getDestination(),
                flight.getDestinationCity(),
                flight.getDepartureDate(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getDuration(),
                flight.getStops(),
                flight.getPrice(),
                flight.getAvailableSeats(),
                flight.getStatus(),
                flight.getGate()
        );
    }
}
