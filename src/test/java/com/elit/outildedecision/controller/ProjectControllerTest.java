package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ProjectFolderAlreadyExistsException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.model.*;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.elit.outildedecision.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.service.ProjectService;
import com.elit.outildedecision.enumeration.ProjectPermession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Unit test for ProjectController
 */
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class ProjectControllerTest {

  private MockMvc mockMvc;

  @Mock
  ProjectService projectService;
  @Mock
  CustomMethodSecurityExpressionRoot customMethodSecurity;

  @InjectMocks
  private ProjectController projectController;

  private List<MultipartFile> files;

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
    files.add(fileMultipart);

    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(projectController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

    user = new User();
    user.setUserEmail("user@user.com");
    project = new Project();
    project.setProjectId(1L);
    permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(ProjectPermession.CONTRIBUTER.toString());
    user.setUserProjectPermissions(List.of(permission));
  }

  @WithMockUser(authorities = {"USER"}, username = "userEmail")
  @Test
  void createProject() throws Exception {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    ProjectModel project = new ProjectModel();
    ProjectResponseModel projectToReturn = new ProjectResponseModel();
    when(projectService.persistProject(project,"userEmail")).thenReturn(projectToReturn);
    mockMvc.perform(MockMvcRequestBuilders.multipart("/user/projects")
      .file((MockMultipartFile) files.get(0))).andDo(print()).andExpect(status().is(200));
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void createProject_whenProjectFolderAlreadyExists() throws Exception {
    ProjectModel project = new ProjectModel();
    when(projectService.persistProject(project,"userEmail")).thenThrow(new ProjectFolderAlreadyExistsException("Dossier du projet dupliqué, réessayer plus tard."));
    Exception exception = assertThrows(ProjectFolderAlreadyExistsException.class, () -> {
      projectService.persistProject(project,"userEmail");
    });
    String expectedMessage = "Dossier du projet dupliqué, réessayer plus tard.";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjectData() throws Exception {
    Long projectId = 1L;
    when(customMethodSecurity.isContributer(projectId)).thenReturn(true);
    ProjectResponseModel projectResponseModel = new ProjectResponseModel();
    when(projectService.getProjectData(projectId)).thenReturn(projectResponseModel);
    MockHttpServletResponse response = mockMvc.perform(get("/user/projects/getdata/"+projectId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjectData_whenProjectNotFound() throws Exception {
    Long projectId = 1L;
    when(projectService.getProjectData(projectId)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas!"));
    MockHttpServletResponse response = mockMvc.perform(get("/user/projects/getdata/"+projectId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjects() throws Exception {
    String keyword = "query";
    List<Project> projects = new ArrayList<>();
    when(projectService.getUserProjects(keyword)).thenReturn(projects);
    MockHttpServletResponse response = mockMvc.perform(get("/user/projects").param("required","false")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void getAllProjects() throws Exception {
    List<Project> projects = new ArrayList<>();
    when(projectService.getAllProjects()).thenReturn(projects);
    MockHttpServletResponse response = mockMvc.perform(get("/admin/projects")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjectList() throws Exception {
    String keyword = "query";
    ResponsePageProject responsePageProject = new ResponsePageProject();
    TriModel triModel = new TriModel("", "ALL", "DATEDESC", true, 0, 10,"ALL");
    ObjectMapper objectMapper = new ObjectMapper();
    String triModelJson = objectMapper.writeValueAsString(triModel);

    MockHttpServletResponse response = mockMvc.perform(post("/user/projectlist/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(triModelJson))
            .andDo(print())
            .andReturn()
            .getResponse();

    Assertions.assertEquals(200, response.getStatus());
  }
  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void validateOrRejectProject() throws Exception {
    Long id = 1L;
    HttpEntity<String> isConfirmed = new HttpEntity<>("confirme");
    when(projectService.validateOrRejectProject(id, isConfirmed)).thenReturn(ResponseEntity.ok().body("Le Projet a été refusé"));
    MockHttpServletResponse response = mockMvc.perform(put("/admin/projects/"+id).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(String.valueOf(isConfirmed)) ).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }


  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void deleteProjectADMIN_Success() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(projectService.deleteProject(project.getProjectId())).thenReturn(ResponseEntity.ok().body("Le projet a été bien supprimé"));

    mockMvc.perform(delete("/admin/projects/"+project.getProjectId()))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le projet a été bien supprim")));

    verify(projectService, times(1)).deleteProject(project.getProjectId());

  }

  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void deleteProjectAdmin_whenProjectNotFound() throws Exception {
    Long id = 1L;
    when(customMethodSecurity.isProjectOwner(id)).thenReturn(true);
    when(projectService.deleteProject(id)).thenThrow(new ProjectNotFoundException("Ce project n'existe pas !"));
    MockHttpServletResponse response = mockMvc.perform(delete("/admin/projects/"+id)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void deleteProject_Success() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(projectService.deleteProject(project.getProjectId())).thenReturn(ResponseEntity.ok().body("Le projet a été bien supprimé"));

    mockMvc.perform(delete("/user/projects/"+project.getProjectId()))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le projet a été bien supprim")));

    verify(projectService, times(1)).deleteProject(project.getProjectId());

  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void deleteProject_whenProjectNotFound() throws Exception {
    Long id = 1L;
    when(customMethodSecurity.isProjectOwner(id)).thenReturn(true);
    when(projectService.deleteProject(id)).thenThrow(new ProjectNotFoundException("Ce project n'existe pas !"));
    MockHttpServletResponse response = mockMvc.perform(delete("/user/projects/"+id)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }
  @WithMockUser(authorities = {"USER"})
  @Test
  void addProjectContributor_Success() throws Exception {
    ContributerModel contributor = new ContributerModel();
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(projectService.addProjectContributor(project.getProjectId(), contributor)).thenReturn(ResponseEntity.ok().body("Le collaborateur a été bien enregistré"));

    mockMvc.perform(post("/user/projects/"+project.getProjectId()+"/add-contributor"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le collaborateur a")));

    verify(projectService, times(1)).addProjectContributor(project.getProjectId(), contributor);
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void addProjectContributor_ThrowProjectNOtFound() throws Exception {
    ContributerModel contributor = new ContributerModel();
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(projectService.addProjectContributor(project.getProjectId(), contributor)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas! "));

    mockMvc.perform(post("/user/projects/"+project.getProjectId()+"/add-contributor"))
      .andDo(print())
      .andExpect(status().isNotFound());
    verify(projectService, times(1)).addProjectContributor(project.getProjectId(), contributor);
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void addProjectContributor_ThrowUserNotFound() throws Exception {
    ContributerModel contributor = new ContributerModel();
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(projectService.addProjectContributor(project.getProjectId(), contributor)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas! "));

    mockMvc.perform(post("/user/projects/"+project.getProjectId()+"/add-contributor"))
      .andDo(print())
      .andExpect(status().isNotFound());
    verify(projectService, times(1)).addProjectContributor(project.getProjectId(), contributor);
  }

  @WithMockUser(authorities = {"USER"})
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

  @WithMockUser(authorities = {"USER"})
  @Test
  void DeleteProjectContributor_Success() throws Exception {
    Long contributorId =2L;
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(projectService.deleteProjectContributor(project.getProjectId(), contributorId)).thenReturn(ResponseEntity.ok().body("Le collaborateur a été bien retiré du projet"));

    mockMvc.perform(delete("/user/projects/"+project.getProjectId()+"/delete-contributor/"+contributorId))
      .andDo(print())
      .andExpect(status().isOk());
    verify(projectService, times(1)).deleteProjectContributor(project.getProjectId(), contributorId);
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void DeleteProjectContributor_ThrowProjectNotFound() throws Exception {
    Long contributorId =2L;
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(projectService.deleteProjectContributor(project.getProjectId(), contributorId)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas!"));

    mockMvc.perform(delete("/user/projects/"+project.getProjectId()+"/delete-contributor/"+contributorId))
      .andDo(print())
      .andExpect(status().isNotFound());
    verify(projectService, times(1)).deleteProjectContributor(project.getProjectId(), contributorId);
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void DeleteProjectContributor_ThrowUserNotFound() throws Exception {
    Long contributorId =2L;
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(projectService.deleteProjectContributor(project.getProjectId(), contributorId)).thenThrow(new ProjectNotFoundException("Cet utilisateur n'existe pas dans le projet !"));

    mockMvc.perform(delete("/user/projects/"+project.getProjectId()+"/delete-contributor/"+contributorId))
      .andDo(print())
      .andExpect(status().isNotFound());
    verify(projectService, times(1)).deleteProjectContributor(project.getProjectId(), contributorId);
  }
}
