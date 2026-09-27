package com.group3airways.airlineapi.flight.service;

import com.group3airways.airlineapi.flight.dto.FlightResponse;
import com.group3airways.airlineapi.flight.entity.Flight;
import com.group3airways.airlineapi.flight.repository.FlightRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class FlightService {

    private static final Map<String, String> AIRPORT_CITIES = Map.ofEntries(
            Map.entry("DEN", "Denver"),
            Map.entry("MCO", "Orlando"),
            Map.entry("LAS", "Las Vegas"),
            Map.entry("LAX", "Los Angeles"),
            Map.entry("SEA", "Seattle"),
            Map.entry("ORD", "Chicago"),
            Map.entry("JFK", "New York"),
            Map.entry("ATL", "Atlanta"),
            Map.entry("DFW", "Dallas"),
            Map.entry("PHX", "Phoenix"),
            Map.entry("SFO", "San Francisco"),
            Map.entry("OMA", "Omaha")
    );

    private static final List<LocalTime> DEPARTURE_TIMES = List.of(
            LocalTime.of(6, 0),
            LocalTime.of(7, 45),
            LocalTime.of(9, 30),
            LocalTime.of(11, 15),
            LocalTime.of(13, 0),
            LocalTime.of(15, 30),
            LocalTime.of(17, 45),
            LocalTime.of(20, 15)
    );

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll()
                .stream()
                .map(FlightResponse::fromEntity)
                .toList();
    }

    public FlightResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Flight not found with ID: " + id
                ));

        return FlightResponse.fromEntity(flight);
    }

    @Transactional
    public List<FlightResponse> searchFlights(
            String origin,
            String destination,
            LocalDate departureDate
    ) {
        String normalizedOrigin = normalizeAirportCode(origin);
        String normalizedDestination = normalizeAirportCode(destination);

        validateSearch(
                normalizedOrigin,
                normalizedDestination,
                departureDate
        );

        List<Flight> flights = flightRepository
                .findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureDateOrderByDepartureTimeAsc(
                        normalizedOrigin,
                        normalizedDestination,
                        departureDate
                );

        if (flights.isEmpty()) {
            flights = generateFlights(
                    normalizedOrigin,
                    normalizedDestination,
                    departureDate
            );

            flights = flightRepository.saveAll(flights);
        }

        return flights.stream()
                .map(FlightResponse::fromEntity)
                .toList();
    }

    private String normalizeAirportCode(String airportCode) {
        if (airportCode == null) {
            return "";
        }

        return airportCode.trim().toUpperCase();
    }

    private void validateSearch(
            String origin,
            String destination,
            LocalDate departureDate
    ) {
        if (!AIRPORT_CITIES.containsKey(origin)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported origin airport: " + origin
            );
        }

        if (!AIRPORT_CITIES.containsKey(destination)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported destination airport: " + destination
            );
        }

        if (origin.equals(destination)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Origin and destination must be different"
            );
        }

        if (departureDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Departure date is required"
            );
        }
    }

    private List<Flight> generateFlights(
            String origin,
            String destination,
            LocalDate departureDate
    ) {
        List<Flight> generatedFlights = new ArrayList<>();

        int routeSeed = Math.abs(
                (origin + destination + departureDate).hashCode()
        );

        for (int index = 0; index < DEPARTURE_TIMES.size(); index++) {
            int durationMinutes =
                    90 + Math.floorMod(routeSeed + index * 37, 240);

            LocalTime departureTime = DEPARTURE_TIMES.get(index);
            LocalTime arrivalTime =
                    departureTime.plusMinutes(durationMinutes);

            int stops = index < 5 ? 0 : 1;

            BigDecimal price = BigDecimal.valueOf(
                    149.99 + Math.floorMod(routeSeed + index * 43, 350)
            );

            Flight flight = new Flight();

            flight.setAirline("Group 3 Airways");
            flight.setFlightNumber(
                    createFlightNumber(routeSeed, index)
            );

            flight.setOrigin(origin);
            flight.setOriginCity(AIRPORT_CITIES.get(origin));

            flight.setDestination(destination);
            flight.setDestinationCity(
                    AIRPORT_CITIES.get(destination)
            );

            flight.setDepartureDate(departureDate);
            flight.setDepartureTime(departureTime);
            flight.setArrivalTime(arrivalTime);

            flight.setDuration(formatDuration(durationMinutes));
            flight.setStops(stops);
            flight.setPrice(price);

            flight.setAvailableSeats(
                    20 + Math.floorMod(routeSeed + index * 11, 80)
            );

            flight.setStatus("SCHEDULED");
            flight.setGate(
                    "A" + (1 + Math.floorMod(routeSeed + index, 30))
            );

            generatedFlights.add(flight);
        }

        return generatedFlights;
    }

    private String createFlightNumber(int routeSeed, int index) {
        int number = 100 + Math.floorMod(
                routeSeed + index * 17,
                900
        );

        return "G" + number;
    }

    private String formatDuration(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        return hours + "h " + minutes + "m";
    }
}