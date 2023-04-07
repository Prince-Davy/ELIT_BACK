
package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.exception.UserNotVerifiedException;
import com.elit.outildedecision.model.PasswordChangeModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
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
   * @return a ResponseEntity indicating the success or failure of the confirmation operation
   */
  ResponseEntity<String> confirmUser(String token);

  /**
   * Reinstates a forgotten password for the user account associated with the specified token.
   *
   * @param token a String representing the token of the verification token to reinstate the forgotten password for
   * @param changePasswordModel a PasswordChangeModel object representing the new password for the user account
   * @return a ResponseEntity indicating the success or failure of the password change operation
   */
  ResponseEntity<String> reinstateForgotPassword(
    String token, PasswordChangeModel changePasswordModel);

  /**
   * Sends a forgot password email to the user with the specified email address.
   *
   * @param email a String representing the email address of the user to send the forgot password email to
   * @return a ResponseEntity indicating the success or failure of the email send operation
   * @throws JsonProcessingException if an error occurs while processing the JSON data for the email
   * @throws UserNotVerifiedException if the specified user account has not been verified
   */
  ResponseEntity<String> forgotPassword(String email)
    throws JsonProcessingException, UserNotVerifiedException;
}

