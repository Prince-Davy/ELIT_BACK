package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.repository.AttachmentRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.implementation.AttachmentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class AttachementServiceImplTests {

  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private AttachmentRepository attachmentRepository;
  @Mock private HttpServletResponse response;
  @InjectMocks
  private AttachmentServiceImpl attachmentService;

  private List<MultipartFile> files;
  private Long projectId;

  @BeforeEach
  public void setup() {
    files = new ArrayList<>();
    projectId = 1L;
  }

  @Test
  void testDeleteAttachmentNotFound() throws ProjectNotFoundException {
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
  void testRenameAttachmentAttachmentNotFound() throws Exception {

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

  @Test
  void testDeleteAllAttachmentsOfProject() throws Exception {
    Long projectId = 1L;
    Project project = new Project();
    project.setProjectId(projectId);
    Attachment att1 = new Attachment();
    att1.setAttachmentId(10L);
    att1.setFileName("f1.txt");
    att1.setFilePath("/tmp/f1.txt");
    att1.setProject(project);
    Attachment att2 = new Attachment();
    att2.setAttachmentId(11L);
    att2.setFileName("f2.txt");
    att2.setFilePath("/tmp/f2.txt");
    att2.setProject(project);
    List<Attachment> attachments = Arrays.asList(att1, att2);

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(attachmentRepository.findAllByProject(project)).thenReturn(attachments);
    // Simule la suppression physique OK
    try (var fileUtilsMock = mockStatic(org.springframework.util.FileSystemUtils.class)) {
      fileUtilsMock.when(() -> org.springframework.util.FileSystemUtils.deleteRecursively(any(File.class))).thenReturn(true);
      attachmentService.deleteAllAttachmentsOfProject(projectId);
    }

    attachmentService.deleteAllAttachmentsOfProject(projectId);

    verify(attachmentRepository).deleteById(10L);
    verify(attachmentRepository).deleteById(11L);
  }

  @Test
  void testUploadAttachmentProjectNotFound() {
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    assertThrows(ProjectNotFoundException.class, () -> attachmentService.uploadAttachment(files, projectId, "user@x.fr"));
  }

  @Test
  void testUploadAttachmentFileCouldNotBeSaved() throws Exception {
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("/tmp");
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("fail.txt");
    when(file.getInputStream()).thenThrow(new IOException("fail"));
    files = List.of(file);
    assertThrows(FileCouldNotBeSaved.class, () -> attachmentService.uploadAttachment(files, projectId, "user@x.fr"));
  }

  @Test
  void testDeleteAttachmentFileNotDeleted() throws Exception {
    Attachment attachment = new Attachment();
    attachment.setAttachmentId(1L);
    attachment.setFilePath("/tmp/fail.txt");
    when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
    try (var fileUtilsMock = mockStatic(FileSystemUtils.class)) {
      fileUtilsMock.when(() -> FileSystemUtils.deleteRecursively(any(File.class))).thenReturn(false);
      ResponseEntity<String> resp = attachmentService.deleteAttachment(1L);
      assertEquals("Erreur lors de la suppression du fichier.", resp.getBody());
    }
  }

  @Test
  void testDeleteAllAttachmentsOfProject_PhysicalDeleteFails() throws Exception {
    Long projectId = 2L;
    Project project = new Project();
    project.setProjectId(projectId);
    Attachment att = new Attachment();
    att.setAttachmentId(20L);
    att.setFileName("fail.txt");
    att.setFilePath("/tmp/fail.txt");
    att.setProject(project);
    List<Attachment> attachments = List.of(att);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(attachmentRepository.findAllByProject(project)).thenReturn(attachments);
    try (var fileUtilsMock = mockStatic(FileSystemUtils.class)) {
      fileUtilsMock.when(() -> FileSystemUtils.deleteRecursively(any(File.class))).thenReturn(false);
      attachmentService.deleteAllAttachmentsOfProject(projectId);
      // Pas d'exception, mais logger.error appelé (non vérifiable ici sans capturer le logger)
    }
  }

  @Test
  void testDeleteAllAttachmentsOfProject_AttachmentNotFound() {
    Long projectId = 3L;
    Project project = new Project();
    project.setProjectId(projectId);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(attachmentRepository.findAllByProject(project)).thenReturn(List.of());
    assertThrows(AttachmentNotFoundException.class, () -> attachmentService.deleteAllAttachmentsOfProject(projectId));
  }
}
