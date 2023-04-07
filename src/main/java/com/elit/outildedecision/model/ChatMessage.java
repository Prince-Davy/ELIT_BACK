package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Represents a chat message with username, avatar, message body, and status.
 */
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
  /**
   * The username of the user who sent the chat message.
   */
  private String username;

  /**
   * The avatar of the user who sent the chat message.
   */
  private String avatar;

  /**
   * The content of the chat message.
   */
  private String messageBody;

  /**
   * The status of the chat message.
   */
  private String status;

  /**
   * Returns the username of the user who sent the chat message.
   * @return The username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the user who sent the chat message.
   * @param username The username to set.
   */
  public void setUsername(final String username) {
    this.username = username;
  }

  /**
   * Returns the avatar of the user who sent the chat message.
   * @return The avatar.
   */
  public String getAvatar() {
    return avatar;
  }

  /**
   * Sets the avatar of the user who sent the chat message.
   * @param avatar The avatar to set.
   */
  public void setAvatar(final String avatar) {
    this.avatar = avatar;
  }

  /**
   * Returns the content of the chat message.
   * @return The message body.
   */
  public String getMessageBody() {
    return messageBody;
  }

  /**
   * Sets the content of the chat message.
   * @param messageBody The message body to set.
   */
  public void setMessageBody(final String messageBody) {
    this.messageBody = messageBody;
  }

  /**
   * Returns the status of the chat message.
   * @return The status.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the status of the chat message.
   * @param status The status to set.
   */
  public void setStatus(final String status) {
    this.status = status;
  }
}
