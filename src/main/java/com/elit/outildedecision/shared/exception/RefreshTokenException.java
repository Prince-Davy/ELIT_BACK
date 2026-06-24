package com.elit.outildedecision.shared.exception;

/** Refresh token exception class. */
public class RefreshTokenException extends Exception {
    /**
     * Constructor for the RefreshTokenException class.
     * Exception thrown when the refresh token is not valid.
     * @param message the message to be displayed
     */
    public RefreshTokenException(final String message) {
        super(message);
    }
}
