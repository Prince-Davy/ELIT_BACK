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
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel implements Serializable {
  @NotBlank(message = "username est obligatoire")
  private String username;

  @NotBlank(message = "le nom est requis")
  private String userLastName;

  @NotBlank(message = "le prenom est requis")
  private String userFirstName;

  @Email(message = "email invalide")
  @NotBlank(message = "email est obligatoire")
  private String userEmail;

  @NotBlank(message = "metier est obligatoire")
  private String userJob;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
      message =
          "le mot de passe doit contenir au moins 8 caractères,"
            + " une lettre majuscule, une lettre minuscule,"
            + " un chiffre et un caractère spécial")
  private String userPassword;

  private String userEmailConfirmation;

  private String userPasswordConfirmation;
  private MultipartFile userAvatar;
}
