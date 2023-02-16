package com.elit.outildedecision.exception;

public class CouldNotSaveUserException extends Exception {

    public CouldNotSaveUserException() {
        super();
    }

    public CouldNotSaveUserException(String message) {
        super(message);
    }

    public CouldNotSaveUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotSaveUserException(Throwable cause) {
        super(cause);
    }
}
