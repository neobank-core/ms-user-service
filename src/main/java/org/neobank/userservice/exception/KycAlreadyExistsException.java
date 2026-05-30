package org.neobank.userservice.exception;

public class KycAlreadyExistsException extends RuntimeException {
    public KycAlreadyExistsException(String message) {
        super(message);
    }
}
