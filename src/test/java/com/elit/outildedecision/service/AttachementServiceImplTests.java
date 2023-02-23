package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.repository.AttachmentRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.implementation.AttachmentServiceImpl2;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachementServiceImplTests {

  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private AttachmentRepository attachmentRepository;
  @Mock private HttpServletResponse response;
  @InjectMocks
  private AttachmentServiceImpl2 attachmentService;

  private List<MultipartFile> files;
  private Long projectId;

  @BeforeEach
  public void setup() {
    files = new ArrayList<>();
    projectId = 1L;
  }

  @Test
  public void testUploadAttachmentProjectNotFound() {
    when(projectRepository.findById(projectId)).thenReturn(java.util.Optional.empty());

    try {
      attachmentService.uploadAttachment(files, projectId);
    } catch (ProjectNotFoundException e) {
      assertEquals("Ce Projet n'existe pas !", e.getMessage());
    }
  }

  @Test
  public void testDeleteAttachmentNotFound() throws ProjectNotFoundException {
    when(attachmentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    try {
      attachmentService.deleteAttachment(1L);
    } catch (AttachmentNotFoundException e) {
      assertEquals("Ce fichier n'existe pas!", e.getMessage());
    }
  }

  @Test
  void testOpenAttachment_fileDoesNotExist() throws Exception {
    // Arrange
    Long attachmentId = 1L;
    Attachment attachment = new Attachment();
    attachment.setAttachmentId(attachmentId);
    attachment.setFileName("test.txt");
    attachment.setFilePath("C:/files/test.txt");
    when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));

    // Act & Assert
    try {
      attachmentService.openAttachment(1L,response);
    } catch (AttachmentNotFoundException e) {
      assertEquals("Ce fichier n'existe pas!", e.getMessage());
    }
  }

  @Test
  public void testRenameAttachmentAttachmentNotFound() throws Exception {

    when(attachmentRepository.findById(1L)).thenReturn(Optional.empty());

    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("newAttachmentName", "newFileName.txt");
    String requestBodyJson = new ObjectMapper().writeValueAsString(requestBody);
    HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson);

    assertThrows(
      AttachmentNotFoundException.class,
      () -> attachmentService.renameAttachment(1L, requestEntity));

    verify(attachmentRepository, never()).save(any());
  }

}
