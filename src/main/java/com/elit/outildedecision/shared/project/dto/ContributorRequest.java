package com.elit.outildedecision.shared.project.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Payload describing a contributor of a project. Used both as an input when attaching a
 * contributor and as an embedded item in the project response.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ContributorRequest implements Serializable {

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
   * A function of contributor.
   */
  private String func;

  /**
   * The avatar path of the contributor.
   */
  private String avatarPath;

  /**
   * The role of the contributor within the project.
   */
  @Pattern(regexp = "^(?i)(CONTRIBUTEUR|OBSERVATEUR|CDP)$", message = "Le role doit étre CONTRIBUTER, OBSERVER, ou CDP.")
  private String role;
}
