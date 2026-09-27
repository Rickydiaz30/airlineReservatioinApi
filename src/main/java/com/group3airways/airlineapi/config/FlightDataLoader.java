package com.group3airways.airlineapi.config;

import com.group3airways.airlineapi.flight.entity.Flight;
import com.group3airways.airlineapi.flight.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class FlightDataLoader {

    @Bean
    CommandLineRunner loadFlightData(
            FlightRepository flightRepository
    ) {
        return args -> {

            if (flightRepository.count() == 0) {
                List<Flight> flights = List.of(
                        createFlight(
                                "Group 3 Airways",
                                "G301",
                                "ATL",
                                "Atlanta",
                                "MCO",
                                "Orlando",
                                LocalDate.of(2026, 9, 25),
                                LocalTime.of(8, 0),
                                LocalTime.of(9, 30),
                                "1h 30m",
                                0,
                                new BigDecimal("189.99"),
                                42,
                                "SCHEDULED",
                                "A12"
                        ),
                        createFlight(
                                "Group 3 Airways",
                                "G302",
                                "ATL",
                                "Atlanta",
                                "MCO",
                                "Orlando",
                                LocalDate.of(2026, 9, 25),
                                LocalTime.of(12, 30),
                                LocalTime.of(14, 0),
                                "1h 30m",
                                0,
                                new BigDecimal("219.99"),
                                36,
                                "SCHEDULED",
                                "B4"
                        ),
                        createFlight(
                                "Group 3 Airways",
                                "G303",
                                "ATL",
                                "Atlanta",
                                "MCO",
                                "Orlando",
                                LocalDate.of(2026, 9, 25),
                                LocalTime.of(17, 15),
                                LocalTime.of(18, 50),
                                "1h 35m",
                                0,
                                new BigDecimal("249.99"),
                                28,
                                "SCHEDULED",
                                "C8"
                        ),
                        createFlight(
                                "Group 3 Airways",
                                "G401",
                                "MCO",
                                "Orlando",
                                "ATL",
                                "Atlanta",
                                LocalDate.of(2026, 9, 26),
                                LocalTime.of(10, 0),
                                LocalTime.of(11, 30),
                                "1h 30m",
                                0,
                                new BigDecimal("199.99"),
                                40,
                                "SCHEDULED",
                                "D6"
                        )
                );

                flightRepository.saveAll(flights);
                return;
            }

            updateExistingFlights(flightRepository);
        };
    }

    private void updateExistingFlights(
            FlightRepository flightRepository
    ) {
        List<Flight> existingFlights =
                flightRepository.findAll();

        boolean changesMade = false;

        for (Flight flight : existingFlights) {
            if (
                    flight.getStatus() == null ||
                            flight.getStatus().isBlank()
            ) {
                flight.setStatus("SCHEDULED");
                changesMade = true;
            }

            if (
                    flight.getGate() == null ||
                            flight.getGate().isBlank()
            ) {
                flight.setGate("TBD");
                changesMade = true;
            }
        }

        if (changesMade) {
            flightRepository.saveAll(existingFlights);
        }
    }

    private Flight createFlight(
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
        Flight flight = new Flight();

        flight.setAirline(airline);
        flight.setFlightNumber(flightNumber);
        flight.setOrigin(origin);
        flight.setOriginCity(originCity);
        flight.setDestination(destination);
        flight.setDestinationCity(destinationCity);
        flight.setDepartureDate(departureDate);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setDuration(duration);
        flight.setStops(stops);
        flight.setPrice(price);
        flight.setAvailableSeats(availableSeats);
        flight.setStatus(status);
        flight.setGate(gate);

        return flight;
    }
}