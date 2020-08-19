package com.postitapplications.user.exception;

public class NullOrEmptyException extends RuntimeException {

    public NullOrEmptyException(String errorMessage) {
        super(errorMessage);
    }
}
