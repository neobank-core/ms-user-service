package org.neobank.userservice.exception;

public class KycNotFoundException extends RuntimeException {
    public KycNotFoundException(String message) {
        super(message);
    }
}
