package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.repository.AttachmentRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.implementation.AttachmentServiceImpl2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachementServiceImplTests {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private AttachmentRepository attachmentRepository;

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
}
