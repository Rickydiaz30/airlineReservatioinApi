package com.group3airways.airlineapi.reservation.dto;

import java.time.LocalDate;

public record CreateReservationRequest(
        String userEmail,
        Long flightId,
        String passengerFirstName,
        String passengerLastName,
        String passengerEmail,
        String passengerPhone,
        LocalDate passengerDateOfBirth,
        String seatPreference
) {
}
