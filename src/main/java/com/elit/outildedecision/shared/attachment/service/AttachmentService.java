package com.elit.outildedecision.shared.attachment.service;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.attachment.exception.AttachmentNotFoundException;
import com.elit.outildedecision.shared.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Contract for managing the attachments of a project: upload, rename, download, preview
 * and deletion. The service exposes plain results and never HTTP envelopes; the controller
 * and the global handler are responsible for the HTTP status and error bodies.
 */
public interface AttachmentService {

  /**
   * Uploads the given files and links them to the project.
   *
   * @param files     the files to upload
   * @param projectId the project the files are attached to
   * @param userEmail the email of the uploading user
   * @return the success confirmation message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws FileCouldNotBeSaved      if a file could not be written to disk
   */
  String uploadAttachment(List<MultipartFile> files, Long projectId, String userEmail)
      throws ProjectNotFoundException, FileCouldNotBeSaved;

  /**
   * Deletes a single attachment and its file on disk.
   *
   * @param attachmentId the attachment to delete
   * @return the success confirmation message
   * @throws AttachmentNotFoundException if the attachment does not exist
   */
  String deleteAttachment(Long attachmentId) throws AttachmentNotFoundException;

  /**
   * Deletes every attachment of the given project (by id).
   *
   * @param projectId the project whose attachments are deleted
   * @throws ProjectNotFoundException    if the project does not exist
   * @throws AttachmentNotFoundException if the project has no attachment
   */
  void deleteAllAttachmentsOfProject(Long projectId) throws ProjectNotFoundException, AttachmentNotFoundException;

  /**
   * Streams an attachment inline (browser preview).
   *
   * @param attachmentId the attachment to open
   * @param response     the servlet response to write the bytes into
   * @throws AttachmentNotFoundException if the attachment does not exist
   * @throws IOException                 if the file cannot be read
   */
  void openAttachment(Long attachmentId, HttpServletResponse response)
      throws AttachmentNotFoundException, IOException;

  /**
   * Streams an attachment as a download.
   *
   * @param attachmentId the attachment to download
   * @param response     the servlet response to write the bytes into
   * @throws AttachmentNotFoundException if the attachment does not exist
   * @throws IOException                 if the file cannot be read
   */
  void downloadAttachment(Long attachmentId, HttpServletResponse response)
      throws AttachmentNotFoundException, IOException;

  /**
   * Deletes every attachment of the given project entity.
   *
   * @param project the project whose attachments are deleted
   * @throws AttachmentNotFoundException if the project has no attachment
   */
  void deleteAttachments(Project project) throws AttachmentNotFoundException;

  /**
   * Renames an attachment (file and database record).
   *
   * @param attachmentId       the attachment to rename
   * @param attachmentNameJson the request body holding the new name
   * @return the success confirmation message
   * @throws AttachmentNotFoundException if the attachment does not exist
   * @throws IOException                 if the file cannot be moved
   */
  String renameAttachment(Long attachmentId, HttpEntity<String> attachmentNameJson)
      throws AttachmentNotFoundException, IOException;
}
