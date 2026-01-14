package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.handler.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.model.PredictibilityScoreModel;
import com.elit.outildedecision.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.service.PredictibilityAnalysisService;
import com.elit.outildedecision.util.ProjectPermession;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Unit test for PredictibilityAnalysisController
 */
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class PredictibilityAnalysisControllerTest {

  private MockMvc mockMvc;
  @Mock
   private PredictibilityAnalysisService predictibilityAnalysisService;
  @Mock
  CustomMethodSecurityExpressionRoot customMethodSecurity;
  @InjectMocks
  PredictibilityAnalysisController predictibilityAnalysisController;

  private User user;
  private Project project;

  private ProjectUserPermission permission;

  Long projectId, analyseId;
  String chosenMethodology;

  @BeforeEach
  public void setup() throws JsonProcessingException {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(predictibilityAnalysisController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

    user = new User();
    user.setUserEmail("user@user.com");
    project = new Project();
    project.setProjectId(1L);
    permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(ProjectPermession.CONTRIBUTER.toString());
    user.setUserProjectPermissions(List.of(permission));

    projectId = 1L;
    analyseId = 1L;
    chosenMethodology = "{\"chosenMethodology\": \"Agile\"}";
  }

  @Test
  void createPredictibilityAnalysis() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.createPredictibilityForm(projectId)).thenReturn(ResponseEntity.ok().body(1L));
    MockHttpServletResponse response = mockMvc.perform(post("/predictibility-analysis/"+projectId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void createPredictibilityAnalysis_whenProjectDoseNotExist() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.createPredictibilityForm(projectId)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas"));

    mockMvc.perform(post("/predictibility-analysis/"+projectId))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce projet n'existe pas")));
  }

  @Test
  void createPredictibilityAnalysis_old_whenPredictibilityAnalysisIsNotFinished() throws Exception {
    when(predictibilityAnalysisService.createPredictibilityForm(projectId)).thenReturn(ResponseEntity.status(HttpStatus.FORBIDDEN).body(-1L));
    MockHttpServletResponse response = mockMvc.perform(post("/predictibility-analysis/"+projectId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(403, response.getStatus());
  }

  @Test
  void addFinalChosenMethodology() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.addFinalChosenMethodology(chosenMethodology, projectId, analyseId)).thenReturn(ResponseEntity.ok().body("La méthodologie finale choisie est : Agile"));
    MockHttpServletResponse response = mockMvc.perform(post("/"+projectId+"/predictibility/"+analyseId+"/chosenMethodology").content(chosenMethodology)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void addFinalChosenMethodology_whenBadRequest() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.addFinalChosenMethodology(chosenMethodology, projectId, analyseId)).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors du traitement de la méthodologie choisie."));
    MockHttpServletResponse response = mockMvc.perform(post("/"+projectId+"/predictibility/"+analyseId+"/chosenMethodology").content(chosenMethodology)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(400, response.getStatus());
  }

  @Test
  void addFinalChosenMethodology_whenPredictibilityDoseNotExist() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.addFinalChosenMethodology(chosenMethodology, projectId, analyseId)).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).body("L'analyse de prédictibilté n'existe pas encore pour ce projet."));
    MockHttpServletResponse response = mockMvc.perform(post("/"+projectId+"/predictibility/"+analyseId+"/chosenMethodology").content(chosenMethodology)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(409, response.getStatus());
  }

  @Test
  void shouldGetPredictabilityAnalysisScore_success() throws Exception {
    PredictibilityScoreModel predictibilityResult = new PredictibilityScoreModel();
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.calculatePredictibilityAnalysisScore(projectId, analyseId)).thenReturn(ResponseEntity.ok().body(predictibilityResult));
    MockHttpServletResponse response = mockMvc.perform(get("/"+projectId+"/predictibility/"+analyseId+"/score")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void shouldGetPredictabilityAnalysisScore_ThrowAnalysisNotFoundException() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.calculatePredictibilityAnalysisScore(projectId, analyseId)).thenThrow(new PredictibilityAnalysisNotFoundException("Cet analyse n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(get("/"+projectId+"/predictibility/"+analyseId+"/score")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void shouldGetPredictabilityAnalysisScore_ThrowProjectNotFoundException() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.calculatePredictibilityAnalysisScore(projectId, analyseId)).thenThrow(new ProjectNotFoundException("Le project dans le path et celui de l'analyse ne correspondent pas"));
    MockHttpServletResponse response = mockMvc.perform(get("/"+projectId+"/predictibility/"+analyseId+"/score")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }


  @Test
  void putIsFinishedPredictibilityAnalysis() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when( predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(projectId, analyseId)).thenReturn(ResponseEntity.ok()
      .body("L'analyse de prévisibilité a bien été enregistré comme terminée."));
    MockHttpServletResponse response = mockMvc.perform(put("/"+projectId+"/predictibility/isFinished/"+analyseId))
      .andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis__whenPredictibilityDoseNotExist() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when( predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(projectId ,analyseId)).thenThrow(new PredictibilityAnalysisNotFoundException(
      "Cette analyse de prévisibilité n'existe pas."));
    MockHttpServletResponse response = mockMvc.perform(put("/"+projectId+"/predictibility/isFinished/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis__whenProjectNotFound() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when( predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(projectId ,analyseId)).thenThrow(new ProjectNotFoundException(
      "Le project dans le path et celui de l'analyse ne correspondent pas"));
    MockHttpServletResponse response = mockMvc.perform(put(
    "/"+projectId+"/predictibility/isFinished/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void increasePredictibilityAnalyseIterationNumber() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.increasePredictAnalysisIterationNum(projectId, analyseId)).thenReturn(ResponseEntity.ok()
      .body("L'analyse de prévisibilité a rentre dans une nouvelle iteration."));
    MockHttpServletResponse response = mockMvc.perform(put("/" + projectId+"/predictibility/iteration/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void increasePredictibilityAnalyseIterationNumber_whenPredictibilityDoseNotExist() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.increasePredictAnalysisIterationNum(projectId, analyseId)).thenThrow( new PredictibilityAnalysisNotFoundException(
      "Cette analyse de prévisibilité n'existe pas."));
    MockHttpServletResponse response = mockMvc.perform(put("/"+projectId+"predictibility/iteration/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void increasePredictibilityAnalyseIterationNumber_whenPrprojectNotFound() throws Exception {
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
    when(predictibilityAnalysisService.increasePredictAnalysisIterationNum(projectId, analyseId)).thenThrow( new ProjectNotFoundException(
      "Le project dans le path et celui de l'analyse ne correspondent pas"));
    MockHttpServletResponse response = mockMvc.perform(put("/"+projectId+"predictibility/iteration/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }
}
