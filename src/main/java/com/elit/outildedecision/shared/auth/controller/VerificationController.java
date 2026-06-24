package com.elit.outildedecision.shared.auth.controller;

import com.elit.outildedecision.shared.auth.dto.PasswordChangeRequest;
import com.elit.outildedecision.shared.auth.service.ResetPasswordEmailResult;
import com.elit.outildedecision.shared.auth.service.VerificationTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * class used to verify user account and reset password.
 */
@RestController
@Tag(name = "Shared - Verification", description = "Vérification de compte et réinitialisation du mot de passe")
public class VerificationController {
  /**
   * verificationTokenService used to verify user account and reset password.
   */
  private final VerificationTokenService verificationTokenService;
  /**
   * The constructor of VerificationController.
   *
   * dependency injection of VerificationTokenService.
   *
   * @param verificationTokenService the service that used to verify user account and reset password.
   */
  public VerificationController(final VerificationTokenService verificationTokenService) {
    this.verificationTokenService = verificationTokenService;
  }

  /**
   * method used to verify user account.
   * @param token the token used to verify user account.
   *              the token is sent to user email.
   * @return ResponseEntity<String> the response of verification.
   */
  @GetMapping("/confirm-account")
  public ResponseEntity<String> confirmAccount(@RequestParam("token")final String token) {
    return switch (verificationTokenService.confirmUser(token)) {
      case CONFIRMED -> ResponseEntity.ok("Account confirmed");
      case INVALID_TOKEN -> ResponseEntity.badRequest().body("Invalid token");
      case EXPIRED -> ResponseEntity.badRequest().body("Token expired");
    };
  }
  /**
   * Reset the user password.
   * @param token the token used to reset password.
   *              the token is sent to user email address.
   * @param changePasswordModel the new password.
   * @return ResponseEntity<String> the response of reset password.
   */
  @PostMapping("/reset-password")
  public ResponseEntity<String> reinstateForgotPassword(
      @RequestParam("token")final String token, @RequestBody final PasswordChangeRequest changePasswordModel) {
    return switch (verificationTokenService.reinstateForgotPassword(token, changePasswordModel)) {
      case UPDATED -> ResponseEntity.ok("Le mot de passe est mis à jour");
      case INVALID_LINK -> ResponseEntity.badRequest().body("le lien est invalide");
      case EXPIRED_LINK -> ResponseEntity.badRequest().body("le lien est expiré");
      case PASSWORD_MISMATCH -> ResponseEntity.badRequest().body("les mots de passes ne sont pas identiques");
    };
  }

  /**
   * Send a reset password email to the concerned user email address.
   * @param jsonRequestBody the json request body containing the user email address.
   * @return ResponseEntity<String> the response of sending the reset email.
   */
  @PostMapping("/send-reset-password-email")
  public ResponseEntity<String> sendResetPasswordEmail(@RequestBody final String jsonRequestBody) {
    ResetPasswordEmailResult result = verificationTokenService.sendResetPasswordEmail(jsonRequestBody);
    return switch (result.outcome()) {
      case SENT -> ResponseEntity.ok("Email de récupération envoyé");
      case ACCOUNT_NOT_FOUND -> ResponseEntity.badRequest()
          .body("Aucun compte n'a été trouvé pour l'e-mail " + result.detail() + ", Veuillez réessayer.");
      case NOT_VERIFIED -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
          .body("User verification error: " + result.detail());
      case INVALID_JSON -> ResponseEntity.badRequest()
          .body("JSON processing error: " + result.detail());
      case MAIL_FAILED -> ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
          .body("Une erreur est survenue lors de l'envoi du mail de récupération de mot de passe."
              + " Veuillez réessayer plus tard;");
    };
  }

}
