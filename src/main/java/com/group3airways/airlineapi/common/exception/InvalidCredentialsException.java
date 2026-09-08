package com.group3airways.airlineapi.common.exception;

public class InvalidCredentialsException
        extends RuntimeException {

    public InvalidCredentialsException() {
        super("The email or password is incorrect.");
    }
}
