package com.group3airways.airlineapi.reservation.dto;

import com.group3airways.airlineapi.flight.dto.FlightResponse;
import com.group3airways.airlineapi.reservation.entity.Reservation;
import com.group3airways.airlineapi.reservation.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        String confirmationNumber,
        String userEmail,
        FlightResponse flight,
        String passengerFirstName,
        String passengerLastName,
        String passengerEmail,
        String passengerPhone,
        LocalDate passengerDateOfBirth,
        String seatPreference,
        BigDecimal totalPrice,
        ReservationStatus status,
        Instant bookedAt,
        Instant cancelledAt
) {

    public static ReservationResponse fromEntity(
            Reservation reservation
    ) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getConfirmationNumber(),
                reservation.getUser().getEmail(),
                FlightResponse.fromEntity(reservation.getFlight()),
                reservation.getPassengerFirstName(),
                reservation.getPassengerLastName(),
                reservation.getPassengerEmail(),
                reservation.getPassengerPhone(),
                reservation.getPassengerDateOfBirth(),
                reservation.getSeatPreference(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getBookedAt(),
                reservation.getCancelledAt()
        );
    }
}
