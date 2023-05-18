package com.gerp.shared.exception;

public class ServiceValidationException extends RuntimeException{
    public ServiceValidationException(String message) {
        super(message);
    }
}
