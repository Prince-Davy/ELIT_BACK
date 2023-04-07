package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an email details model containing information about an email message.
 * It is used to store and manage data associated with email messages, including recipient, message body, subject, and attachments.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

  /**
   * The email address of the recipient.
   */
  private String recipient;

  /**
   * The body of the email message.
   */
  private String msgBody;

  /**
   * The subject of the email message.
   */
  private String subject;

  /**
   * The file path of the attachment, if any.
   */
  private String attachment;
}
