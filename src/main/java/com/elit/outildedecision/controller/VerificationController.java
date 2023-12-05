package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.UserNotVerifiedException;
import com.elit.outildedecision.model.PasswordChangeModel;
import com.elit.outildedecision.service.VerificationTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
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
   * method used to reset password.
   * @param token the token used to reset password.
   *              the token is sent to user email.
   * @param changePasswordModel the new password.
   * @return ResponseEntity<String> the response of reset password.
   */
  @PostMapping("/reset-password")
  public ResponseEntity<String> reinstateForgotPassword(
      @RequestParam("token")final String token, @RequestBody final PasswordChangeModel changePasswordModel) {
    return verificationTokenService.reinstateForgotPassword(token, changePasswordModel);
  }
  /**
   * method used to send email to user to reset password.
   * @param email the email of user.
   * @return ResponseEntity<String> the response of sending email.
   * @throws JsonProcessingException when there is an error in parsing json.
   * @throws UserNotVerifiedException when user could not be verified.
   */
  @GetMapping(value = "/reset-password", consumes = "application/json")
  public ResponseEntity<String> forgotPassword(final HttpEntity<String> email)
      throws JsonProcessingException, UserNotVerifiedException {
    return verificationTokenService.sendForgotPasswordEmail(email.getBody());
  }
}
