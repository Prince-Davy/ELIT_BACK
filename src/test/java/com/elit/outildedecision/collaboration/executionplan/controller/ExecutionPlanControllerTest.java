package com.elit.outildedecision.collaboration.executionplan.controller;

import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanAnswerRequest;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanScoreResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteRequest;
import com.elit.outildedecision.collaboration.executionplan.service.ExecutionPlanService;
import com.elit.outildedecision.shared.exception.handler.RestResponseEntityExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class ExecutionPlanControllerTest {

  private MockMvc mockMvc;

  @Mock
  ExecutionPlanService executionPlanService;
  @InjectMocks
  ExecutionPlanController executionPlanController;

  @BeforeEach
  void setup() {
    mockMvc = standaloneSetup(executionPlanController)
        .setControllerAdvice(RestResponseEntityExceptionHandler.class)
        .build();
  }

  private static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void createExecutionPlanShouldReturnOk() throws Exception {
    when(executionPlanService.createExecutionPlan(1L)).thenReturn(1L);
    mockMvc.perform(post("/executionPlan/create/1")).andExpect(status().isOk());
  }

  @Test
  void getQuestionsAndAnswersShouldReturnOk() throws Exception {
    when(executionPlanService.getQuestionsAndAnswers(1L, 1L)).thenReturn(List.of());
    mockMvc.perform(get("/1/executionPlan/formConstraints/1")).andExpect(status().isOk());
  }

  @Test
  void updateAnswersShouldReturnOk() throws Exception {
    when(executionPlanService.updateAnswers(eq(1L), eq(1L), any())).thenReturn(List.of("ok"));
    mockMvc.perform(put("/1/executionPlan/formConstraints/putAnswers/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(List.of(new ExecutionPlanAnswerRequest()))))
        .andExpect(status().isOk());
  }

  @Test
  void getMethodologiesShouldReturnOk() throws Exception {
    when(executionPlanService.getMethodologies(1L, 1L)).thenReturn(List.of());
    mockMvc.perform(get("/1/executionPlan/methodologies/1")).andExpect(status().isOk());
  }

  /**
   * Test for the getVotes method of ExecutionPlanController.
   * It verifies that the method returns a OK status when called with valid
   * parameters.
   */
  @Test
  void getVotesShouldReturnOk() throws Exception {
    when(executionPlanService.getVotes(1L)).thenReturn(List.of());
    mockMvc.perform(get("/1/executionPlan/methodologies/votes/1")).andExpect(status().isOk());
  }

  @Test
  void postMethodologiesVoteShouldReturnOk() throws Exception {
    when(executionPlanService.postMethodologiesVote(eq(1L), any(ExecutionVoteRequest.class)))
        .thenReturn("Le vote a bien été enregistré.");
    mockMvc.perform(post("/1/executionPlan/methodologies/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(new ExecutionVoteRequest())))
        .andExpect(status().isOk());
  }

  @Test
  void getExecutionPlanScoreShouldReturnOk() throws Exception {
    when(executionPlanService.getExecutionPlanScore(anyLong(), anyLong()))
        .thenReturn(new ExecutionPlanScoreResponse());
    mockMvc.perform(get("/1/executionPlan/1/score")).andExpect(status().isOk());
  }

  @Test
  void putIsFinishedExecutionPlanShouldReturnOk() throws Exception {
    when(executionPlanService.putIsFinishedExecutionPlan(1L, 1L)).thenReturn(true);
    mockMvc.perform(post("/1/executionPlan/isFinished/1")).andExpect(status().isOk());
  }
}
