package com.elit.outildedecision.shared.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload exposing the public information of a user.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserResponse {

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
   * The avatar image of the user, represented as a byte array.
   */
  private byte[] userAvatar;
}
