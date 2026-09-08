package com.group3airways.airlineapi.common.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("An account with that email already exists.");
    }
}
