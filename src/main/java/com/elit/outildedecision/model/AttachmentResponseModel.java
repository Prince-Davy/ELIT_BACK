package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an attachment response model, containing information about an attachment.
 * It is used to provide a structured response after a file has been uploaded or when retrieving file details.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponseModel {

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
}
