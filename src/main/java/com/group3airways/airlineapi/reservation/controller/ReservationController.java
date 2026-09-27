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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.server.ResponseStatusException;

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
            @RequestBody CreateReservationRequest request,
            HttpServletRequest servletRequest
    ) {
        if (request.userEmail() == null || !request.userEmail().equalsIgnoreCase(currentEmail(servletRequest))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Reservation owner does not match signed-in user");
        }
        return reservationService.createReservation(request);
    }

    @GetMapping
    public List<ReservationResponse> getReservationsForUser(
            HttpServletRequest servletRequest
    ) {
        return reservationService.getReservationsForUser(currentEmail(servletRequest));
    }

    @GetMapping("/{confirmationNumber}")
    public ReservationResponse getReservation(
            @PathVariable String confirmationNumber,
            HttpServletRequest servletRequest
    ) {
        ReservationResponse reservation = reservationService.getReservation(confirmationNumber);
        if (!reservation.userEmail().equalsIgnoreCase(currentEmail(servletRequest))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found");
        }
        return reservation;
    }

    @PatchMapping("/{confirmationNumber}/cancel")
    public ReservationResponse cancelReservation(
            @PathVariable String confirmationNumber,
            HttpServletRequest servletRequest
    ) {
        return reservationService.cancelReservation(
                confirmationNumber,
                currentEmail(servletRequest)
        );
    }

    private String currentEmail(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("userEmail") instanceof String email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in");
        }
        return email;
    }
}
