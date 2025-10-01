package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.UserNotVerifiedException;
import com.elit.outildedecision.model.PasswordChangeModel;
import com.elit.outildedecision.service.VerificationTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    return verificationTokenService.confirmUser(token);
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
      @RequestParam("token")final String token, @RequestBody final PasswordChangeModel changePasswordModel) {
    return verificationTokenService.reinstateForgotPassword(token, changePasswordModel);
  }

  /**
   * Send a reset password email to the concerned user email address.
   * @param jsonRequestBody the json request body containing the user email address.
   * @return ResponseEntity<String> the response of sending the reset email.
   */
  @PostMapping("/send-reset-password-email")
  public ResponseEntity<String> sendResetPasswordEmail(@RequestBody final String jsonRequestBody) {
    try {
      return verificationTokenService.sendResetPasswordEmail(jsonRequestBody);
    } catch (JsonProcessingException e) {
      return ResponseEntity.badRequest().body("JSON processing error: " + e.getMessage());
    } catch (UserNotVerifiedException e) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("User verification error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
    }
  }

}
