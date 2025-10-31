package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Attachment;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.repository.AttachmentRepository;
import com.elit.outildedecision.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.service.implementation.AttachmentServiceImpl;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.ProjectPermession;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttachementControllerTests {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private AttachmentServiceImpl attachmentService;

  @MockBean
  private UserServiceImpl userService;
  @MockBean
  AttachmentRepository attachmentRepository;
  @Mock
  CustomMethodSecurityExpressionRoot customMethodSecurity;
  @InjectMocks
  AttachementController attachementController;

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

//    MockitoAnnotations.initMocks(this);
//    mockMvc = standaloneSetup(attachementController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

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

    when(attachmentService.uploadAttachment(files, projectId,"userEmail")).thenReturn(ResponseEntity.ok().body("Fichiers enregistrés avec succés"));
    mockMvc.perform(MockMvcRequestBuilders.multipart("/user/attachments/upload/"+ projectId)
      .file((MockMultipartFile) files.get(0))).andDo(print()).andExpect(status().is(200));
  }

  @WithMockUser(authorities = {"USER", "ADMIN"},username = "userEmail")
  @Test
  void uploadAttachment_whenFile_isNotDownloaded() throws Exception {
    when(attachmentService.uploadAttachment(files, projectId,"userEmail")).thenReturn(ResponseEntity.badRequest().body("Erreur lors de l'enregistrement du fichier : " + files.get(0).getOriginalFilename()));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/user/attachments/upload/"+ projectId)
      .file((MockMultipartFile) files.get(0))).andDo(print()).andExpect(status().is(400));
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void editAttachment_whenRenameFile_isOk() throws Exception {
    HttpEntity<String> newAttachmentName = new HttpEntity<>("newName");
    when(attachmentService.renameAttachment(attachmentId , newAttachmentName)).thenReturn(ResponseEntity.ok().body("Votre fichier a été mis à jour"));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put("/user/attachments/edit/"+ attachmentId).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(newAttachmentName))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void editAttachment_whenRenameFile_isNotOk() throws Exception {
    HttpEntity<String> newAttachmentName = new HttpEntity<>("");
    when(attachmentRepository.findById(2L)).thenReturn(Optional.empty());
    when(attachmentService.renameAttachment(2L , newAttachmentName)).thenReturn(ResponseEntity.badRequest().body("Ce fichier n'existe pas !"));
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
    when(userService.findByEmail(user.getUsername())).thenReturn(user);
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(attachmentService.deleteAttachment(attachmentId)).thenReturn(ResponseEntity.ok().body("Fichier supprimé avec succès."));

    ResultActions response = mockMvc.perform(delete("/user/attachments/"+ attachmentId +"/"+project.getProjectId()));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }
  @WithMockUser(authorities = {"USER"})
  @Test
  void deleteAttachmentById_whenAttachment_doseNotExist() throws Exception {
    Attachment attachment = new Attachment();
    attachment.setAttachmentId(attachmentId);
    List<Attachment> attachments = new ArrayList<>();
    attachments.add(attachment);
    project.setAttachments(attachments);

    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(userService.findByEmail(user.getUsername())).thenReturn(user);
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(attachmentService.deleteAttachment(attachmentId)).thenReturn(ResponseEntity.badRequest().body("Erreur lors de la suppression du fichier."));

    ResultActions response = mockMvc.perform(delete("/user/attachments/"+ attachmentId+"/"+project.getProjectId()));
    response.andExpect(status().isBadRequest())
      .andDo(print());
    Assertions.assertEquals(400, response.andReturn().getResponse().getStatus());
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
