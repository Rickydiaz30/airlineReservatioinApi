package com.group3airways.airlineapi.auth.service;

import com.group3airways.airlineapi.auth.dto.LoginRequest;
import com.group3airways.airlineapi.auth.dto.RegisterRequest;
import com.group3airways.airlineapi.auth.dto.UserResponse;
import com.group3airways.airlineapi.common.exception.DuplicateEmailException;
import com.group3airways.airlineapi.common.exception.InvalidCredentialsException;
import com.group3airways.airlineapi.user.entity.User;
import com.group3airways.airlineapi.user.entity.UserRole;
import com.group3airways.airlineapi.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String dummyPasswordHash;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        // Used to reduce timing differences when an email is not found.
        this.dummyPasswordHash = passwordEncoder.encode(
                "not-a-real-user-password"
        );
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(
                request.email()
        );

        if (
                userRepository.existsByEmailIgnoreCase(
                        normalizedEmail
                )
        ) {
            throw new DuplicateEmailException();
        }

        User user = new User(
                request.firstName().trim(),
                request.lastName().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.password()),
                UserRole.CUSTOMER
        );

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(
                request.email()
        );

        Optional<User> optionalUser =
                userRepository.findByEmailIgnoreCase(
                        normalizedEmail
                );

        if (optionalUser.isEmpty()) {
            // Perform a password comparison even when the email
            // does not exist to reduce user-enumeration timing clues.
            passwordEncoder.matches(
                    request.password(),
                    dummyPasswordHash
            );

            throw new InvalidCredentialsException();
        }

        User user = optionalUser.get();

        if (
                !passwordEncoder.matches(
                        request.password(),
                        user.getPasswordHash()
                )
        ) {
            throw new InvalidCredentialsException();
        }

        return UserResponse.from(user);
    }

    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}