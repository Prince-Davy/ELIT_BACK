package com.elit.outildedecision.collaboration.predictibility.controller;

import com.elit.outildedecision.collaboration.predictibility.dto.ChosenMethodologyRequest;
import com.elit.outildedecision.collaboration.predictibility.dto.PredictibilityScoreResponse;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.predictibility.service.PredictibilityAnalysisService;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.handler.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Unit test for PredictibilityAnalysisController.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PredictibilityAnalysisControllerTest {

  private MockMvc mockMvc;
  @Mock
  private PredictibilityAnalysisService predictibilityAnalysisService;
  @InjectMocks
  PredictibilityAnalysisController predictibilityAnalysisController;

  Long projectId;
  Long analyseId;
  String chosenMethodology;

  @BeforeEach
  public void setup() {
    mockMvc = standaloneSetup(predictibilityAnalysisController)
        .setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

    projectId = 1L;
    analyseId = 1L;
    chosenMethodology = "{\"chosenMethodology\": \"Agile\"}";
  }

  @Test
  void createPredictibilityAnalysis() throws Exception {
    when(predictibilityAnalysisService.createPredictibilityForm(projectId)).thenReturn(1L);
    MockHttpServletResponse response = mockMvc.perform(post("/predictibility-analysis/" + projectId))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void createPredictibilityAnalysis_whenProjectDoseNotExist() throws Exception {
    when(predictibilityAnalysisService.createPredictibilityForm(projectId))
        .thenThrow(new ProjectNotFoundException("Ce projet n'existe pas"));

    mockMvc.perform(post("/predictibility-analysis/" + projectId))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Ce projet n'existe pas")));
  }

  @Test
  void createPredictibilityAnalysis_whenMultiCriteriaNotFinished() throws Exception {
    when(predictibilityAnalysisService.createPredictibilityForm(projectId))
        .thenThrow(new ConflictException(
            "L'analyse multicritère doit être finalisée avant de créer la prévisibilité."));
    MockHttpServletResponse response = mockMvc.perform(post("/predictibility-analysis/" + projectId))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(409, response.getStatus());
  }

  @Test
  void addFinalChosenMethodology() throws Exception {
    when(predictibilityAnalysisService.addFinalChosenMethodology(any(ChosenMethodologyRequest.class), eq(projectId), eq(analyseId)))
        .thenReturn("La méthodologie finale choisie est : Agile");
    MockHttpServletResponse response = mockMvc.perform(
        post("/" + projectId + "/predictibility/" + analyseId + "/chosenMethodology")
            .content(chosenMethodology)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void addFinalChosenMethodology_whenBadRequest() throws Exception {
    when(predictibilityAnalysisService.addFinalChosenMethodology(any(ChosenMethodologyRequest.class), eq(projectId), eq(analyseId)))
        .thenThrow(new BadRequestException("Erreur lors du traitement de la méthodologie choisie."));
    MockHttpServletResponse response = mockMvc.perform(
        post("/" + projectId + "/predictibility/" + analyseId + "/chosenMethodology")
            .content(chosenMethodology)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(400, response.getStatus());
  }

  @Test
  void addFinalChosenMethodology_whenPredictibilityDoseNotExist() throws Exception {
    when(predictibilityAnalysisService.addFinalChosenMethodology(any(ChosenMethodologyRequest.class), eq(projectId), eq(analyseId)))
        .thenThrow(new PredictibilityAnalysisNotFoundException("Cette analyse n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(
        post("/" + projectId + "/predictibility/" + analyseId + "/chosenMethodology")
            .content(chosenMethodology)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void shouldGetPredictabilityAnalysisScore_success() throws Exception {
    PredictibilityScoreResponse predictibilityResult = new PredictibilityScoreResponse();
    when(predictibilityAnalysisService.calculatePredictibilityAnalysisScore(projectId, analyseId))
        .thenReturn(predictibilityResult);
    MockHttpServletResponse response = mockMvc.perform(
        get("/" + projectId + "/predictibility/" + analyseId + "/score"))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void shouldGetPredictabilityAnalysisScore_ThrowAnalysisNotFoundException() throws Exception {
    when(predictibilityAnalysisService.calculatePredictibilityAnalysisScore(projectId, analyseId))
        .thenThrow(new PredictibilityAnalysisNotFoundException("Cet analyse n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(
        get("/" + projectId + "/predictibility/" + analyseId + "/score"))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void shouldGetPredictabilityAnalysisScore_ThrowProjectNotFoundException() throws Exception {
    when(predictibilityAnalysisService.calculatePredictibilityAnalysisScore(projectId, analyseId))
        .thenThrow(new ProjectNotFoundException(
            "Le project dans le path et celui de l'analyse ne correspondent pas"));
    MockHttpServletResponse response = mockMvc.perform(
        get("/" + projectId + "/predictibility/" + analyseId + "/score"))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis() throws Exception {
    when(predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(projectId, analyseId))
        .thenReturn("L'analyse de prévisibilité a bien été enregistré comme terminée.");
    MockHttpServletResponse response = mockMvc.perform(
        put("/" + projectId + "/predictibility/isFinished/" + analyseId))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis__whenPredictibilityDoseNotExist() throws Exception {
    when(predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(projectId, analyseId))
        .thenThrow(new PredictibilityAnalysisNotFoundException(
            "Cette analyse de prévisibilité n'existe pas."));
    MockHttpServletResponse response = mockMvc.perform(
        put("/" + projectId + "/predictibility/isFinished/" + analyseId))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis__whenProjectNotFound() throws Exception {
    when(predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(projectId, analyseId))
        .thenThrow(new ProjectNotFoundException(
            "Le project dans le path et celui de l'analyse ne correspondent pas"));
    MockHttpServletResponse response = mockMvc.perform(
        put("/" + projectId + "/predictibility/isFinished/" + analyseId))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void increasePredictibilityAnalyseIterationNumber() throws Exception {
    when(predictibilityAnalysisService.increasePredictAnalysisIterationNum(projectId, analyseId))
        .thenReturn("L'analyse de prévisibilité a rentre dans une nouvelle iteration.");
    MockHttpServletResponse response = mockMvc.perform(
        put("/" + projectId + "/predictibility/iteration/" + analyseId))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void increasePredictibilityAnalyseIterationNumber_whenPredictibilityDoseNotExist() throws Exception {
    when(predictibilityAnalysisService.increasePredictAnalysisIterationNum(projectId, analyseId))
        .thenThrow(new PredictibilityAnalysisNotFoundException(
            "Cette analyse de prévisibilité n'existe pas."));
    MockHttpServletResponse response = mockMvc.perform(
        put("/" + projectId + "/predictibility/iteration/" + analyseId))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void increasePredictibilityAnalyseIterationNumber_whenProjectNotFound() throws Exception {
    when(predictibilityAnalysisService.increasePredictAnalysisIterationNum(projectId, analyseId))
        .thenThrow(new ProjectNotFoundException(
            "Le project dans le path et celui de l'analyse ne correspondent pas"));
    MockHttpServletResponse response = mockMvc.perform(
        put("/" + projectId + "/predictibility/iteration/" + analyseId))
        .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }
}
