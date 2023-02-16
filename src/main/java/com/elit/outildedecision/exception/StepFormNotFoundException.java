package com.elit.outildedecision.exception;

public class StepFormNotFoundException extends Exception {
    public StepFormNotFoundException() {
        super();
    }

    public StepFormNotFoundException(String message) {
        super(message);
    }

    public StepFormNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StepFormNotFoundException(Throwable cause) {
        super(cause);
    }

    protected StepFormNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
