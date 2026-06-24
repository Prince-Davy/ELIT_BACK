package com.elit.outildedecision.shared.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Pattern;

/**
 * Request payload carrying a password change (new password and its confirmation).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PasswordChangeRequest {

  /**
   * The user's old password.
   */
  private String oldPassword;

  /**
   * The user's new password.
   * It must match the password pattern specified in the @Pattern annotation.
   */
  @Pattern(
    regexp = "^(?=.*[~!@#$%^&*()_+`\\-=\\[\\]\\{\\};':\\\",./<>?éàè])(?=.*\\d)(?=.*[a-zéàè])(?=.*[A-ZZÉÀÈ])\\S{12,32}$",
    message =
      "Veuillez vérifier que le mot de passe inscrit comporte bien les caractères recommandés.")
  private String newPassword;

  /**
   * The confirmation of the user's new password.
   * It must match the password pattern specified in the @Pattern annotation.
   */
  @Pattern(
    regexp = "^(?=.*[~!@#$%^&*()_+`\\-=\\[\\]\\{\\};':\\\",./<>?éàè])(?=.*\\d)(?=.*[a-zéàè])(?=.*[A-ZZÉÀÈ])\\S{12,32}$",
    message =
      "Veuillez vérifier que le mot de passe inscrit comporte bien les caractères recommandés.")
  private String confirmPassword;
}
