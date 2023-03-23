package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

/**
 * This class represents a password change model containing information about a password change request.
 * It is used to store and manage data associated with password changes, including old password, new password, and confirm password fields.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeModel {

  /**
   * The user's old password.
   */
  private String oldPassword;

  /**
   * The user's new password.
   * It must match the password pattern specified in the @Pattern annotation.
   */
  @Pattern(
    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
    message =
      "le mot de passe doit contenir au moins 8 caractères,"
        + " une lettre majuscule, une lettre minuscule,"
        + " un chiffre et un caractère spécial")
  private String newPassword;

  /**
   * The confirmation of the user's new password.
   * It must match the password pattern specified in the @Pattern annotation.
   */
  @Pattern(
    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
    message =
      "le mot de passe doit contenir au moins 8 caractères,"
        + " une lettre majuscule, une lettre minuscule,"
        + " un chiffre et un caractère spécial")
  private String confirmPassword;
}
