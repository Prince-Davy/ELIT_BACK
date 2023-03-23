/*
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.PredictibilityScoreModel;
import com.elit.outildedecision.service.PredictibilityAnalysisService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class PredictibilityAnalysisControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
   private PredictibilityAnalysisService predictibilityAnalysisService;

  @Test
  void createPredictibilityAnalysis() throws Exception {
    Long projectId = 1L;
    when(predictibilityAnalysisService.createPredictibilityForm(projectId)).thenReturn(ResponseEntity.ok().body(1L));
    MockHttpServletResponse response = mockMvc.perform(post("/api/predictibility-analysis/"+projectId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void createPredictibilityAnalysis_whenProjectDoseNotExist() throws Exception {
    Long projectId = 1L;
    when(predictibilityAnalysisService.createPredictibilityForm(projectId)).thenThrow(new RuntimeException("Ce projet n'existe pas"));
    Exception exception = assertThrows(RuntimeException.class, () -> {
      predictibilityAnalysisService.createPredictibilityForm(projectId);
    });
    String expectedMessage = "Ce projet n'existe pas";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void createPredictibilityAnalysis_whenPredictibilityAnalysisIsNotFinished() throws Exception {
    Long projectId = 1L;
    when(predictibilityAnalysisService.createPredictibilityForm(projectId)).thenReturn(ResponseEntity.status(HttpStatus.FORBIDDEN).body(-1L));
    MockHttpServletResponse response = mockMvc.perform(post("/api/predictibility-analysis/"+projectId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(403, response.getStatus());
  }

  @Test
  void getPredictabilityAnalysisScore() throws Exception {
    Long analyseId = 2L;
    PredictibilityScoreModel predictibilityResult = new PredictibilityScoreModel();
    when(predictibilityAnalysisService.calculatePredictibilityAnalysisScore(analyseId)).thenReturn(ResponseEntity.ok().body(predictibilityResult));
    MockHttpServletResponse response = mockMvc.perform(get("/api/predictibility/"+analyseId+"/score")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @Test
  void getPredictabilityAnalysisScore_whenAnalysisDoseNotExist() throws Exception {
    Long analyseId = 2L;
    when(predictibilityAnalysisService.calculatePredictibilityAnalysisScore(analyseId)).thenThrow(new PredictibilityAnalysisNotFoundException("Cet analyse n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(get("/api/predictibility/"+analyseId+"/score")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis() throws UserNotAuthorizedException, Exception {
    Long analyseId = 1L;
    when( predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(analyseId)).thenReturn(ResponseEntity.ok()
      .body("L'analyse de prévisibilité a bien été enregistré comme terminée."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/predictibility/isFinished/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_whenPredictibilityDoseNotExist() throws UserNotAuthorizedException, Exception {
    Long analyseId = 1L;
    when( predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(analyseId)).thenThrow(new PredictibilityAnalysisNotFoundException(
      "Cette analyse de prévisibilité n'existe pas."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/predictibility/isFinished/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void increasePredictibilityAnalyseIterationNumber() throws UserNotAuthorizedException, Exception {
    Long analyseId = 1L;
    when(predictibilityAnalysisService.increasePredictAnalysisIterationNum(analyseId)).thenReturn(ResponseEntity.ok()
      .body("L'analyse de prévisibilité a rentre dans une nouvelle iteration."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/predictibility/iteration/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @Test
  void increasePredictibilityAnalyseIterationNumber_whenPredictibilityDoseNotExist() throws UserNotAuthorizedException, Exception {
    Long analyseId = 1L;
    when(predictibilityAnalysisService.increasePredictAnalysisIterationNum(analyseId)).thenThrow( new PredictibilityAnalysisNotFoundException(
      "Cette analyse de prévisibilité n'existe pas."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/predictibility/iteration/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

}
*/
