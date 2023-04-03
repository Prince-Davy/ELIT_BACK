package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.service.SuperAdminService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SuperAdminControllerTests {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SuperAdminService superAdminService;
  @InjectMocks
  private SuperAdminController superAdminController;

  private Long projectId= 1L;
  private Long userId= 1L;
  private int pageNumber = 1;

  private String notConfirmedProjectDateOrderType = "DESC";
  private String searchKeyWord = "project";

  @Test
  @WithMockUser (authorities = {"ADMIN"})
  void shouldConfirmProject_success() throws Exception {
    when(superAdminService.confirmProject(projectId)).thenReturn(ResponseEntity.ok("Votre projet a été confirmé"));
    mockMvc.perform(put("/api/projects/"+ projectId))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Votre projet a")));
  }

  @Test
  @WithMockUser (authorities = {"ADMIN"})
  void shouldConfirmProject_ThrowProjectNotExistsException() throws Exception {
    when(superAdminService.confirmProject(projectId)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas!"));
    mockMvc.perform(put("/api/projects/"+ projectId))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("Ce projet n'existe pas!"));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldSuspenUser_Success() throws Exception {
    when(superAdminService.suspendUser(userId)).thenReturn(ResponseEntity.ok("L'utilisateur a été suspendu"));
    mockMvc.perform(put("/api/users/" +userId))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("L'utilisateur a ")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldSuspendUser_ThrowUserNotFoundException() throws Exception {
    when(superAdminService.suspendUser(userId)).thenThrow(new UserNotFoundException("Cet utilisateur n'existe pas!"));
    mockMvc.perform(put("/api/users/" +userId))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("Cet utilisateur n'existe pas!"));;
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldDeleteProject_Success() throws Exception {
    when(superAdminService.deleteProject(projectId)).thenReturn(ResponseEntity.ok("Le projet a été supprimé"));
    mockMvc.perform(delete("/api/projects/"+projectId))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le projet a")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldDeleteProject_ThrowProjectNotFoundException() throws Exception {
    when(superAdminService.deleteProject(projectId)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas!"));
    mockMvc.perform(delete("/api/projects/"+ projectId))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("Ce projet n'existe pas!"));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldGetAllNonConfirmedProjects_Success() throws Exception {
    Page<Project>  page = new PageImpl<>(Arrays.asList(ModelSamples.ProjectSamples.getProject()));
    when(superAdminService.getAllNonConfirmedProjects(pageNumber)).thenReturn(page);
    mockMvc.perform(get("/api/projects/notConfirmed/"+ pageNumber))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(page)));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldGetAllConfirmedProjectsOrderByDate_Success() throws Exception {
    Page<Project>  page = new PageImpl<>(ModelSamples.ProjectSamples.getProjects());
    when(superAdminService.getAllNonConfirmedProjectsOrderByDate(pageNumber, notConfirmedProjectDateOrderType)).thenReturn(page);
    mockMvc.perform(get("/api/projects/notConfirmed/"+ pageNumber +"/orderByDate").param("order", notConfirmedProjectDateOrderType))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(page)));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldConfimListOfProjects_Success() throws Exception {
    List<Long> projectIds = Arrays.asList(1L, 2L);
    when(superAdminService.confirmProjects(projectIds)).thenReturn(ResponseEntity.ok().body("Vos projets sont confirmés"));
    mockMvc.perform(put("/api/projects/confirmProjects").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(projectIds)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Vos projets sont confirm")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldFindNotConfirmed_Success() throws Exception {
    Page<Project> page = new PageImpl<>(ModelSamples.ProjectSamples.getProjects());
    when(superAdminService.findNotConfirmedProjectByKeyword(searchKeyWord, pageNumber)).thenReturn(page);
    mockMvc.perform(get("/api/projects/notConfirmed/"+pageNumber+"/search").param("keyword", searchKeyWord))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(page)));
  }

}
