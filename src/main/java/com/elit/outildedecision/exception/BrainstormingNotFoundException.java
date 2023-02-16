package com.elit.outildedecision.exception;

public class BrainstormingNotFoundException extends Exception {
    public BrainstormingNotFoundException() {
    }

    public BrainstormingNotFoundException(String message) {
        super(message);
    }

    public BrainstormingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BrainstormingNotFoundException(Throwable cause) {
        super(cause);
    }

    public BrainstormingNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
