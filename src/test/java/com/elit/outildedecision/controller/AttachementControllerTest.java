package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.repository.AttachmentRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.AttachmentService;
import com.elit.outildedecision.service.implementation.AttachmentServiceImpl2;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AttachementControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @InjectMocks
  private AttachmentServiceImpl2 attachmentService;
  @MockBean
  AttachmentRepository attachmentRepository;
  @MockBean
  ProjectRepository  projectRepository;

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private List<MultipartFile> files;

  private Long projectId;

  @BeforeEach
  public void setup() {
    files = new ArrayList<>();
    projectId = 1L;
    Attachment attachment = new Attachment();
  }
  @Test
  void uploadAttachment() throws Exception {
    Project proj = new Project();
    proj.setProjectId(projectId);
    proj.setName("name projet");
    projectRepository.save(proj);

    when(attachmentService.uploadAttachment(files, projectId)).thenReturn(ResponseEntity.ok().body("Fichiers enregistrés avec succés"));
    MockHttpServletResponse response = mockMvc.perform(post("/api/user/attachments/upload/{projectId}").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(files.get(0)))).andReturn().getResponse();
    Assertions.assertEquals(201, response.getStatus());
  }

  @Test
  void editAttachment() {
  }

  @Test
  void downloadAttachment() {
  }

  @Test
  void deleteAttachmentById() {
  }

  @Test
  void previewAttachmentWithoutDownload() {
  }
}
