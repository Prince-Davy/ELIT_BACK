package com.elit.outildedecision.exception;

public class VoteAlreadyExistException extends Exception {

    public VoteAlreadyExistException() {
        super();
    }

    public VoteAlreadyExistException(String message) {
        super(message);
    }

    public VoteAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public VoteAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
