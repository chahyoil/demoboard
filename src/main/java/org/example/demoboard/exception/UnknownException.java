package org.example.demoboard.exception;

public class UnknownException extends RuntimeException {
    public UnknownException(String message) {
        super(message);
    }
}