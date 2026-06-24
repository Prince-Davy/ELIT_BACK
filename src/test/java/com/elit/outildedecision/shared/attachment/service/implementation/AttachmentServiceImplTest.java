package com.elit.outildedecision.shared.attachment.service.implementation;

import com.elit.outildedecision.shared.attachment.entity.Attachment;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.attachment.exception.AttachmentNotFoundException;
import com.elit.outildedecision.shared.attachment.exception.InvalidAttachmentException;
import com.elit.outildedecision.shared.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.attachment.repository.AttachmentContentRepository;
import com.elit.outildedecision.shared.attachment.repository.AttachmentRepository;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {

  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private AttachmentRepository attachmentRepository;
  @Mock
  private AttachmentContentRepository attachmentContentRepository;
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
    Long localProjectId = 1L;
    Project project = new Project();
    project.setProjectId(localProjectId);
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

    when(projectRepository.findById(localProjectId)).thenReturn(Optional.of(project));
    when(attachmentRepository.findAllByProject(project)).thenReturn(attachments);

    attachmentService.deleteAllAttachmentsOfProject(localProjectId);

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
    when(file.getBytes()).thenThrow(new IOException("fail"));
    files = List.of(file);
    assertThrows(FileCouldNotBeSaved.class, () -> attachmentService.uploadAttachment(files, projectId, "user@x.fr"));
  }

  @Test
  void testUploadAttachmentForbiddenExtension() throws Exception {
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("/tmp");
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("malware.exe");
    files = List.of(file);
    assertThrows(InvalidAttachmentException.class,
        () -> attachmentService.uploadAttachment(files, projectId, "user@x.fr"));
    verify(attachmentRepository, never()).save(any());
  }

  @Test
  void testUploadAttachmentTooLarge() throws Exception {
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("/tmp");
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    MultipartFile file = mock(MultipartFile.class);
    when(file.getSize()).thenReturn(10L * 1024 * 1024 + 1);
    files = List.of(file);
    assertThrows(InvalidAttachmentException.class,
        () -> attachmentService.uploadAttachment(files, projectId, "user@x.fr"));
    verify(attachmentRepository, never()).save(any());
  }

  @Test
  void testDeleteAttachment_Success() throws Exception {
    Attachment attachment = new Attachment();
    attachment.setAttachmentId(1L);
    attachment.setFilePath("/tmp/ok.txt");
    when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));

    String result = attachmentService.deleteAttachment(1L);

    assertEquals("Fichier supprimé avec succès.", result);
    verify(attachmentRepository).deleteById(1L);
  }

  @Test
  void testDeleteAllAttachmentsOfProject_AttachmentNotFound() {
    Long localProjectId = 3L;
    Project project = new Project();
    project.setProjectId(localProjectId);
    when(projectRepository.findById(localProjectId)).thenReturn(Optional.of(project));
    when(attachmentRepository.findAllByProject(project)).thenReturn(List.of());
    assertThrows(AttachmentNotFoundException.class, () -> attachmentService.deleteAllAttachmentsOfProject(localProjectId));
  }
}
