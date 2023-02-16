package com.elit.outildedecision.exception;

public class ResponseNotValidException extends Exception {
    public ResponseNotValidException() {
        super();
    }

    public ResponseNotValidException(String message) {
        super(message);
    }

    public ResponseNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseNotValidException(Throwable cause) {
        super(cause);
    }
}
