package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.service.AnswerIaService;
import com.elit.outildedecision.service.ProjectService;
import com.elit.outildedecision.enumeration.FormProgressStatus;
import com.elit.outildedecision.enumeration.ProjectPermession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@ActiveProfiles("Test")
@RunWith(MockitoJUnitRunner.class)
class AnswerIaControllerTest {

  @Mock
  AnswerIaService answerIaService;

  @Mock
  ProjectService projectService;

  @InjectMocks
  AnswerIaController answerIaController;

  MockMvc mockMvc;

  @Mock
  CustomMethodSecurityExpressionRoot customMethodSecurity;

  private User user;
  private Project project;
  private ProjectUserPermission permission;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(answerIaController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();
    project = new Project();
    project.setProjectId(1L);
    user = new User();
    user.setUserEmail("user@user.com");
    user.setUsername("user@user.com");
    permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(ProjectPermession.CONTRIBUTER.toString());
    user.setUserProjectPermissions(List.of(permission));
  }

  @Test
  void contextLoad() {
    Assertions.assertNotNull(answerIaController);
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void test_getAnswers() throws Exception {
    // Given
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);

    // When
    mockMvc.perform(get("/answers/" + project.getProjectId())
        .contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();

    // Then
    Mockito.verify(answerIaService).getAllAnswers(1L);
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void test_saveMultipleAnswers() throws Exception {
    // Given
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);
    when(projectService.updateFormTypeStatus(project.getProjectId(), FormProgressStatus.IA)).thenReturn(true);

    List<AnswerIa> answerIa = List.of(new AnswerIa("Question", "Category", "Response", project));

    // When
    mockMvc.perform(post("/answers/" + project.getProjectId())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("  [\n" +
          "            {\n" +
          "              \"question\": \"Question\",\n" +
          "              \"category\": \"Category\",\n" +
          "              \"answer\": \"Response\",\n" +
          "              \"project\": {\n" +
          "                \"projectId\": 1\n" +
          "              }\n" +
          "            }\n" +
          "          ]"))
      .andExpect(MockMvcResultMatchers.status().isCreated());

    // then
    Mockito.verify(answerIaService).saveAll(answerIa);

  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void test_deleteAnswersByProject() throws Exception {
    // When
    mockMvc.perform(delete("/answers/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    // then
    Mockito.verify(answerIaService).deleteAnswersByProjectId(1L);


  }

}
