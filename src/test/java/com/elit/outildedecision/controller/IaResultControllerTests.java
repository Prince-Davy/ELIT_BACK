package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.*;

import com.elit.outildedecision.exception.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.model.ResultAiModel;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ResultIaRepository;
import com.elit.outildedecision.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.service.ResultIaService;
import com.elit.outildedecision.util.ProjectPermession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ActiveProfiles("Test")
@RunWith(MockitoJUnitRunner.class)
class IaResultControllerTests {

  @Mock
  private ResultIaService resultIaService;

  @Mock
  private ResultIaRepository resultIaRepository;
  @Mock
  private ProjectRepository projectRepository;
  @InjectMocks
  private IaResultController IaResultController;
  MockMvc mockMvc;
  private ResultAiModel resultAiModel ;
  private Project project;
  private ProjectUserPermission permission;
  private User user;
  @Mock
  CustomMethodSecurityExpressionRoot customMethodSecurity;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(IaResultController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();
    project = new Project();
    project.setProjectId(1L);
    user = new User();
    user.setUserEmail("user@user.com");
    user.setUsername("user@user.com");
    permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(ProjectPermession.CDP.toString());
    user.setUserProjectPermissions(List.of(permission));
    resultAiModel = new ResultAiModel();
    resultAiModel.setId(2L);
    resultAiModel.setPredictedMethod("Traditionnelle");
    resultAiModel.setProjectId(1L);
    resultAiModel.setPredictedProba(82.34);
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void saveAiResponse() throws Exception {
    // Given
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);

    List<AnswerIa> answerIa = List.of(new AnswerIa("Question", "Category", "Response", project));

    // When
    mockMvc.perform(post("/result/ai/" + project.getProjectId())
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
          "          ]")).andExpect(status().is(200));

    // then
    Mockito.verify(resultIaService).generateAndSaveResult(project.getProjectId(),answerIa);

  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void shouldSavesaveAiResponseWhenIsAlreadyExist() throws Exception {
    // Given
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);

    Optional<ResultAi> existingResultIa = Optional.of(new ResultAi());
    when(projectRepository.getById(project.getProjectId())).thenReturn(project);
    when(resultIaRepository.findByProject(project)).thenReturn(existingResultIa);

    List<AnswerIa> answerIa = List.of(new AnswerIa("Question", "Category", "Response", project));

    // When
    mockMvc.perform(post("/result/ai/" + project.getProjectId())
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
        "          ]")).andExpect(status().isOk());

    // then
    Mockito.verify(resultIaService).generateAndSaveResult(project.getProjectId(),answerIa);

  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void getAnalyseIAResult() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);

    // When
    mockMvc.perform(get("/result/ai/" + project.getProjectId())
        .contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();
    // then
    Mockito.verify(resultIaService).getResultIa(project.getProjectId());
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void deleteAnalysisIaResult() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isProjectOwner(project.getProjectId())).thenReturn(true);

    MockHttpServletResponse response = mockMvc.perform(delete("/result/ai/" + project.getProjectId()+"/"+resultAiModel.getId())).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

}
