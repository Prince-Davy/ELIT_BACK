package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.ExecutionPlanAnswerModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.ExecutionVoteRequestModel;
import com.elit.outildedecision.repository.ExecutionPlanRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.ExecutionPlanService;
import com.elit.outildedecision.util.ProjectPermession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class ExecutionPlanControllerTest {

  private MockMvc mockMvc;
  @Mock
  ProjectRepository  projectRepository;
  @Mock
  ExecutionPlanService executionPlanService;
  @Mock
  ExecutionPlanRepository executionPlanRepository;
  @InjectMocks
  ExecutionPlanController executionPlanController;

  private User user;
  private Project project;
  private ProjectUserPermission permission;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(executionPlanController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

    user = new User();
    user.setUserEmail("user@user.com");
    project = new Project();
    project.setProjectId(1L);
    permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(ProjectPermession.CONTRIBUTER.name());
    user.setUserProjectPermissions(List.of(permission));
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void createExecutionPlan() throws Exception {
   Long projectId = 1L;
    when(executionPlanService.createExecutionPlan(projectId)).thenReturn(ResponseEntity.ok().body(1L));
    ResultActions response = mockMvc.perform(post("/executionPlan/create/"+ project.getProjectId()));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }
  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void createExecutionPlan_whenProjectDoseNotExists() throws Exception {
    Long projectId = 1L;
    when(executionPlanService.createExecutionPlan(projectId)).thenThrow(new RuntimeException("Ce projet n'existe pas"));
   Exception e = Assertions.assertThrows(RuntimeException.class,
      () -> executionPlanService.createExecutionPlan(projectId));
    Assertions.assertEquals("Ce projet n'existe pas", e.getMessage());

  }

  @Test
  void shouldGetQuestionsAndAnswers_Succsess() throws Exception {
    Long executionPlanId = 1L;
    when(executionPlanService.getQuestionsAndAnswers(project.getProjectId(), executionPlanId)).thenReturn(ResponseEntity.ok().body("List of executionPlanFormMode"));
    ResultActions response = mockMvc.perform(get("/" + project.getProjectId() +"/executionPlan/formConstraints/"+ executionPlanId));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }


  @Test
  void getQuestionsAndAnswers_whenExecutionPlanDoesNotExists() {
    Long executionPlanId = 1L;
    when(executionPlanService.getQuestionsAndAnswers(executionPlanId)).thenThrow(new RuntimeException("Execution plan introuvable.")).getMock();
    Exception e = Assertions.assertThrows(RuntimeException.class,
      () -> executionPlanService.getQuestionsAndAnswers(executionPlanId));
    Assertions.assertEquals("Execution plan introuvable.", e.getMessage());
  }


  @Test
  void shouldUpdateAnswers_Successe() throws Exception {
    Long executionPlanId = 1L;
    ExecutionPlanAnswerModel executionPlanAnswerModel = new ExecutionPlanAnswerModel();
    executionPlanAnswerModel.setExecutionPlanQuestionId(1L);
    List<ExecutionPlanAnswerModel> executionPlanAnswerModels = new ArrayList<>();
    executionPlanAnswerModels.add(executionPlanAnswerModel);
    when(executionPlanService.updateAnswers(project.getProjectId(), executionPlanId, executionPlanAnswerModels)).thenReturn(ResponseEntity.ok().body( "La réponse avec l'id "+executionPlanId+" a bien été modifié."));
    MockHttpServletResponse response = mockMvc.perform(put("/"+ project.getProjectId() + "/executionPlan/formConstraints/putAnswers/"+ executionPlanId).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(executionPlanAnswerModels))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }


  @Test
  void shouldGetMethodologies_Successe() throws Exception {
    Long executionPlanId = 1L;
    when(executionPlanService.getMethodologies(executionPlanId)).thenReturn(ResponseEntity.ok().body("List of ExecutionVoteResponse"));
    ResultActions response = mockMvc.perform(get("/"+ project.getProjectId() + "/executionPlan/methodologies/"+ executionPlanId));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }

  @Test
  void getMethodologies_whenExecutionPlanDoseNotExists() {
    Long executionPlanId = 1L;
    when(executionPlanService.getMethodologies(executionPlanId)).thenThrow(new RuntimeException("Plan d'exécution introuvable."));
    Exception e = Assertions.assertThrows(RuntimeException.class,
      () -> executionPlanService.getMethodologies(executionPlanId));
    Assertions.assertEquals("Plan d'exécution introuvable.", e.getMessage());
  }

  @Test
  void shouldPostMethodologiesVote_successe() throws Exception {
    ExecutionVoteRequestModel executionVoteRequestModel = new ExecutionVoteRequestModel();
    when(executionPlanService.postMethodologiesVote(project.getProjectId(),executionVoteRequestModel)).thenReturn(ResponseEntity.ok().body("Le vote a bien été enregistré."));
    MockHttpServletResponse response = mockMvc.perform(post("/" + project.getProjectId() + "/executionPlan/methodologies/vote").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(executionVoteRequestModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());

  }

  @Test
  void postMethodologiesVote_whenExecutionPlanDoseNotExists() throws Exception {
    ExecutionVoteRequestModel executionVoteRequestModel = new ExecutionVoteRequestModel();
    when(executionPlanService.postMethodologiesVote(executionVoteRequestModel)).thenThrow(new RuntimeException("Ce plan d'exécution n'existe pas."));
    Exception e = Assertions.assertThrows(RuntimeException.class,
      () -> executionPlanService.postMethodologiesVote(executionVoteRequestModel));
    Assertions.assertEquals("Ce plan d'exécution n'existe pas.", e.getMessage());
  }

  @Test
  void postMethodologiesVote_whenVoteUserAlreadyExists() throws Exception {
    ExecutionVoteRequestModel executionVoteRequestModel = new ExecutionVoteRequestModel();
    when(executionPlanService.postMethodologiesVote(executionVoteRequestModel)).thenThrow(new RuntimeException("Cet utilisateur a déjà soumis un vote sur ce plan d'exécution."));
    Exception e = Assertions.assertThrows(RuntimeException.class,
      () -> executionPlanService.postMethodologiesVote(executionVoteRequestModel));
    Assertions.assertEquals("Cet utilisateur a déjà soumis un vote sur ce plan d'exécution.", e.getMessage());
  }

  @Test
  void shouldGetExecutionPlanScore_Successe() throws Exception {
    Long executionPlanId = 1L;
    ExecutionPlanScoreResponseModel executionPlanScoreResponseModel = new ExecutionPlanScoreResponseModel();
    when(executionPlanService.getExecutionPlanScore(project.getProjectId(), executionPlanId)).thenReturn(executionPlanScoreResponseModel);
    ResultActions response = mockMvc.perform(get("/" + project.getProjectId() + "/executionPlan/"+ executionPlanId+"/score"));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }

  @Test
  void getExecutionPlanScore_whenExecutionPlanDoseNotExists() throws UserNotAuthorizedException, ExecutionPlanNotFoundException {
    Long executionPlanId = 1L;
    when(executionPlanService.getExecutionPlanScore(executionPlanId)).thenThrow(new ExecutionPlanNotFoundException("Plan d'exécution introuvable."));
    Exception e = Assertions.assertThrows(ExecutionPlanNotFoundException.class,
      () -> executionPlanService.getExecutionPlanScore(executionPlanId));
    Assertions.assertEquals("Plan d'exécution introuvable.", e.getMessage());
  }


  @Test
  void shouldPutIsFinishedExecutionPlan_Successe() throws Exception {
    Long executionPlanId = 1L;
    when(executionPlanService.putIsFinishedExecutionPlan(project.getProjectId(), executionPlanId)).thenReturn(ResponseEntity.ok().body(true));
    MockHttpServletResponse response = mockMvc.perform(post("/" + project.getProjectId() + "/executionPlan/isFinished/"+executionPlanId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void putIsFinishedExecutionPlan_whenExecutionPlanDoseNotExists() throws Exception, UserNotAuthorizedException {
    Long executionPlanId = 1L;
    when(executionPlanService.putIsFinishedExecutionPlan(executionPlanId)).thenThrow(new ExecutionPlanNotFoundException("Ce plan d'exécution n'existe pas."));
    Exception e = Assertions.assertThrows(ExecutionPlanNotFoundException.class,
      () -> executionPlanService.putIsFinishedExecutionPlan(executionPlanId));
    Assertions.assertEquals("Ce plan d'exécution n'existe pas.", e.getMessage());
  }

}
