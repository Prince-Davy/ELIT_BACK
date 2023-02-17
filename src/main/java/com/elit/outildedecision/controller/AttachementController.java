package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.AttachmentNotFoundException;
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
<<<<<<< HEAD
public class AttachementController {
  private final AttachmentService attachmentService;

  public AttachementController(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @PostMapping(
      value = "/upload/{projectId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Object> uploadAttachment(
      @PathVariable Long projectId, @RequestParam("files") List<MultipartFile> files)
      throws ProjectNotFoundException, IOException {
    return attachmentService.uploadAttachment(files, projectId);
  }

  @PutMapping("/edit/{attachmentId}")
  public ResponseEntity<String> editAttachment(
      @PathVariable Long attachmentId, HttpEntity<String> newAttachmentName)
      throws AttachmentNotFoundException, IOException {
    return attachmentService.renameAttachment(attachmentId, newAttachmentName);
  }
=======
public class AttachementController{
  private final AttachmentService attachmentService;

  public AttachementController(AttachmentService attachmentService){
    this.attachmentService = attachmentService;
  }
  @PostMapping(value = "/upload/{projectId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Object> uploadAttachment(@PathVariable Long projectId,
                                                 @RequestParam("files") List<MultipartFile> files)
                                                  throws ProjectNotFoundException, IOException{
    return attachmentService.uploadAttachment(files, projectId);
}

  @PutMapping("/edit/{attachmentId}")
  public ResponseEntity<String> editAttachment(@PathVariable Long attachmentId,
            HttpEntity<String> newAttachmentName) throws AttachmentNotFoundException, IOException {
    return attachmentService.renameAttachment(attachmentId, newAttachmentName);
    }
>>>>>>> 0292700ea2129c507e71508049e07804b2a532f3

  @GetMapping("/download/{projectId}/{attachmentId}")
  public void downloadAttachment(
      @PathVariable("attachmentId") Long attachmentId, HttpServletResponse response)
      throws ProjectNotFoundException, IOException, AttachmentNotFoundException {
    attachmentService.downloadAttachment(attachmentId, response);
  }

  @DeleteMapping("/{attachmentId}")
  public ResponseEntity deleteAttachmentById(@PathVariable("attachmentId") Long attachmentId)
      throws IOException, AttachmentNotFoundException, ProjectNotFoundException {
    return attachmentService.deleteAttachment(attachmentId);
  }

  @RequestMapping("/preview/{projectId}/{attachmentId}")
  public void previewAttachmentWithoutDownload(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("attachmentId") Long attachmentId)
      throws AttachmentNotFoundException, IOException {
    attachmentService.openAttachment(attachmentId, response);
  }

  @RequestMapping("/download/{projectId}/{attachmentId}")
  // @PreAuthorize("isContributer(#projectId)")
  public void downloadAttachment(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("attachmentId") Long attachmentId)
      throws AttachmentNotFoundException, IOException {
    attachmentService.downloadAttachment(attachmentId, response);
  }
}
