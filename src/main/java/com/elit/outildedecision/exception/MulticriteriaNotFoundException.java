package com.elit.outildedecision.exception;

public class MulticriteriaNotFoundException extends Exception {
    public MulticriteriaNotFoundException() {
    }

    public MulticriteriaNotFoundException(String message) {
        super(message);
    }

    public MulticriteriaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MulticriteriaNotFoundException(Throwable cause) {
        super(cause);
    }

    public MulticriteriaNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
