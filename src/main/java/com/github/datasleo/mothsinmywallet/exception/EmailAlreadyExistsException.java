package com.github.datasleo.mothsinmywallet.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException (String message) {
        super(message);
    }
}
