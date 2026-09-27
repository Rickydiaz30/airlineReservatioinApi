package com.group3airways.airlineapi.reservation.repository;

import com.group3airways.airlineapi.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByConfirmationNumber(
            String confirmationNumber
    );

    List<Reservation> findByUserEmailOrderByBookedAtDesc(
            String email
    );

    boolean existsByConfirmationNumber(
            String confirmationNumber
    );
}
