package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.exception.ExistingBrainstormingException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.model.BrainstormingModel;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.service.implementation.BrainstormingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
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

  User user;


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
    user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");
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

  @Test
  void createBrainstorming_PredictibilityError() throws ExistingBrainstormingException, PredictibilityAnalysisNotFoundException {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(1L);
    brainstormingModel.setPredictibilityAnalysisId(1L);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.empty());
    Assertions.assertThrows(PredictibilityAnalysisNotFoundException.class, ()-> brainstormingService.createBrainstorming(brainstormingModel));
  }

  @Test
  void createBrainstorming_ExistingError() throws ExistingBrainstormingException, PredictibilityAnalysisNotFoundException {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(0L);
    brainstormingModel.setPredictibilityAnalysisId(0L);
    brainstormingModel.setQuestionId(0L);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(predictibilityAnalysis));
    when(brainstormingRepository.findByUserQuestionPredictibilityId(anyLong(),anyLong(),anyLong()))
      .thenReturn(brainstorming);
    Assertions.assertThrows(ExistingBrainstormingException.class, ()-> brainstormingService.createBrainstorming(brainstormingModel));
  }

  @Test
  void createBrainstorming_Success() throws ExistingBrainstormingException, PredictibilityAnalysisNotFoundException {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(0L);
    brainstormingModel.setPredictibilityAnalysisId(0L);
    brainstormingModel.setQuestionId(0L);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(predictibilityAnalysis));
    when(brainstormingRepository.findByUserQuestionPredictibilityId(1L,0L,1L))
      .thenReturn(null);
    assertEquals(HttpStatus.OK,brainstormingService.createBrainstorming(brainstormingModel).getStatusCode());
  }

  @Test
  void updateBrainstorming_BrainstormingNotFoundException() throws Exception {
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(0L);
    brainstormingModel.setPredictibilityAnalysisId(0L);
    brainstormingModel.setQuestionId(0L);
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(BrainstormingNotFoundException.class,() -> brainstormingService.updateBrainstorming(brainstormingModel));
  }

  @Test
  void updateBrainstorming_Success() throws BrainstormingNotFoundException {
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.of(brainstorming));
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(user);
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(0L);
    brainstormingModel.setPredictibilityAnalysisId(0L);
    brainstormingModel.setQuestionId(0L);
    brainstorming.setUser(user);
    assertEquals(HttpStatus.OK,brainstormingService.updateBrainstorming(brainstormingModel).getStatusCode());
  }

  @Test
  void updateBrainstorming_Forbidden() throws BrainstormingNotFoundException {
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.of(brainstorming));
    User userNotTheSame = new User();
    userNotTheSame.setUserId(10L);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(userNotTheSame);
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(0L);
    brainstormingModel.setPredictibilityAnalysisId(0L);
    brainstormingModel.setQuestionId(0L);
    brainstorming.setUser(user);
    assertEquals(HttpStatus.FORBIDDEN,brainstormingService.updateBrainstorming(brainstormingModel).getStatusCode());
  }
}
