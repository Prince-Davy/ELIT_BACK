
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.service.AttachmentService;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user/attachments")
public class AttachementController {
  /** The Attachment service. */
  private final AttachmentService attachmentService;

  /**
   * Instantiates a new Attachement controller.
   * dependency injection via constructor of the attachment service
   * @param attachmentService the attachment service
   */
  public AttachementController(final AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  /**
   * this method is used to upload attachments of a project.
   *
   * @param projectId the project id
   * @param files the files
   * @return the response entity
   * @throws ProjectNotFoundException the project not found exception
   * @throws IOException the io exception
   * @throws FileCouldNotBeSaved the file could not be saved
   */
  @PostMapping(
      value = "/upload/{projectId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Object> uploadAttachment(@PathVariable final Long projectId,
                                                 @RequestParam("files") final List<MultipartFile> files)
    throws ProjectNotFoundException, IOException, FileCouldNotBeSaved {
    return attachmentService.uploadAttachment(files, projectId);
  }
  /**
   * this method is used to rename attachments of a project.
   *
   * @param attachmentId the attachment id
   * @param newAttachmentName the new attachment name
   * @return the response entity
   * @throws AttachmentNotFoundException the attachment not found exception
   * @throws IOException the io exception
   */
  @PutMapping("/edit/{attachmentId}")
  public ResponseEntity<String> editAttachment(
      @PathVariable final Long attachmentId,
      final HttpEntity<String> newAttachmentName)
      throws AttachmentNotFoundException, IOException {
    return attachmentService.renameAttachment(attachmentId, newAttachmentName);
  }
  /**
   * this method is used to download an attachment of a project based on its id.
   *
   * @param attachmentId the attachment id
   * @param response the response
   * @throws AttachmentNotFoundException the attachment not found exception
   * @throws IOException the io exception
   */
  @GetMapping("/download/{projectId}/{attachmentId}")
  public void downloadAttachment(
      @PathVariable("attachmentId") final Long attachmentId, final HttpServletResponse response)
      throws  IOException, AttachmentNotFoundException {
    attachmentService.downloadAttachment(attachmentId, response);
  }
  /**
   * this method is used to delete an attachment of a project based on its id.
   *
   * @param attachmentId the attachment id
   * @return the response entity
   * @throws AttachmentNotFoundException the attachment not found exception
   * @throws ProjectNotFoundException the project not found exception
   */
  @DeleteMapping("/{attachmentId}")
  public ResponseEntity<String> deleteAttachmentById(@PathVariable("attachmentId") final Long attachmentId)
    throws AttachmentNotFoundException,
    ProjectNotFoundException {
    return attachmentService.deleteAttachment(attachmentId);
  }
  /**
   * this method is used to preview an attachment of a project based on its id.
   *
   * @param attachmentId the attachment id
   * @param response the response
   * @param request the request
   * @throws AttachmentNotFoundException the attachment not found exception
   * @throws IOException the io exception
   */
  @GetMapping("/preview/{projectId}/{attachmentId}")
  public void previewAttachmentWithoutDownload(
      final HttpServletRequest request,
      final HttpServletResponse response,
      @PathVariable("attachmentId") final Long attachmentId)
      throws AttachmentNotFoundException, IOException {
    attachmentService.openAttachment(attachmentId, response);
  }
}
