package com.elit.outildedecision.exception;

public class QuestionsNotFoundException extends Exception {

    public QuestionsNotFoundException() {
        super();
    }

    public QuestionsNotFoundException(String message) {
        super(message);
    }

    public QuestionsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuestionsNotFoundException(Throwable cause) {
        super(cause);
    }
}
