package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectFolderAlreadyExistsException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.ContributerModel;
import com.elit.outildedecision.model.ProjectModel;
import com.elit.outildedecision.model.ProjectResponseModel;
import com.elit.outildedecision.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test for ProjectController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  ProjectService projectService;
  @MockBean
  CustomMethodSecurityExpressionRoot customMethodSecurity;
  @InjectMocks
  private ProjectController ProjectController;

  private List<MultipartFile> files;

  @BeforeEach
  public void setup() {
    MockMultipartFile fileMultipart =
      new MockMultipartFile(
        "files",
        "test contract.pdf",
        MediaType.APPLICATION_PDF_VALUE,
        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));
    files = new ArrayList<>();
    files.add(fileMultipart);
  }
  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void createProject() throws Exception {
    ProjectModel project = new ProjectModel();
    ProjectResponseModel projectToReturn = new ProjectResponseModel();
    when(projectService.persistProject(project)).thenReturn(projectToReturn);
    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/projects")
      .file((MockMultipartFile) files.get(0))).andDo(print()).andExpect(status().is(200));
  }
  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void createProject_whenProjectFolderAlreadyExists() throws Exception {
    ProjectModel project = new ProjectModel();
    when(projectService.persistProject(project)).thenThrow(new ProjectFolderAlreadyExistsException("Dossier du projet dupliqué, réessayer plus tard."));
    Exception exception = assertThrows(ProjectFolderAlreadyExistsException.class, () -> {
      projectService.persistProject(project);
    });
    String expectedMessage = "Dossier du projet dupliqué, réessayer plus tard.";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjectData() throws Exception {
    Long projectId = 1L;
    ProjectResponseModel projectResponseModel = new ProjectResponseModel();
    when(projectService.getProjectData(projectId)).thenReturn(projectResponseModel);
    MockHttpServletResponse response = mockMvc.perform(get("/api/user/projects/getdata/"+projectId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjectData_whenProjectNotFound() throws Exception {
    Long projectId = 1L;
    when(projectService.getProjectData(projectId)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas!"));
    MockHttpServletResponse response = mockMvc.perform(get("/api/user/projects/getdata/"+projectId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }
  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjects() throws Exception {
    String keyword = "query";
    List<Project> projects = new ArrayList<>();
    when(projectService.getUserProjects(keyword)).thenReturn(projects);
    MockHttpServletResponse response = mockMvc.perform(get("/api/user/projects").param("required","false")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void getAllProjects() throws Exception {
    List<Project> projects = new ArrayList<>();
    when(projectService.getAllProjects()).thenReturn(projects);
    MockHttpServletResponse response = mockMvc.perform(get("/api/admin/projects")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjectList() throws Exception {
    String keyword = "query";
    List<ProjectResponseModel> projectList = new ArrayList<>();
    when(projectService.getUserProjectList(keyword)).thenReturn(projectList);
    MockHttpServletResponse response = mockMvc.perform(get("/api/user/projectlist/").param("required","false")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void validateOrRejectProject() throws Exception {
    Long id = 1L;
    HttpEntity<String> isConfirmed = new HttpEntity<>("confirme");
    when(projectService.validateOrRejectProject(id, isConfirmed)).thenReturn(ResponseEntity.ok().body("Le Projet a été refusé"));
    MockHttpServletResponse response = mockMvc.perform(put("/api/admin/projects/"+id).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(String.valueOf(isConfirmed)) ).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void validateOrRejectProjec_ProjectNotFound() throws Exception {
    Long id = 1L;
    HttpEntity<String> isConfirmed = new HttpEntity<>("confirme");
    when(projectService.validateOrRejectProject(id, isConfirmed)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas!"));
    Exception exception = assertThrows(ProjectNotFoundException.class, () -> {
      projectService.validateOrRejectProject(id, isConfirmed);
    });
    String expectedMessage ="Ce Projet n'existe pas!";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void deleteProjectAdmin() throws Exception {
    Long id = 1L;
    when(projectService.deleteProject(id)).thenReturn(ResponseEntity.ok().body("Le projet a été bien supprimé"));
    MockHttpServletResponse response = mockMvc.perform(delete("/api/admin/projects/"+id)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void deleteProjectAdmin_whenProjectDoseNotFound() throws Exception {
    Long id = 1L;
    when(projectService.deleteProject(id)).thenThrow(new ProjectNotFoundException("Ce project n'existe pas !"));
    MockHttpServletResponse response = mockMvc.perform(delete("/api/admin/projects/"+id)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }
  @WithMockUser(authorities = {"ADMIN","USER"})
  @Test
  void deleteProject() throws Exception {
    Long id = 1L;
    when(customMethodSecurity.isProjectOwner(id)).thenReturn(true);
    when(projectService.deleteProject(id)).thenReturn(ResponseEntity.ok().body("Le projet a été bien supprimé"));
    Assertions.assertEquals("Le projet a été bien supprimé", projectService.deleteProject(id).getBody());
  }
  @WithMockUser(authorities = {"ADMIN","USER"})
  @Test
  void deleteProject_whenProjectNotFound() throws Exception {
    Long id = 1L;
    when(customMethodSecurity.isProjectOwner(id)).thenReturn(true);
    when(projectService.deleteProject(id)).thenThrow(new ProjectNotFoundException("Ce project n'existe pas !"));
    MockHttpServletResponse response = mockMvc.perform(delete("/api/admin/projects/"+id)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @WithMockUser(authorities = {"ADMIN","USER"})
  @Test
  void addProjectContributor() throws Exception {
    Long projectId = 1L;
    ContributerModel contributor = new ContributerModel();
    when(projectService.addProjectContributor(projectId, contributor)).thenReturn(ResponseEntity.ok().body("Le collaborateur a été bien enregistré"));
    Assertions.assertEquals("Le collaborateur a été bien enregistré", projectService.addProjectContributor(projectId, contributor).getBody());
  }
  @WithMockUser(authorities = {"ADMIN","USER"})
  @Test
  void addProjectContributor_whenProjectNotFound() throws Exception {
    Long projectId = 1L;
    ContributerModel contributor = new ContributerModel();
    when(projectService.addProjectContributor(projectId, contributor)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas! "));
    Exception exception = assertThrows(ProjectNotFoundException.class, () -> {
      projectService.addProjectContributor(projectId, contributor);
    });
    String expectedMessage ="Ce Projet n'existe pas! ";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
  @WithMockUser(authorities = {"ADMIN","USER"})
  @Test
  void deleteProjectContributor() throws Exception {
    Long projectId = 1L;
    Long contributorID =2L;
    when(projectService.deleteProjectContributor(projectId, contributorID)).thenReturn(ResponseEntity.ok().body("Le collaborateur a été bien retiré du projet"));
    Assertions.assertEquals("Le collaborateur a été bien retiré du projet", projectService.deleteProjectContributor(projectId, contributorID).getBody());
  }
  @WithMockUser(authorities = {"ADMIN","USER"})
  @Test
  void deleteProjectContributor_whenProjectNotFound() throws Exception {
    Long projectId = 1L;
    Long contributorID =2L;
    when(projectService.deleteProjectContributor(projectId, contributorID)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas!"));
    Exception exception = assertThrows(ProjectNotFoundException.class, () -> {
      projectService.deleteProjectContributor(projectId, contributorID);
    });
    String expectedMessage ="Ce Projet n'existe pas!";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
  @WithMockUser(authorities = {"ADMIN","USER"})
  @Test
  void deleteProjectContributor_whenUserNotFound() throws Exception {
    Long projectId = 1L;
    Long contributorID =2L;
    when(projectService.deleteProjectContributor(projectId, contributorID)).thenThrow(new ProjectNotFoundException("Cet utilisateur n'existe pas dans le projet !"));
    Exception exception = assertThrows(ProjectNotFoundException.class, () -> {
      projectService.deleteProjectContributor(projectId, contributorID);
    });
    String expectedMessage ="Cet utilisateur n'existe pas dans le projet !";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
}
