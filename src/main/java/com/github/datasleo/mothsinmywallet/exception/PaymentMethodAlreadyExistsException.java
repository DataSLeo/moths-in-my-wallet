package com.github.datasleo.mothsinmywallet.exception;

public class PaymentMethodAlreadyExistsException extends RuntimeException {
    
    public PaymentMethodAlreadyExistsException(String message) {
        super(message);
    }

}
