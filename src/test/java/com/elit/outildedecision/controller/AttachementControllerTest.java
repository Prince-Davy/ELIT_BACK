package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.repository.AttachmentRepository;
import com.elit.outildedecision.service.implementation.AttachmentServiceImpl2;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = AttachementController.class)
@ExtendWith(MockitoExtension.class)
class AttachementControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private AttachmentServiceImpl2 attachmentService;

  @MockBean
  private UserServiceImpl userService;
  @MockBean
  AttachmentRepository attachmentRepository;
  @InjectMocks
  AttachementController attachementController;

  private List<MultipartFile> files;
  private Long projectId;
  private Long attachmentId;

  @BeforeEach
  public void setup() {
    MockMultipartFile fileMultipart =
      new MockMultipartFile(
        "files",
        "test contract.pdf",
        MediaType.APPLICATION_PDF_VALUE,
        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));
    files = new ArrayList<>();
    projectId = 1L;
    attachmentId = 1L;
    files.add(fileMultipart);
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void uploadAttachment_whenFile_isDownloaded_Successfully() throws Exception {

    when(attachmentService.uploadAttachment(files, projectId)).thenReturn(ResponseEntity.ok().body("Fichiers enregistrés avec succés"));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/attachments/upload/"+ projectId)
      .file((MockMultipartFile) files.get(0))).andDo(print()).andExpect(status().is(200));
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void uploadAttachment_whenFile_isNotDownloaded() throws Exception {
    when(attachmentService.uploadAttachment(files, projectId)).thenReturn(ResponseEntity.badRequest().body("Erreur lors de l'enregistrement du fichier : " + files.get(0).getOriginalFilename()));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/attachments/upload/"+ projectId)
      .file((MockMultipartFile) files.get(0))).andDo(print()).andExpect(status().is(400));
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void editAttachment_whenRenameFile_isOk() throws Exception {
    HttpEntity<String> newAttachmentName = new HttpEntity<>("newName");
    when(attachmentService.renameAttachment(attachmentId , newAttachmentName)).thenReturn(ResponseEntity.ok().body("Votre fichier a été mis à jour"));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put("/api/user/attachments/edit/"+ attachmentId).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(newAttachmentName))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void editAttachment_whenRenameFile_isNotOk() throws Exception {
    HttpEntity<String> newAttachmentName = new HttpEntity<>("");
    when(attachmentRepository.findById(2L)).thenReturn(Optional.empty());
    when(attachmentService.renameAttachment(2L , newAttachmentName)).thenReturn(ResponseEntity.badRequest().body("Ce fichier n'existe pas !"));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put("/api/user/attachments/edit/"+ 2L).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(newAttachmentName))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
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
