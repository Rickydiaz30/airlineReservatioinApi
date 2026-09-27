package com.group3airways.airlineapi.flight.repository;

import com.group3airways.airlineapi.flight.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight>
    findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureDateOrderByDepartureTimeAsc(
            String origin,
            String destination,
            LocalDate departureDate
    );
}
