package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.entity.Project;

import com.elit.outildedecision.service.implementation.AttachmentServiceImpl2;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(controllers = AttachementController.class)
@ExtendWith(MockitoExtension.class)
class AttachementControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private AttachmentServiceImpl2 attachmentService;

  @InjectMocks
  AttachementController attachementController;



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

  }
  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void uploadAttachment() throws Exception {

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
