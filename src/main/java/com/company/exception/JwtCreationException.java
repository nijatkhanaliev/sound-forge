package com.company.exception;

public class JwtCreationException extends RuntimeException {
    public JwtCreationException(String msg) {
        super(msg);
    }
}
