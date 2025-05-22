package com.goldenowl.springboottemplate.app.exception;

public class SignUpNotValidException extends RuntimeException {
    public SignUpNotValidException(String message) {
        super(message);
    }
}
