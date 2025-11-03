package com.github.datasleo.mothsinmywallet.exception;

public class PaymentMethodNotFoundOrNotAuthorizedException extends RuntimeException {
    
    public PaymentMethodNotFoundOrNotAuthorizedException(String message) {
        super(message);
    }

}
