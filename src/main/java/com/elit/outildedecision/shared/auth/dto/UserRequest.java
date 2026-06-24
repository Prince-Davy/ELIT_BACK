package com.elit.outildedecision.shared.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Registration request payload carrying the data submitted by a new user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserRequest {

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
    regexp = "^(?=.*[~!@#$%^&*()_+`\\-=\\[\\].;':,/<>?])(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])\\S{12,32}$",
    message =
      "le mot de passe doit contenir au moins 12 caractères,"
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
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String userPasswordConfirmation;

  /**
   * The avatar file of the user.
   */
  private MultipartFile userAvatar;
}
