package com.github.datasleo.mothsinmywallet.exception;

/* 
 * Before this Exception was "AccountIdWasNotFoundException", but i refactored to "UnauthorizedAccountException" for more security.  
 * Imagine an scenario that hacker get this error, he can use this message to make a new attack.
 */

public class UnauthorizedAccountException extends RuntimeException {
    
    public UnauthorizedAccountException(String message) {
        super(message);
    }

}
