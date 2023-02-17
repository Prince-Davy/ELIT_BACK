package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeModel {
  private String oldPassword;

  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
      message =
          "le mot de passe doit contenir au moins 8 caractères," +
            " une lettre majuscule, une lettre minuscule," +
            " un chiffre et un caractère spécial")
  private String newPassword;

  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
      message =
          "le mot de passe doit contenir au moins 8 caractères," +
            " une lettre majuscule, une lettre minuscule," +
            " un chiffre et un caractère spécial")
  private String confirmPassword;
}
