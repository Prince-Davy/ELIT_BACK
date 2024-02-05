package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
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
     * A boolean flag indicating whether the contributor has administrative privileges.
     */
    private Boolean isAdmin;

  /**
   * A function of contributer.
   */
    private String func;


    private String avatarPath;

    /**
     * The role of the contributor within the project.
     */
    @Pattern(regexp = "^(CONTRIBUTER|OBSERVER|CDP)$", message = "Le role doit étre CONTRIBUTER, OBSERVER, ou CDP.")
    private String role;
}
