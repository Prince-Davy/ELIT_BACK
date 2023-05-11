package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.model.MultiCriteriaStepModel;
import com.elit.outildedecision.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.util.ModelSamples;
import com.elit.outildedecision.util.ProjectPermession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;



@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class MultiCriteriaAnalysisControllerTests {

  private MockMvc mockMvc;
  @Mock
  MultiCriteriaAnalysisService multiCriteriaAnalysisService;
  @Mock
  CustomMethodSecurityExpressionRoot customMethodSecurity;
  @InjectMocks
  MultiCriteriaAnalysisController multiCriteriaAnalysisController;

  private User user;
  private Project project;

  private ProjectUserPermission permission;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(multiCriteriaAnalysisController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

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

  @Test
  void createMultiCriteriaAnalysis_whenMulticriteria_isCreated() throws Exception {
    Long projectId = 1L;
    when(customMethodSecurity.isProjectOwner(projectId)).thenReturn(true);
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
   void shouldUpdateMultiCriteriaAnalysis_Success() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    MultiCriteriaStepModel multiCriteriaAnalysisStep = new MultiCriteriaStepModel();

    when(multiCriteriaAnalysisService.updateMultiCriteriaAnalysis(
      project.getProjectId(), multiCriteriaAnalysisStep)).thenReturn(ResponseEntity.ok().body("Votre Modification sont enregistrées avec succés"));

    mockMvc.perform(put("/api/multi-criteria-analysis/"+project.getProjectId()).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(multiCriteriaAnalysisStep)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Votre Modification sont")));

    verify(multiCriteriaAnalysisService, times(1)).updateMultiCriteriaAnalysis(project.getProjectId(), multiCriteriaAnalysisStep);
  }

  @Test
  @ExtendWith(SpringExtension.class)
  void updateMultiCriteriaAnalysis_whenStepDoseNotExist() throws Exception {
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
  void updateQuestion_Success() throws Exception {
    FormQuestionModel formQuestion = new FormQuestionModel();
    when(multiCriteriaAnalysisService.updateMultiCriteriaQuestion(formQuestion)).thenReturn(ResponseEntity.ok().body("Votre Modification sont enregistrées avec succés"));
    MockHttpServletResponse response = mockMvc.perform(put("/api/multi-criteria-analysis/question").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(formQuestion))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @Test
  void updateQuestion_whenQuestionDoseNotExists() throws Exception {
    FormQuestionModel formQuestion = new FormQuestionModel();
    when(multiCriteriaAnalysisService.updateMultiCriteriaQuestion(formQuestion)).thenThrow(new MulticriteriaNotFoundException("Ce fichier n'existe pas !"));
    MockHttpServletResponse response = mockMvc.perform(put("/api/multi-criteria-analysis/question").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(formQuestion))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void updateQuestion_v2_Success() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    FormQuestionModel formQuestion = new FormQuestionModel();

    when(multiCriteriaAnalysisService.updateMultiCriteriaQuestion(
      project.getProjectId(), formQuestion)).thenThrow(new MulticriteriaNotFoundException("Ce fichier n'existe pas !"));

    mockMvc.perform(put("/api/"+project.getProjectId()+"/multi-criteria-analysis/question").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(formQuestion)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce fichier n'existe pas !")));

    verify(multiCriteriaAnalysisService, times(1)).updateMultiCriteriaQuestion(project.getProjectId(), formQuestion);
  }

  @Test
  void updateQuestion_v2_whenQuestionDoseNotExists() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    FormQuestionModel formQuestion = new FormQuestionModel();

    when(multiCriteriaAnalysisService.updateMultiCriteriaQuestion(
      project.getProjectId(), formQuestion)).thenReturn(ResponseEntity.ok().body("La questions a été bien modifié.."));

    mockMvc.perform(put("/api/"+project.getProjectId()+"/multi-criteria-analysis/question").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(formQuestion)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("La questions")));

    verify(multiCriteriaAnalysisService, times(1)).updateMultiCriteriaQuestion(project.getProjectId(), formQuestion);

  }

  @Test
  void shouldIncreaseMultiCriteriaAnalysis_Success() throws Exception {
    Long analyseId = 1L;
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    MultiCriteriaStepModel multiCriteriaAnalysisStep = new MultiCriteriaStepModel();

    when(multiCriteriaAnalysisService.increaseMulticriteriaIteration(
      project.getProjectId(), analyseId)).thenReturn(ResponseEntity.ok().body("L'analyse multicritères a bien changé d'itération."));

    mockMvc.perform(put("/api/"+project.getProjectId()+"/multi-criteria-analysis/iteration/"+analyseId).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(multiCriteriaAnalysisStep)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("L'analyse multicri")));

    verify(multiCriteriaAnalysisService, times(1)).increaseMulticriteriaIteration(project.getProjectId(),analyseId);
  }

  @Test
  void shouldIncreaseMultiCriteriaAnalysis_ThrowException() throws Exception {
    Long analyseId = 1L;
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    MultiCriteriaStepModel multiCriteriaAnalysisStep = new MultiCriteriaStepModel();

    when(multiCriteriaAnalysisService.increaseMulticriteriaIteration(
      project.getProjectId(), analyseId)).thenThrow(new MulticriteriaNotFoundException("Cette analyse multicritères n'existe pas."));

    doThrow(new MulticriteriaNotFoundException("Cette analyse multicritères n'existe pas."))
      .when(multiCriteriaAnalysisService)
      .increaseMulticriteriaIteration(project.getProjectId(), analyseId);

    mockMvc.perform(put("/api/"+project.getProjectId()+"/multi-criteria-analysis/iteration/"+analyseId)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .content(ModelSamples.toJson(multiCriteriaAnalysisStep)))
      .andDo(print())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("Cette analyse multicritères n'existe pas."));;

    verify(multiCriteriaAnalysisService, times(1)).increaseMulticriteriaIteration(project.getProjectId(),analyseId);
  }

  @Test
  void increaseMulticriteriaIteration_whenMulticriteriaDoseNotExists() throws Exception {
    Long analyseId = 1L;
    when(multiCriteriaAnalysisService.increaseMulticriteriaIteration(analyseId)).thenThrow(new MulticriteriaNotFoundException(
      "Cette analyse multicritères n'existe pas."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/multi-criteria-analysis/iteration/"+analyseId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void getMulticriteriaAnalysisScore() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    MultiCriteriaStepModel multiCriteriaAnalysisStep = new MultiCriteriaStepModel();

    when(multiCriteriaAnalysisService.updateMultiCriteriaAnalysis(
      project.getProjectId(), multiCriteriaAnalysisStep)).thenReturn(ResponseEntity.ok().body("Votre Modification sont enregistrées avec succés"));

    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);
    when(multiCriteriaAnalysisService.calculateMultiCriteriaAnalysisScore(project.getProjectId())).thenReturn(ResponseEntity.ok().body(null));
    MockHttpServletResponse response = mockMvc.perform(get("/api/multi-criteria-analysis/"+project.getProjectId()+"/score")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void getMulticriteriaAnalysisScore_whenEscoreQuestionsDoseNotExists() throws Exception {

    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    when(multiCriteriaAnalysisService.calculateMultiCriteriaAnalysisScore(project.getProjectId())).thenThrow(new EscoreException("score n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(get("/api/multi-criteria-analysis/"+project.getProjectId()+"/score")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(406, response.getStatus());
  }
}


