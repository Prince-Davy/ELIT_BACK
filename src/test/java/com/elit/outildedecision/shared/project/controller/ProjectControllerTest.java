package com.elit.outildedecision.shared.project.controller;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.exception.handler.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.shared.project.dto.ContributorRequest;
import com.elit.outildedecision.shared.project.dto.ProjectFilterRequest;
import com.elit.outildedecision.shared.project.dto.ProjectRequest;
import com.elit.outildedecision.shared.project.dto.ProjectResponse;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.shared.project.service.ProjectValidationResult;
import com.elit.outildedecision.shared.util.ProjectPermession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.MediaType;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Unit test for ProjectController
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectControllerTest {

  private MockMvc mockMvc;

  @Mock
  ProjectService projectService;

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

    ProjectRequest projectRequest = new ProjectRequest();
    ProjectResponse projectToReturn = new ProjectResponse();
    when(projectService.persistProject(projectRequest,"userEmail")).thenReturn(projectToReturn);
    mockMvc.perform(MockMvcRequestBuilders.multipart("/user/projects")
      .file((MockMultipartFile) files.get(0))).andDo(print()).andExpect(status().is(200));
  }

  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjectData() throws Exception {
    Long projectId = 1L;
    ProjectResponse projectResponseModel = new ProjectResponse();
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
    List<ProjectResponse> projects = new ArrayList<>();
    when(projectService.getUserProjects(keyword)).thenReturn(projects);
    MockHttpServletResponse response = mockMvc.perform(get("/user/projects").param("required","false")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void getAllProjects() throws Exception {
    List<ProjectResponse> projects = new ArrayList<>();
    when(projectService.getAllProjects()).thenReturn(projects);
    MockHttpServletResponse response = mockMvc.perform(get("/admin/projects")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @WithMockUser(authorities = {"ADMIN", "USER"})
  @Test
  void getProjectList() throws Exception {
    ProjectFilterRequest triModel = new ProjectFilterRequest("", "ALL", "DATEDESC", true, 0, 10,"ALL");
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
    when(projectService.validateOrRejectProject(id, false)).thenReturn(ProjectValidationResult.REJECTED);
    MockHttpServletResponse response = mockMvc.perform(put("/admin/projects/"+id).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content("{\"isConfirmed\":false}")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }


  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void deleteProjectADMIN_Success() throws Exception {
    when(projectService.deleteProject(project.getProjectId())).thenReturn("Le projet a été bien supprimé");

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
    when(projectService.deleteProject(id)).thenThrow(new ProjectNotFoundException("Ce project n'existe pas !"));
    MockHttpServletResponse response = mockMvc.perform(delete("/admin/projects/"+id)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void deleteProject_Success() throws Exception {
    when(projectService.deleteProject(project.getProjectId())).thenReturn("Le projet a été bien supprimé");

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
    when(projectService.deleteProject(id)).thenThrow(new ProjectNotFoundException("Ce project n'existe pas !"));
    MockHttpServletResponse response = mockMvc.perform(delete("/user/projects/"+id)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }
  @WithMockUser(authorities = {"USER"})
  @Test
  void addProjectContributor_Success() throws Exception {
    ContributorRequest contributor = new ContributorRequest();
    when(projectService.addProjectContributor(project.getProjectId(), contributor)).thenReturn("Le collaborateur a été bien enregistré");

    mockMvc.perform(post("/user/projects/"+project.getProjectId()+"/add-contributor"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le collaborateur a")));

    verify(projectService, times(1)).addProjectContributor(project.getProjectId(), contributor);
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void addProjectContributor_ThrowProjectNOtFound() throws Exception {
    ContributorRequest contributor = new ContributorRequest();
    when(projectService.addProjectContributor(project.getProjectId(), contributor)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas! "));

    mockMvc.perform(post("/user/projects/"+project.getProjectId()+"/add-contributor"))
      .andDo(print())
      .andExpect(status().isNotFound());
    verify(projectService, times(1)).addProjectContributor(project.getProjectId(), contributor);
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void addProjectContributor_ThrowUserNotFound() throws Exception {
    ContributorRequest contributor = new ContributorRequest();
    when(projectService.addProjectContributor(project.getProjectId(), contributor)).thenThrow(new ProjectNotFoundException("Ce Projet n'existe pas! "));

    mockMvc.perform(post("/user/projects/"+project.getProjectId()+"/add-contributor"))
      .andDo(print())
      .andExpect(status().isNotFound());
    verify(projectService, times(1)).addProjectContributor(project.getProjectId(), contributor);
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void DeleteProjectContributor_Success() throws Exception {
    Long contributorId =2L;
    when(projectService.deleteProjectContributor(project.getProjectId(), contributorId)).thenReturn("Le collaborateur a été bien retiré du projet");

    mockMvc.perform(delete("/user/projects/"+project.getProjectId()+"/delete-contributor/"+contributorId))
      .andDo(print())
      .andExpect(status().isOk());
    verify(projectService, times(1)).deleteProjectContributor(project.getProjectId(), contributorId);
  }

  @WithMockUser(authorities = {"USER"})
  @Test
  void DeleteProjectContributor_ThrowProjectNotFound() throws Exception {
    Long contributorId =2L;
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
    when(projectService.deleteProjectContributor(project.getProjectId(), contributorId)).thenThrow(new ProjectNotFoundException("Cet utilisateur n'existe pas dans le projet !"));

    mockMvc.perform(delete("/user/projects/"+project.getProjectId()+"/delete-contributor/"+contributorId))
      .andDo(print())
      .andExpect(status().isNotFound());
    verify(projectService, times(1)).deleteProjectContributor(project.getProjectId(), contributorId);
  }
}
