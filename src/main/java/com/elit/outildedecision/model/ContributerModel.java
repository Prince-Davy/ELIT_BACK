package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * This class represents a contributor model containing information about a contributor.
 * It is used to store and manage data associated with users who contribute to the project.
 * The class implements Serializable interface to allow its instances to be converted to a byte stream.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContributerModel implements Serializable {

    /**
     * A unique identifier for the contributor.
     */
    private Long contributerId;

    /**
     * The first name of the contributor.
     */
    private String firstName;

    /**
     * The last name of the contributor.
     */
    private String lastName;

    /**
     * The email address of the contributor.
     */
    private String email;

    /**
     * The job or occupation of the contributor.
     */
    private String job;

    /**
     * A boolean flag indicating whether the contributor has administrative privileges.
     */
    private Boolean isAdmin;

    /**
     * The role of the contributor within the project.
     */
    private String role;
}
