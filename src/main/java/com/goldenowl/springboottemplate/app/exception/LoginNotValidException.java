package com.goldenowl.springboottemplate.app.exception;

public class LoginNotValidException extends RuntimeException {
    public LoginNotValidException(String message) {
        super(message);
    }
}
