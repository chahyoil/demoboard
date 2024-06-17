package org.example.demoboard.exception;

public class UpdateDeleteException extends RuntimeException {
    public UpdateDeleteException(String message) {
        super(message);
    }
}