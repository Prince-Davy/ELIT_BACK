package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a response containing user information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseModel {

  /**
   * The ID of the user.
   */
  private Long userId;

  /**
   * The username of the user.
   */
  private String username;

  /**
   * The last name of the user.
   */
  private String userLastName;

  /**
   * The first name of the user.
   */
  private String userFirstName;

  /**
   * The email address of the user.
   */
  private String userEmail;

  /**
   * The job title of the user.
   */
  private String userJob;

  /**
   * The avatar image of the user, represented as a byte array.
   */
  private byte[] userAvatar;
}
