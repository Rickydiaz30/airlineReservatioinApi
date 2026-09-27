package com.group3airways.airlineapi.reservation.controller;

import com.group3airways.airlineapi.reservation.dto.CreateReservationRequest;
import com.group3airways.airlineapi.reservation.dto.ReservationResponse;
import com.group3airways.airlineapi.reservation.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(
            @RequestBody CreateReservationRequest request
    ) {
        return reservationService.createReservation(request);
    }

    @GetMapping
    public List<ReservationResponse> getReservationsForUser(
            @RequestParam String userEmail
    ) {
        return reservationService.getReservationsForUser(userEmail);
    }

    @GetMapping("/{confirmationNumber}")
    public ReservationResponse getReservation(
            @PathVariable String confirmationNumber
    ) {
        return reservationService.getReservation(
                confirmationNumber
        );
    }

    @PatchMapping("/{confirmationNumber}/cancel")
    public ReservationResponse cancelReservation(
            @PathVariable String confirmationNumber,
            @RequestParam String userEmail
    ) {
        return reservationService.cancelReservation(
                confirmationNumber,
                userEmail
        );
    }
}
