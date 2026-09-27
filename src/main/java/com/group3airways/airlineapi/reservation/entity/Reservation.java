package com.group3airways.airlineapi.reservation.entity;

import com.group3airways.airlineapi.flight.entity.Flight;
import com.group3airways.airlineapi.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "confirmation_number",
            nullable = false,
            unique = true,
            length = 20
    )
    private String confirmationNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "flight_id",
            nullable = false
    )
    private Flight flight;

    @Column(
            name = "passenger_first_name",
            nullable = false,
            length = 100
    )
    private String passengerFirstName;

    @Column(
            name = "passenger_last_name",
            nullable = false,
            length = 100
    )
    private String passengerLastName;

    @Column(
            name = "passenger_email",
            nullable = false,
            length = 254
    )
    private String passengerEmail;

    @Column(
            name = "passenger_phone",
            length = 30
    )
    private String passengerPhone;

    @Column(
            name = "passenger_date_of_birth",
            nullable = false
    )
    private LocalDate passengerDateOfBirth;

    @Column(
            name = "seat_preference",
            nullable = false,
            length = 20
    )
    private String seatPreference;

    @Column(
            name = "total_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private ReservationStatus status;

    @Column(
            name = "booked_at",
            nullable = false,
            updatable = false
    )
    private Instant bookedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    protected Reservation() {
        // Required by JPA.
    }

    public Reservation(
            String confirmationNumber,
            User user,
            Flight flight,
            String passengerFirstName,
            String passengerLastName,
            String passengerEmail,
            String passengerPhone,
            LocalDate passengerDateOfBirth,
            String seatPreference,
            BigDecimal totalPrice
    ) {
        this.confirmationNumber = confirmationNumber;
        this.user = user;
        this.flight = flight;
        this.passengerFirstName = passengerFirstName;
        this.passengerLastName = passengerLastName;
        this.passengerEmail = passengerEmail;
        this.passengerPhone = passengerPhone;
        this.passengerDateOfBirth = passengerDateOfBirth;
        this.seatPreference = seatPreference;
        this.totalPrice = totalPrice;
        this.status = ReservationStatus.CONFIRMED;
    }

    @PrePersist
    void setBookedAt() {
        this.bookedAt = Instant.now();

        if (this.status == null) {
            this.status = ReservationStatus.CONFIRMED;
        }
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public User getUser() {
        return user;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getPassengerFirstName() {
        return passengerFirstName;
    }

    public String getPassengerLastName() {
        return passengerLastName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public LocalDate getPassengerDateOfBirth() {
        return passengerDateOfBirth;
    }

    public String getSeatPreference() {
        return seatPreference;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Instant getBookedAt() {
        return bookedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }
}