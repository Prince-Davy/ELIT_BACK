package com.elit.outildedecision.exception;

public class ExistingBrainstormingException extends Exception{
    public ExistingBrainstormingException() {
    }

    public ExistingBrainstormingException(String message) {
        super(message);
    }

    public ExistingBrainstormingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExistingBrainstormingException(Throwable cause) {
        super(cause);
    }

    public ExistingBrainstormingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
