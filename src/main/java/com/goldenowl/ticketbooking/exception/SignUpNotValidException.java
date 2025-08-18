package com.goldenowl.ticketbooking.exception;

public class SignUpNotValidException extends RuntimeException {
    public SignUpNotValidException(String message) {
        super(message);
    }
}
