package com.group3airways.airlineapi.auth.dto;

import com.group3airways.airlineapi.user.entity.User;
import com.group3airways.airlineapi.user.entity.UserRole;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
