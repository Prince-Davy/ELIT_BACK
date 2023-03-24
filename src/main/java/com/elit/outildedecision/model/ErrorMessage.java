package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * This class represents an error message model containing information about an error that occurred during processing.
 * It is used to store and manage data associated with error messages, including the HTTP status and error message.
 * It uses Lombok annotations to generate constructors, getters, and setters, as well as a builder pattern.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorMessage {

    /**
     * The HTTP status code associated with the error.
     */
    private HttpStatus status;

    /**
     * A human-readable description of the error.
     */
    private String message;
}
