package com.elit.outildedecision.exception;



public class FormAlreadyExistsException extends Exception{
    public FormAlreadyExistsException() {
        super();
    }

    public FormAlreadyExistsException(String message) {
        super(message);
    }

    public FormAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    protected FormAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
