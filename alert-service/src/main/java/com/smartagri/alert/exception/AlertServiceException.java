package com.smartagri.alert.exception;

public class AlertServiceException extends RuntimeException {
    public AlertServiceException(String message) {
        super(message);
    }

    public AlertServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}