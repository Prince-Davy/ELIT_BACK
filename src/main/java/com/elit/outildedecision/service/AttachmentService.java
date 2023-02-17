package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;

import com.elit.outildedecision.model.AttachmentModel;
import org.springframework.http.HttpEntity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface AttachmentService {

  ResponseEntity deleteAttachment(Long attachmentId)
      throws AttachmentNotFoundException, ProjectNotFoundException;

  ResponseEntity<Object> uploadAttachment(List<MultipartFile> files, Long projectId)
      throws IOException, ProjectNotFoundException;

  ResponseEntity<String> addAttachmentToProject(Long projectId, AttachmentModel files)
      throws ProjectNotFoundException;

  ResponseEntity<String> renameAttachment(Long projectId, HttpEntity<String> newAttachmentName)
      throws AttachmentNotFoundException, IOException;

  void openAttachment(Long attachmentId, HttpServletResponse response)
      throws AttachmentNotFoundException, IOException;

  void downloadAttachment(Long attachmentId, HttpServletResponse response)
      throws AttachmentNotFoundException, IOException;
}
