package com.elit.outildedecision.collaboration.multicriteria.controller;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.collaboration.multicriteria.exception.EscoreException;
import com.elit.outildedecision.collaboration.multicriteria.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.exception.handler.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.collaboration.multicriteria.exception.StepFormNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.dto.FormQuestionModel;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepRequest;
import com.elit.outildedecision.collaboration.multicriteria.dto.MulticriteriaAnalysisResponse;
import com.elit.outildedecision.collaboration.multicriteria.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import com.elit.outildedecision.shared.util.ModelSamples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MultiCriteriaAnalysisControllerTest {

  @Mock
  MultiCriteriaAnalysisService multiCriteriaAnalysisService;
  @Mock
  ProjectService projectService;

  @InjectMocks
  MultiCriteriaAnalysisController multiCriteriaAnalysisController;
  private MockMvc mockMvc;
  private Project project;

  @BeforeEach
  public void setup() {
    mockMvc = standaloneSetup(multiCriteriaAnalysisController)
        .setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

    project = new Project();
    project.setProjectId(1L);
  }

  @Test
  void shouldUpdateMultiCriteriaAnalysis_Success() throws Exception {
    when(projectService.updateFormTypeStatus(project.getProjectId(), FormProgressStatus.COLLABORATIVE)).thenReturn(true);

    MultiCriteriaStepRequest multiCriteriaAnalysisStep = new MultiCriteriaStepRequest();

    mockMvc.perform(put("/multi-criteria-analysis/" + project.getProjectId()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(ModelSamples.toJson(multiCriteriaAnalysisStep)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Votre Modification sont")));

    verify(multiCriteriaAnalysisService, times(1)).updateMultiCriteriaAnalysis(project.getProjectId(), multiCriteriaAnalysisStep);
  }

  @Test
  void shouldUpdateMultiCriteriaAnalysis_ThrowException() throws Exception {
    MultiCriteriaStepRequest multiCriteriaAnalysisStep = new MultiCriteriaStepRequest();

    doThrow(new StepFormNotFoundException("cette étape n'existe pas"))
    .when(multiCriteriaAnalysisService)
    .updateMultiCriteriaAnalysis(project.getProjectId(), multiCriteriaAnalysisStep);

    mockMvc.perform(put("/multi-criteria-analysis/"+project.getProjectId()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(multiCriteriaAnalysisStep)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("cette")));

    verify(multiCriteriaAnalysisService, times(1)).updateMultiCriteriaAnalysis(project.getProjectId(), multiCriteriaAnalysisStep);
  }

  @Test
  void shouldGetMultiCriteriaAnalysis_ThrowProjectNotFound() throws Exception {
    when(multiCriteriaAnalysisService.getMulticriteriaAnalysis(
      project.getProjectId())).thenThrow( new ProjectNotFoundException("Ce projet n'existe pas"));

    mockMvc.perform(get("/multi-criteria-analysis/"+project.getProjectId()))
      .andDo(print())
      .andExpect(status().isNotFound());

    verify(multiCriteriaAnalysisService, times(1)).getMulticriteriaAnalysis(project.getProjectId());
  }

  @Test
  void shouldGetMultiCriteriaAnalysis_Success() throws Exception {
    MulticriteriaAnalysisResponse responseModel = new MulticriteriaAnalysisResponse();
    responseModel.setMultiCriteriaAnalysisId(1L);
    when(multiCriteriaAnalysisService.getMulticriteriaAnalysis(project.getProjectId())).thenReturn(Optional.of(responseModel));

    mockMvc.perform(get("/multi-criteria-analysis/"+project.getProjectId()))
      .andDo(print())
      .andExpect(status().isOk());

    verify(multiCriteriaAnalysisService, times(1)).getMulticriteriaAnalysis(project.getProjectId());
  }

  @Test
  void updateQuestion_v2_Success() throws Exception {
    FormQuestionModel formQuestion = new FormQuestionModel();

    doThrow(new MulticriteriaNotFoundException("Ce fichier n'existe pas !"))
      .when(multiCriteriaAnalysisService).updateMultiCriteriaQuestion(project.getProjectId(), formQuestion);

    mockMvc.perform(put("/" + project.getProjectId() + "/multi-criteria-analysis/question").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(ModelSamples.toJson(formQuestion)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce fichier n'existe pas !")));

    verify(multiCriteriaAnalysisService, times(1)).updateMultiCriteriaQuestion(project.getProjectId(), formQuestion);
  }

  @Test
  void updateQuestion_v2_whenQuestionDoseNotExists() throws Exception {
    FormQuestionModel formQuestion = new FormQuestionModel();

    mockMvc.perform(put("/" + project.getProjectId() + "/multi-criteria-analysis/question").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(ModelSamples.toJson(formQuestion)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("La questions")));

    verify(multiCriteriaAnalysisService, times(1)).updateMultiCriteriaQuestion(project.getProjectId(), formQuestion);
  }

  @Test
  void shouldIncreaseMultiCriteriaAnalysis_Success() throws Exception {
    Long analyseId = 1L;
    MultiCriteriaStepRequest multiCriteriaAnalysisStep = new MultiCriteriaStepRequest();

    mockMvc.perform(
        put("/" + project.getProjectId() + "/multi-criteria-analysis/iteration/" + analyseId).contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .content(ModelSamples.toJson(multiCriteriaAnalysisStep)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("L'analyse multicri")));

    verify(multiCriteriaAnalysisService, times(1)).increaseMulticriteriaIteration(project.getProjectId(), analyseId);
  }

  @Test
  void shouldIncreaseMultiCriteriaAnalysis_ThrowException() throws Exception {
    Long analyseId = 1L;
    MultiCriteriaStepRequest multiCriteriaAnalysisStep = new MultiCriteriaStepRequest();

    doThrow(new MulticriteriaNotFoundException("Cette analyse multicritères n'existe pas.")).when(multiCriteriaAnalysisService)
      .increaseMulticriteriaIteration(project.getProjectId(), analyseId);

    mockMvc.perform(
        put("/" + project.getProjectId() + "/multi-criteria-analysis/iteration/" + analyseId).contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .content(ModelSamples.toJson(multiCriteriaAnalysisStep)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("404"))
      .andExpect(jsonPath("$.message").value("Cette analyse multicritères n'existe pas."));

    verify(multiCriteriaAnalysisService, times(1)).increaseMulticriteriaIteration(project.getProjectId(), analyseId);
  }

  @Test
  void shouldMarkMultiCriteriaAnalysisAsFinished_Success() throws Exception {
    Long analyseId = 1L;

    mockMvc.perform(
        put("/" + project.getProjectId() + "/multi-criteria-analysis/isFinished/" + analyseId))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("L'analyse multicri")));

    verify(multiCriteriaAnalysisService, times(1)).markMultiCriteriaAnalysisAsFinished(project.getProjectId(), analyseId);
  }

  @Test
  void shouldMarkMultiCriteriaAnalysisAsFinished_NotFound() throws Exception {
    Long analyseId = 1L;
    doThrow(new MulticriteriaNotFoundException("Cette analyse multicritères n'existe pas.")).when(multiCriteriaAnalysisService)
      .markMultiCriteriaAnalysisAsFinished(project.getProjectId(), analyseId);

    mockMvc.perform(
        put("/" + project.getProjectId() + "/multi-criteria-analysis/isFinished/" + analyseId))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  @Test
  void increaseMulticriteriaIteration_whenMulticriteriaDoseNotExists() throws Exception {
    Long analyseId = 1L;
    doThrow(new MulticriteriaNotFoundException("Cette analyse multicritères n'existe pas.")).when(multiCriteriaAnalysisService)
      .increaseMulticriteriaIteration(analyseId);
    MockHttpServletResponse response = mockMvc.perform(put("/multi-criteria-analysis/iteration/" + analyseId))
      .andDo(print())
      .andReturn()
      .getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void getMulticriteriaAnalysisScore() throws Exception {
    MockHttpServletResponse response = mockMvc.perform(get("/multi-criteria-analysis/" + project.getProjectId() + "/score"))
      .andDo(print())
      .andReturn()
      .getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void getMulticriteriaAnalysisScore_whenEscoreQuestionsDoseNotExists() throws Exception {
    when(multiCriteriaAnalysisService.calculateMultiCriteriaAnalysisScore(project.getProjectId())).thenThrow(
      new EscoreException("score n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(get("/multi-criteria-analysis/" + project.getProjectId() + "/score"))
      .andDo(print())
      .andReturn()
      .getResponse();
    Assertions.assertEquals(406, response.getStatus());
  }
}
