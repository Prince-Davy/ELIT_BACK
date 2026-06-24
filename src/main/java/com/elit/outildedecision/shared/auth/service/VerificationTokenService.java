
package com.elit.outildedecision.shared.auth.service;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.auth.entity.VerificationToken;
import com.elit.outildedecision.shared.auth.dto.PasswordChangeRequest;
/**
 * This interface defines the contract for the Verification Token Service, which provides methods to manage verification tokens.
 */
public interface VerificationTokenService {

  /**
   * Retrieves the VerificationToken object with the specified token.
   *
   * @param token a String representing the token of the verification token to retrieve
   * @return the VerificationToken object with the specified token
   */
  VerificationToken findByToken(String token);

  /**
   * Removes the VerificationToken object with the specified token.
   *
   * @param token a String representing the token of the verification token to remove
   */
  void removeByToken(String token);

  /**
   * Creates a new VerificationToken object for the specified user.
   *
   * @param user a User object representing the user to create the verification token for
   * @param type a String representing the type of verification token to create
   * @return the newly created VerificationToken object
   */
  VerificationToken createToken(User user, String type);

  /**
   * Confirms the user account associated with the specified token.
   *
   * @param token a String representing the token of the verification token to confirm the user account for
   * @return the business outcome of the confirmation
   */
  ConfirmationOutcome confirmUser(String token);

  /**
   * Reinstates a forgotten password for the user account associated with the specified token.
   *
   * @param token a String representing the token of the verification token to reinstate the forgotten password for
   * @param changePasswordModel a PasswordChangeRequest object representing the new password for the user account
   * @return the business outcome of the password reset
   */
  PasswordResetOutcome reinstateForgotPassword(
    String token, PasswordChangeRequest changePasswordModel);

  /**
   * Sends a reset password email to the user identified by the supplied JSON body.
   *
   * @param jsonRequestBody the JSON request body carrying the user e-mail
   * @return the business outcome of the reset-email request (and the message detail when needed)
   */
  ResetPasswordEmailResult sendResetPasswordEmail(String jsonRequestBody);
}

