package com.elit.outildedecision.shared.attachment.controller;

import com.elit.outildedecision.shared.attachment.service.AttachmentService;
import com.elit.outildedecision.shared.attachment.exception.AttachmentNotFoundException;
import com.elit.outildedecision.shared.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Endpoints managing the attachments of a project ({@code /user/attachments}).
 */
@RestController
@RequestMapping("/user/attachments")
@Tag(name = "Shared - Attachments", description = "Gestion des pièces jointes d'un projet")
public class AttachmentController {
  /**
   * The Attachment service.
   */
  private final AttachmentService attachmentService;

  /**
   * Instantiates a new Attachment controller.
   *
   * @param attachmentService the attachment service
   */
  public AttachmentController(final AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  /**
   * Uploads attachments of a project.
   *
   * @param projectId the project id
   * @param files     the files
   * @return the upload confirmation message
   * @throws ProjectNotFoundException the project not found exception
   * @throws IOException              the io exception
   * @throws FileCouldNotBeSaved      the file could not be saved
   */
  @PostMapping(
      value = "/upload/{projectId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Object> uploadAttachment(@PathVariable final Long projectId,
                                                  @RequestParam("files") final List<MultipartFile> files)
      throws ProjectNotFoundException, IOException, FileCouldNotBeSaved {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails user = (UserDetails) auth.getPrincipal();
    return ResponseEntity.ok(attachmentService.uploadAttachment(files, projectId, user.getUsername()));
  }

  /**
   * Renames an attachment of a project.
   *
   * @param attachmentId      the attachment id
   * @param newAttachmentName the new attachment name
   * @return the rename confirmation message
   * @throws AttachmentNotFoundException the attachment not found exception
   * @throws IOException                 the io exception
   */
  @PutMapping("/edit/{attachmentId}")
  public ResponseEntity<String> editAttachment(
      @PathVariable final Long attachmentId,
      final HttpEntity<String> newAttachmentName)
      throws AttachmentNotFoundException, IOException {
    return ResponseEntity.ok(attachmentService.renameAttachment(attachmentId, newAttachmentName));
  }

  /**
   * Downloads an attachment of a project based on its id.
   *
   * @param attachmentId the attachment id
   * @param response     the response
   * @throws AttachmentNotFoundException the attachment not found exception
   * @throws IOException                 the io exception
   */
  // S6856: {projectId} is part of the frozen URL contract (front-end calls it) but the handler
  // resolves everything from {attachmentId}; binding an unused parameter would add noise instead.
  @SuppressWarnings("java:S6856")
  @GetMapping("/download/{projectId}/{attachmentId}")
  public void downloadAttachment(
      @PathVariable("attachmentId") final Long attachmentId, final HttpServletResponse response)
      throws IOException, AttachmentNotFoundException {
    attachmentService.downloadAttachment(attachmentId, response);
  }

  /**
   * Deletes an attachment of a project based on its id.
   *
   * @param attachmentId the attachment id
   * @param projectId    the id of project
   * @return the deletion confirmation message
   * @throws AttachmentNotFoundException the attachment not found exception
   * @throws ProjectNotFoundException    the project not found exception
   */
  @DeleteMapping("/{attachmentId}/{projectId}")
  @PreAuthorize("hasAnyAuthority('USER') and isProjectOwner(#projectId)")
  public ResponseEntity<String> deleteAttachmentById(
      @PathVariable("attachmentId") final Long attachmentId, @PathVariable("projectId") final Long projectId)
      throws AttachmentNotFoundException, ProjectNotFoundException {
    return ResponseEntity.ok(attachmentService.deleteAttachment(attachmentId));
  }

  /**
   * Previews an attachment of a project (inline) based on its id.
   *
   * @param attachmentId the attachment id
   * @param response     the response
   * @param request      the request
   * @throws AttachmentNotFoundException the attachment not found exception
   * @throws IOException                 the io exception
   */
  // S6856: {projectId} is part of the frozen URL contract (front-end calls it) but the handler
  // resolves everything from {attachmentId}; binding an unused parameter would add noise instead.
  @SuppressWarnings("java:S6856")
  @GetMapping("/preview/{projectId}/{attachmentId}")
  public void previewAttachmentWithoutDownload(
      final HttpServletRequest request,
      final HttpServletResponse response,
      @PathVariable("attachmentId") final Long attachmentId)
      throws AttachmentNotFoundException, IOException {
    attachmentService.openAttachment(attachmentId, response);
  }
}
