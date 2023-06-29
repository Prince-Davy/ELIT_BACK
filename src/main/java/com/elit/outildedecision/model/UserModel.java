package com.elit.outildedecision.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Represents a user of the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {

  /**
   * The username of the user.
   */
  @NotBlank(message = "username est obligatoire")
  private String username;

  /**
   * The last name of the user.
   */
  @NotBlank(message = "le nom est requis")
  private String userLastName;

  /**
   * The first name of the user.
   */
  @NotBlank(message = "le prenom est requis")
  private String userFirstName;

  /**
   * The email address of the user.
   */
  @Email(message = "email invalide")
  @NotBlank(message = "email est obligatoire")
  private String userEmail;

  /**
   * The password of the user.
   */
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Pattern(
    regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
    message =
      "le mot de passe doit contenir au moins 8 caractères,"
        + " une lettre majuscule, une lettre minuscule,"
        + " un chiffre et un caractère spécial")
  private String userPassword;

  /**
   * The email confirmation of the user.
   */
  private String userEmailConfirmation;

  /**
   * The password confirmation of the user.
   */
  private String userPasswordConfirmation;

  /**
   * The avatar file of the user.
   */
  private MultipartFile userAvatar;
}
