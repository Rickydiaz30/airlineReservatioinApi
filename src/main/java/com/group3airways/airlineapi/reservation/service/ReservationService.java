package com.group3airways.airlineapi.reservation.service;

import com.group3airways.airlineapi.flight.entity.Flight;
import com.group3airways.airlineapi.flight.repository.FlightRepository;
import com.group3airways.airlineapi.reservation.dto.CreateReservationRequest;
import com.group3airways.airlineapi.reservation.dto.ReservationResponse;
import com.group3airways.airlineapi.reservation.entity.Reservation;
import com.group3airways.airlineapi.reservation.entity.ReservationStatus;
import com.group3airways.airlineapi.reservation.repository.ReservationRepository;
import com.group3airways.airlineapi.user.entity.User;
import com.group3airways.airlineapi.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            FlightRepository flightRepository,
            UserRepository userRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse createReservation(
            CreateReservationRequest request
    ) {
        validateCreateRequest(request);

        String normalizedUserEmail =
                request.userEmail().trim().toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCase(normalizedUserEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User account not found"
                ));

        Flight flight = flightRepository
                .findById(request.flightId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Flight not found with ID: " + request.flightId()
                ));

        if (flight.getDepartureDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot reserve a flight that has already departed"
            );
        }

        if (flight.getAvailableSeats() == null
                || flight.getAvailableSeats() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No seats are available for this flight"
            );
        }

        Reservation reservation = new Reservation(
                generateConfirmationNumber(),
                user,
                flight,
                request.passengerFirstName().trim(),
                request.passengerLastName().trim(),
                request.passengerEmail()
                        .trim()
                        .toLowerCase(Locale.ROOT),
                normalizeOptionalText(request.passengerPhone()),
                request.passengerDateOfBirth(),
                request.seatPreference()
                        .trim()
                        .toUpperCase(Locale.ROOT),
                flight.getPrice()
        );

        flight.setAvailableSeats(
                flight.getAvailableSeats() - 1
        );

        flightRepository.save(flight);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        return ReservationResponse.fromEntity(savedReservation);
    }

    public List<ReservationResponse> getReservationsForUser(
            String userEmail
    ) {
        if (isBlank(userEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User email is required"
            );
        }

        return reservationRepository
                .findByUserEmailOrderByBookedAtDesc(
                        userEmail.trim().toLowerCase(Locale.ROOT)
                )
                .stream()
                .map(ReservationResponse::fromEntity)
                .toList();
    }

    public ReservationResponse getReservation(
            String confirmationNumber
    ) {
        Reservation reservation = findByConfirmationNumber(
                confirmationNumber
        );

        return ReservationResponse.fromEntity(reservation);
    }

    @Transactional
    public ReservationResponse cancelReservation(
            String confirmationNumber,
            String userEmail
    ) {
        if (isBlank(userEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User email is required"
            );
        }

        Reservation reservation = findByConfirmationNumber(
                confirmationNumber
        );

        if (!reservation.getUser()
                .getEmail()
                .equalsIgnoreCase(userEmail.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This reservation does not belong to that user"
            );
        }

        if (reservation.getStatus()
                == ReservationStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Reservation is already cancelled"
            );
        }

        reservation.cancel();

        Flight flight = reservation.getFlight();

        flight.setAvailableSeats(
                flight.getAvailableSeats() + 1
        );

        flightRepository.save(flight);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        return ReservationResponse.fromEntity(savedReservation);
    }

    private Reservation findByConfirmationNumber(
            String confirmationNumber
    ) {
        if (isBlank(confirmationNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Confirmation number is required"
            );
        }

        return reservationRepository
                .findByConfirmationNumber(
                        confirmationNumber.trim().toUpperCase(Locale.ROOT)
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"
                ));
    }

    private void validateCreateRequest(
            CreateReservationRequest request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reservation information is required"
            );
        }

        if (isBlank(request.userEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User email is required"
            );
        }

        if (request.flightId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Flight ID is required"
            );
        }

        if (isBlank(request.passengerFirstName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passenger first name is required"
            );
        }

        if (isBlank(request.passengerLastName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passenger last name is required"
            );
        }

        if (isBlank(request.passengerEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passenger email is required"
            );
        }

        if (request.passengerDateOfBirth() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passenger date of birth is required"
            );
        }

        if (!request.passengerDateOfBirth()
                .isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Passenger date of birth must be in the past"
            );
        }

        if (isBlank(request.seatPreference())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Seat preference is required"
            );
        }

        String seatPreference = request.seatPreference()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!List.of("WINDOW", "MIDDLE", "AISLE")
                .contains(seatPreference)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Seat preference must be WINDOW, MIDDLE, or AISLE"
            );
        }
    }

    private String generateConfirmationNumber() {
        String confirmationNumber;

        do {
            confirmationNumber = "G3-"
                    + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase(Locale.ROOT);
        } while (reservationRepository
                .existsByConfirmationNumber(confirmationNumber));

        return confirmationNumber;
    }

    private String normalizeOptionalText(String value) {
        if (isBlank(value)) {
            return null;
        }

        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}