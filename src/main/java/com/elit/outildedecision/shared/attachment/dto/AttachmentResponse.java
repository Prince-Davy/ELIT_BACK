package com.elit.outildedecision.shared.attachment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * Response payload describing a file attachment linked to a project.
 * Returned after an upload or when retrieving the details of a project's files.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AttachmentResponse {

  /**
   * A unique identifier for the attachment.
   */
  private Long attachmentId;

  /**
   * The original name of the uploaded file.
   */
  private String fileName;

  /**
   * The path where the file is stored on the server.
   */
  private String filePath;

  /**
   * The MIME content type of the file.
   */
  private String contentType;

  /**
   * Date of creation.
   */
  private Timestamp createdAt;

  /**
   * Date of update.
   */
  private Timestamp updatedAt;

  /**
   * Last name of the creator.
   */
  private String creatorLastName;

  /**
   * First name of the creator.
   */
  private String creatorFirstName;
}
