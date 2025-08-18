package com.goldenowl.ticketbooking.exception;

public class LoginNotValidException extends RuntimeException {
    public LoginNotValidException(String message) {
        super(message);
    }
}
