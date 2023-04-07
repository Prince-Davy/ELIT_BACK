
package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * This interface represents a service that handles attachment-related operations for a project.
 */
public interface AttachmentService {

  /**
   * Deletes the attachment with the given ID.
   *
   * @param attachmentId the ID of the attachment to delete
   * @throws AttachmentNotFoundException if the attachment with the given ID does not exist
   * @throws ProjectNotFoundException if the project that the attachment belongs to does not exist
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> deleteAttachment(Long attachmentId)
    throws AttachmentNotFoundException, ProjectNotFoundException;

  /**
   * Uploads one or more attachments to the project with the given ID.
   *
   * @param files the files to upload
   * @param projectId the ID of the project to upload the attachments to
   * @throws IOException if there is an error reading the files
   * @throws ProjectNotFoundException if the project with the given ID does not exist
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<Object> uploadAttachment(List<MultipartFile> files, Long projectId)
          throws IOException, ProjectNotFoundException, FileCouldNotBeSaved;

  /**
   * Renames the attachment with the given ID.
   *
   * @param projectId the ID of the project that the attachment belongs to
   * @param newAttachmentName the new name for the attachment, provided as an HttpEntity
   * @throws AttachmentNotFoundException if the attachment with the given ID does not exist
   * @throws IOException if there is an error renaming the attachment
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> renameAttachment(Long projectId, HttpEntity<String> newAttachmentName)
    throws AttachmentNotFoundException, IOException;

  /**
   * Opens the attachment with the given ID in the response.
   *
   * @param attachmentId the ID of the attachment to open
   * @param response the HttpServletResponse to write the attachment to
   * @throws AttachmentNotFoundException if the attachment with the given ID does not exist
   * @throws IOException if there is an error opening the attachment
   */
  void openAttachment(Long attachmentId, HttpServletResponse response)
    throws AttachmentNotFoundException, IOException;

  /**
   * Downloads the attachment with the given ID as an attachment in the response.
   *
   * @param attachmentId the ID of the attachment to download
   * @param response the HttpServletResponse to write the attachment to
   * @throws AttachmentNotFoundException if the attachment with the given ID does not exist
   * @throws IOException if there is an error downloading the attachment
   */
  void downloadAttachment(Long attachmentId, HttpServletResponse response)
    throws AttachmentNotFoundException, IOException;
}

