package com.elit.outildedecision.exception;

public class FormStepAlreadyExistsException extends Exception {
    public FormStepAlreadyExistsException() {
        super();
    }

    public FormStepAlreadyExistsException(String message) {
        super(message);
    }

    public FormStepAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormStepAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    protected FormStepAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
