package com.elit.outildedecision.shared.attachment.controller;

import com.elit.outildedecision.shared.attachment.entity.Attachment;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.attachment.exception.AttachmentNotFoundException;
import com.elit.outildedecision.shared.attachment.repository.AttachmentRepository;
import com.elit.outildedecision.shared.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.shared.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.shared.attachment.service.implementation.AttachmentServiceImpl;
import com.elit.outildedecision.shared.user.service.implementation.UserServiceImpl;
import com.elit.outildedecision.shared.util.ProjectPermession;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttachmentControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private AttachmentServiceImpl attachmentService;
  @MockitoBean
  private UserServiceImpl userService;
  @MockitoBean
  AttachmentRepository attachmentRepository;
  @Mock
  CustomMethodSecurityExpressionRoot customMethodSecurity;
  @InjectMocks
  AttachmentController attachementController;

  private List<MultipartFile> files;
  private Long projectId;
  private Long attachmentId;
  private User user;
  private Project project;
  private ProjectUserPermission permission;

  @BeforeEach
  public void setup() {
    MockMultipartFile fileMultipart =
      new MockMultipartFile(
        "files",
        "test contract.pdf",
        MediaType.APPLICATION_PDF_VALUE,
        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));
    files = new ArrayList<>();
    projectId = 2L;
    attachmentId = 1L;
    files.add(fileMultipart);

    user = new User();
    user.setUserEmail("user@user.com");
    user.setUsername("user");
    user.setUserId(3L);
    project = new Project();
    project.setProjectId(projectId);
    project.setChefId(user.getUserId());
    permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(ProjectPermession.CDP.name());
    user.setUserProjectPermissions(List.of(permission));
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

    when(attachmentService.uploadAttachment(anyList(), anyLong(), anyString())).thenReturn("Fichiers enregistrés avec succés");
    mockMvc.perform(MockMvcRequestBuilders.multipart("/user/attachments/upload/"+ projectId)
      .file((MockMultipartFile) files.get(0))).andDo(print()).andExpect(status().is(200));
  }

  @WithMockUser(authorities = {"USER", "ADMIN"},username = "userEmail")
  @Test
  void uploadAttachment_whenFile_isNotDownloaded() throws Exception {
    when(attachmentService.uploadAttachment(anyList(), anyLong(), anyString()))
      .thenThrow(new FileCouldNotBeSaved("Erreur lors de l'enregistrement du fichier : " + files.get(0).getOriginalFilename()));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/user/attachments/upload/"+ projectId)
      .file((MockMultipartFile) files.get(0))).andDo(print()).andExpect(status().is(500));
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void editAttachment_whenRenameFile_isOk() throws Exception {
    HttpEntity<String> newAttachmentName = new HttpEntity<>("newName");
    when(attachmentService.renameAttachment(attachmentId , newAttachmentName)).thenReturn("Votre fichier a été mis à jour");
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put("/user/attachments/edit/"+ attachmentId).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(newAttachmentName))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void editAttachment_whenRenameFile_isNotOk() throws Exception {
    HttpEntity<String> newAttachmentName = new HttpEntity<>("");
    when(attachmentRepository.findById(2L)).thenReturn(Optional.empty());
    when(attachmentService.renameAttachment(2L , newAttachmentName)).thenReturn("Ce fichier n'existe pas !");
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put("/user/attachments/edit/"+ 2L).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(newAttachmentName))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void downloadAttachment_whenAttachmentAndFile_exists() throws Exception {
    HttpServletResponse httpServletResponse = new MockHttpServletResponse();
    Attachment attachment = new Attachment();
    attachment.setAttachmentId(attachmentId);
    when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/user/attachments/download/"+projectId+"/"+ attachmentId)
      .accept(MediaType.APPLICATION_JSON).content(String.valueOf(httpServletResponse))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void downloadAttachment_whenAttachment_doesNotExists() throws Exception {
    HttpServletResponse httpServletResponse = new MockHttpServletResponse();
    Attachment attachment = new Attachment();
    attachment.setAttachmentId(3L);
 when(attachmentRepository.findById(attachment.getAttachmentId())).thenReturn(Optional.empty()).thenThrow();
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/user/attachments/download/"+projectId+"/"+3L)
      .accept(MediaType.APPLICATION_JSON).content(String.valueOf(httpServletResponse))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void deleteAttachmentById_whenAttachment_exists() throws Exception {
    Attachment attachment = new Attachment();
    attachment.setAttachmentId(attachmentId);
    List<Attachment> attachments = new ArrayList<>();
    attachments.add(attachment);
    project.setAttachments(attachments);

    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(userService.getProjectPermissions(user.getUsername(), project.getProjectId()))
      .thenReturn(List.of(ProjectPermession.CDP.name()));
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(attachmentService.deleteAttachment(attachmentId)).thenReturn("Fichier supprimé avec succès.");

    ResultActions response = mockMvc.perform(delete("/user/attachments/"+ attachmentId +"/"+project.getProjectId()));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }
  @WithMockUser(authorities = {"USER"})
  @Test
  void deleteAttachmentById_whenAttachment_doesNotExist() throws Exception {
    Attachment attachment = new Attachment();
    attachment.setAttachmentId(attachmentId);
    List<Attachment> attachments = new ArrayList<>();
    attachments.add(attachment);
    project.setAttachments(attachments);

    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(userService.getProjectPermissions(user.getUsername(), project.getProjectId()))
      .thenReturn(List.of(ProjectPermession.CDP.name()));
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(attachmentService.deleteAttachment(attachmentId))
      .thenThrow(new AttachmentNotFoundException("Ce fichier n'existe pas!"));

    ResultActions response = mockMvc.perform(delete("/user/attachments/"+ attachmentId+"/"+project.getProjectId()));
    response.andExpect(status().isNotFound())
      .andDo(print());
    Assertions.assertEquals(404, response.andReturn().getResponse().getStatus());
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void previewAttachmentWithoutDownload_whenAttachment_exists() throws Exception {
    HttpServletResponse httpServletResponse = new MockHttpServletResponse();
    willDoNothing().given(attachmentService).openAttachment(attachmentId,httpServletResponse);
    ResultActions response = mockMvc.perform(get("/user/attachments/preview/"+ projectId + "/"+ attachmentId));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }

  @Test
  void previewAttachmentWithoutDownload_whenAttachment_doseNotexists() throws Exception {
    when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());
    ResultActions response = mockMvc.perform(get("/user/attachments/preview/"+ projectId + "/"+ attachmentId));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }
}
