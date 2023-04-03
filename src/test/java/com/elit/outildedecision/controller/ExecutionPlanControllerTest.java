package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.ExecutionPlanAnswerModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.ExecutionVoteRequestModel;
import com.elit.outildedecision.repository.ExecutionPlanRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.ExecutionPlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExecutionPlanControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  ProjectRepository  projectRepository;
  @MockBean
  ExecutionPlanService executionPlanService;
  @MockBean
  ExecutionPlanRepository executionPlanRepository;
  @InjectMocks
  ExecutionPlanController ExecutionPlanController;

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void createExecutionPlan() throws Exception {
   Long projectId = 1L;
    when(executionPlanService.createExecutionPlan(projectId)).thenReturn(ResponseEntity.ok().body(1L));
    ResultActions response = mockMvc.perform(post("/api/executionPlan/create/"+ projectId));
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
  void getQuestionsAndAnswers() throws Exception {
    Long executionPlanId = 1L;
    when(executionPlanService.getQuestionsAndAnswers(executionPlanId)).thenReturn(ResponseEntity.ok().body("List of executionPlanFormMode"));
    ResultActions response = mockMvc.perform(get("/api//executionPlan/formConstraints/"+ executionPlanId));
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
  void updateAnswers() throws Exception {
    Long executionPlanId = 1L;
    ExecutionPlanAnswerModel executionPlanAnswerModel = new ExecutionPlanAnswerModel();
    executionPlanAnswerModel.setExecutionPlanQuestionId(1L);
    List<ExecutionPlanAnswerModel> executionPlanAnswerModels = new ArrayList<>();
    executionPlanAnswerModels.add(executionPlanAnswerModel);
    when(executionPlanService.updateAnswers(executionPlanAnswerModels, executionPlanId)).thenReturn(ResponseEntity.ok().body( "La réponse avec l'id "+executionPlanId+" a bien été modifié."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/executionPlan/formConstraints/putAnswers/"+ executionPlanId).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(executionPlanAnswerModels))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void getMethodologies() throws Exception {
    Long executionPlanId = 1L;
    when(executionPlanService.getMethodologies(executionPlanId)).thenReturn(ResponseEntity.ok().body("List of ExecutionVoteResponse"));
    ResultActions response = mockMvc.perform(get("/api//executionPlan/methodologies/"+ executionPlanId));
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
  void postMethodologiesVote() throws UserNotAuthorizedException, Exception {
    ExecutionVoteRequestModel executionVoteRequestModel = new ExecutionVoteRequestModel();
    when(executionPlanService.postMethodologiesVote(executionVoteRequestModel)).thenReturn(ResponseEntity.ok().body("Le vote a bien été enregistré."));
    MockHttpServletResponse response = mockMvc.perform(post("/api/executionPlan/methodologies/vote").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(executionVoteRequestModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());

  }

  @Test
  void postMethodologiesVote_whenExecutionPlanDoseNotExists() throws Exception, UserNotAuthorizedException {
    ExecutionVoteRequestModel executionVoteRequestModel = new ExecutionVoteRequestModel();
    when(executionPlanService.postMethodologiesVote(executionVoteRequestModel)).thenThrow(new RuntimeException("Ce plan d'exécution n'existe pas."));
    Exception e = Assertions.assertThrows(RuntimeException.class,
      () -> executionPlanService.postMethodologiesVote(executionVoteRequestModel));
    Assertions.assertEquals("Ce plan d'exécution n'existe pas.", e.getMessage());
  }
  @Test
  void postMethodologiesVote_whenVoteUserAlreadyExists() throws Exception, UserNotAuthorizedException {
    ExecutionVoteRequestModel executionVoteRequestModel = new ExecutionVoteRequestModel();
    when(executionPlanService.postMethodologiesVote(executionVoteRequestModel)).thenThrow(new RuntimeException("Cet utilisateur a déjà soumis un vote sur ce plan d'exécution."));
    Exception e = Assertions.assertThrows(RuntimeException.class,
      () -> executionPlanService.postMethodologiesVote(executionVoteRequestModel));
    Assertions.assertEquals("Cet utilisateur a déjà soumis un vote sur ce plan d'exécution.", e.getMessage());
  }

  @Test
  void getExecutionPlanScore() throws Exception, UserNotAuthorizedException {
    Long executionPlanId = 1L;
    ExecutionPlanScoreResponseModel executionPlanScoreResponseModel = new ExecutionPlanScoreResponseModel();
    when(executionPlanService.getExecutionPlanScore(executionPlanId)).thenReturn(executionPlanScoreResponseModel);
    ResultActions response = mockMvc.perform(get("/api/executionPlan/"+ executionPlanId+"/score"));
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
  void putIsFinishedExecutionPlan() throws UserNotAuthorizedException, Exception {
    Long executionPlanId = 1L;
    when(executionPlanService.putIsFinishedExecutionPlan(executionPlanId)).thenReturn(ResponseEntity.ok().body(true));
    MockHttpServletResponse response = mockMvc.perform(post("/api/executionPlan/isFinished/"+executionPlanId)).andDo(print()).andReturn().getResponse();
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
