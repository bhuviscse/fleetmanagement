package com.fleetmanagement.system.exception;

public class ValidationException extends RuntimeException {
    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Exception ex) {
        super(message, ex);
    }
}
