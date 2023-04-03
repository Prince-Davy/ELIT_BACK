package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.MultiCriteriaAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MultiCriteriaAnalysisControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  MultiCriteriaAnalysisService multiCriteriaAnalysisService;
  @MockBean
  ProjectRepository projectRepository;
  @InjectMocks
  MultiCriteriaAnalysisController MultiCriteriaAnalysisController;

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  @WithMockUser(authorities = {"ADMIN"})
  @ExtendWith(SpringExtension.class)
  @Test
  void createMultiCriteriaAnalysis_whenMulticriteria_isCreated() throws Exception {
    Long projectId = 1L;
    when(multiCriteriaAnalysisService.createMulticriteriaAnalysisForm(projectId)).thenReturn(ResponseEntity.ok().body("Votre Analyse multicritère est crée avec succés"));
    Assertions.assertEquals("Votre Analyse multicritère est crée avec succés",multiCriteriaAnalysisService.createMulticriteriaAnalysisForm(projectId).getBody());
  }
  @Test
  void createMultiCriteriaAnalysis_whenProjectDoseNotExists() throws Exception {
    Long projectId = 1L;
    when(multiCriteriaAnalysisService.createMulticriteriaAnalysisForm(projectId)).thenThrow( new RuntimeException("Ce projet n'existe pas"));
    Exception exception = assertThrows(RuntimeException.class, () -> {
      multiCriteriaAnalysisService.createMulticriteriaAnalysisForm(projectId);
    });
    String expectedMessage = "Ce projet n'existe pas";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void getMultiCriteriaAnalysis() throws ProjectNotFoundException {
    Long projectId = 1L;
    when(multiCriteriaAnalysisService.getMulticriteriaAnalysis(projectId)).thenReturn(ResponseEntity.ok().body("Result = MulticriteriaAnalyseResponseModel"));
    Assertions.assertEquals("Result = MulticriteriaAnalyseResponseModel",multiCriteriaAnalysisService.getMulticriteriaAnalysis(projectId).getBody());
  }
  @Test
  void getMultiCriteriaAnalysis_whenProjectDoseNotExists() throws ProjectNotFoundException {
    Long projectId = 1L;
    when(multiCriteriaAnalysisService.getMulticriteriaAnalysis(projectId)).thenThrow( new ProjectNotFoundException("Ce projet n'existe pas"));
    Exception exception = assertThrows(ProjectNotFoundException.class, () -> {
      multiCriteriaAnalysisService.getMulticriteriaAnalysis(projectId);
    });
    String expectedMessage = "Ce projet n'existe pas";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void increaseMulticriteriaIteration() throws Exception, UserNotAuthorizedException {
    Long analyseId = 1L;
    when(multiCriteriaAnalysisService.increaseMulticriteriaIteration(analyseId)).thenReturn(ResponseEntity.ok().body("L'analyse multicritères a bien changé d'itération."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/multi-criteria-analysis/iteration/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @Test
  void increaseMulticriteriaIteration_whenMulticriteriaDoseNotExists() throws Exception, UserNotAuthorizedException {
    Long analyseId = 1L;
    when(multiCriteriaAnalysisService.increaseMulticriteriaIteration(analyseId)).thenThrow(new MulticriteriaNotFoundException(
      "Cette analyse multicritères n'existe pas."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/multi-criteria-analysis/iteration/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }
 /* @WithMockUser(authorities = {"ADMIN"})
  @Test
  void updateMultiCriteriaAnalysis() throws Exception {
    Long projectId = 1L;
    FormStep multiCriteriaAnalysisStep = new FormStep();
    when(multiCriteriaAnalysisService.updateMultiCriteriaAnalysis(projectId, multiCriteriaAnalysisStep)).thenReturn(ResponseEntity.ok().body("Votre Modification sont enregistrées avec succés"));
    MockHttpServletResponse response = mockMvc.perform(put("/api/multi-criteria-analysis/"+projectId).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(multiCriteriaAnalysisStep))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }*/
/*  @WithMockUser(authorities = {"ADMIN"})
  @Test
  void updateMultiCriteriaAnalysis_whenStepDoseNotExist() {
    Long projectId = 1L;
    FormStep multiCriteriaAnalysisStep = new FormStep();
    when(multiCriteriaAnalysisService.updateMultiCriteriaAnalysis(projectId, multiCriteriaAnalysisStep)).thenThrow(new RuntimeException("cette étape n'existe pas"));
    Exception exception = assertThrows(RuntimeException.class, () -> {
      multiCriteriaAnalysisService.updateMultiCriteriaAnalysis(projectId, multiCriteriaAnalysisStep);
    });
    String expectedMessage = "cette étape n'existe pas";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }*/

  @Test
  void updateQuestion() throws Exception {
    FormQuestionModel formQuestion = new FormQuestionModel();
    when(multiCriteriaAnalysisService.updateMultiCriteriaQuestion(formQuestion)).thenReturn(ResponseEntity.ok().body("Votre Modification sont enregistrées avec succés"));
    MockHttpServletResponse response = mockMvc.perform(put("/api/multi-criteria-analysis/question").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(formQuestion))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @Test
  void updateQuestion_whenQuestionDoseNotExists() throws Exception {
    FormQuestionModel formQuestion = new FormQuestionModel();
    when(multiCriteriaAnalysisService.updateMultiCriteriaQuestion(formQuestion)).thenThrow(new MulticriteriaNotFoundException("Ce fichier n'existe pas !"));
    MockHttpServletResponse response = mockMvc.perform(put("/api/multi-criteria-analysis/question").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(formQuestion))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void getMulticriteriaAnalysisScore() throws Exception {
    Long projectId = 1L;
    when(multiCriteriaAnalysisService.calculateMultiCriteriaAnalysisScore(projectId)).thenReturn(ResponseEntity.ok().body(null));
    MockHttpServletResponse response = mockMvc.perform(get("/api/multi-criteria-analysis/"+projectId+"/score")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @Test
  void getMulticriteriaAnalysisScore_whenEscoreQuestionsDoseNotExists() throws Exception {
    Long projectId = 1L;
    when(multiCriteriaAnalysisService.calculateMultiCriteriaAnalysisScore(projectId)).thenThrow(new EscoreException("score n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(get("/api/multi-criteria-analysis/"+projectId+"/score")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(406, response.getStatus());
  }
}


