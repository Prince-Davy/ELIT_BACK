package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;

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

  /**
   * Returns the id of the attachment.
   *
   * @return The id
   */
  public Long getAttachmentId() {
    return attachmentId;
  }

  /**
   * set attachment id.
   *
   * @param attachmentId id
   */
  public void setAttachmentId(final Long attachmentId) {
    this.attachmentId = attachmentId;
  }

  /**
   * Get file name.
   *
   * @return fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * set file name.
   *
   * @param fileName fileName
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }

  /**
   * Get file path.
   *
   * @return filePath
   */
  public String getFilePath() {
    return filePath;
  }

  /**
   * set file path.
   *
   * @param filePath filePath
   */
  public void setFilePath(final String filePath) {
    this.filePath = filePath;
  }

  /**
   * Get content type.
   *
   * @return content type
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * set content type.
   *
   * @param contentType content type
   */
  public void setContentType(final String contentType) {
    this.contentType = contentType;
  }

  /**
   * get create date.
   *
   * @return createdAt
   */
  public Timestamp getCreatedAt() {
    return createdAt;
  }

  /**
   * set created date.
   *
   * @param createdAt created date
   */
  public void setCreatedAt(final Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * get updated date.
   *
   * @return updatedAt
   */
  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  /**
   * set update date.
   *
   * @param updatedAt update date
   */
  public void setUpdatedAt(final Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }

  /**
   * get create last name.
   *
   * @return lastName
   */
  public String getCreatorLastName() {
    return creatorLastName;
  }

  /**
   * set create last name.
   *
   * @param creatorLastName lastName
   */
  public void setCreatorLastName(final String creatorLastName) {
    this.creatorLastName = creatorLastName;
  }

  /**
   * get create first name.
   *
   * @return firstName
   */
  public String getCreatorFirstName() {
    return creatorFirstName;
  }

  /**
   * create first name.
   *
   * @param creatorFirstName firstName
   */
  public void setCreatorFirstName(final String creatorFirstName) {
    this.creatorFirstName = creatorFirstName;
  }
}
