package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.service.implementation.BrainstormingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static com.elit.outildedecision.util.Constants.BRAINSTORMING_DOESNT_EXIST;
import static com.elit.outildedecision.util.Constants.ANALYSIS_DOESNT_EXIST;


@ExtendWith(MockitoExtension.class)
class BrainstormingServiceImplTest {

  @Mock
  BrainstormingRepository brainstormingRepository;
  @Mock
  FormQuestionRepository formQuestionRepository;
  @Mock
  ProjectUserPermissionRepository projectUserPermissionRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  ProjectRepository projectRepository;
  @Mock
  MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @InjectMocks
  BrainstormingServiceImpl brainstormingService;

  PredictibilityAnalysis predictibilityAnalysis;
  MultiCriteriaAnalysis multiCriteriaAnalysis;
  List<FormStep> formSteps;
  FormStep formStep;
  Project project;
  Brainstorming brainstorming;


  @BeforeEach
  void setUp() {
    brainstorming = new Brainstorming();
    brainstorming.setBrainstormingId(1L);
    brainstorming.setBrainstormingText("brainstorming");
    project = new Project();
    project.setProjectId(1L);
    formStep = new FormStep();
    formStep.setStepId(1L);
    formSteps = new ArrayList<>();
    multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setFormSteps(formSteps);
    multiCriteriaAnalysis.setProject(project);
    predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(1L);
    predictibilityAnalysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
  }

  @Test
  void validateOrRejectBrainstorming_ThrowBrainstormingNotFoundException() {
    String json = "{\"isConfirmed\": true}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(json, headers);
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.empty());
    BrainstormingNotFoundException p = Assertions.assertThrows(BrainstormingNotFoundException.class, ()-> brainstormingService.validateOrRejectBrainstorming(1L,entity));
    Assertions.assertEquals(BRAINSTORMING_DOESNT_EXIST, p.getMessage());
  }

  @Test
  void testValidateOrRejectBrainstorming() throws Exception {

    // Create a Brainstorming object
    Brainstorming brainstorming = new Brainstorming();
    brainstorming.setBrainstormingId(1L);
    brainstorming.setIsChecked(false);

    // Set up the repository to return the Brainstorming object when findById is called
    when(brainstormingRepository.findById(1L)).thenReturn(Optional.of(brainstorming));

    // Create an HttpEntity containing a JSON object with a "isConfirmed" field set to true
    String json = "{\"isConfirmed\": true}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(json, headers);

    // Call the method under test
    ResponseEntity<String> response = brainstormingService.validateOrRejectBrainstorming(1L, entity);

    // Assert that the Brainstorming object was updated correctly
    assertEquals(true, brainstorming.getIsChecked());

    // Assert that the response is as expected
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Le commentaire a bien été enregistré.", response.getBody());
  }

  @Test
  void getBrainstorming_throwPredictibilityAnalysisNotFoundException() {
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.empty());
    PredictibilityAnalysisNotFoundException p = Assertions.assertThrows(PredictibilityAnalysisNotFoundException.class, ()-> brainstormingService.getBrainstorming(1L));
    Assertions.assertEquals(ANALYSIS_DOESNT_EXIST, p.getMessage());
  }

  @Test
  void getBrainstorming_Success() throws PredictibilityAnalysisNotFoundException {
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(predictibilityAnalysis));
    assertEquals(HttpStatus.OK,brainstormingService.getBrainstorming(1L).getStatusCode());
  }

  @Test
  void getBrainstormingAbstract_Success() throws PredictibilityAnalysisNotFoundException {
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(predictibilityAnalysis));
    assertEquals(HttpStatus.OK,brainstormingService.getBrainstormingAbstract(1L).getStatusCode());
  }

  @Test
  void deleteBrainstorming_BrainstormingNotFoundException() {
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.empty());
    BrainstormingNotFoundException p = Assertions.assertThrows(BrainstormingNotFoundException.class, ()-> brainstormingService.deleteBrainstorming(1L));
    Assertions.assertEquals(BRAINSTORMING_DOESNT_EXIST, p.getMessage());
  }

  @Test
  void deleteBrainstorming_Success() throws Exception {
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.of(brainstorming));
    assertEquals(HttpStatus.OK,brainstormingService.deleteBrainstorming(1L).getStatusCode());
  }
}
