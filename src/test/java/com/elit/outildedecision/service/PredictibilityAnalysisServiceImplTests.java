package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.service.implementation.PredictibilityAnalysisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictibilityAnalysisServiceImplTests {
  @Mock ProjectRepository projectRepository;
  @Mock MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock VoteRepository voteRepository;
  @Mock UserRepository userRepository;
  @Mock ProjectUserPermissionRepository projectUserPermissionRepository;
  @Mock MethodologyRepository methodologyRepository;
  @Mock PredictibilityResultsRepository predictibilityResultsRepository;
  @InjectMocks
  PredictibilityAnalysisServiceImpl predictibilityAnalysisService;
  Project project;
  MultiCriteriaAnalysis multiCriteriaAnalysis;
  PredictibilityAnalysis predictibilityAnalysis;
  FormStep formStep;
  List<FormStep> formSteps = new ArrayList<>();
  User user;
  Long LONG_ID = 1L;
  @BeforeEach
  void setUp(){
    user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");
    formStep = new FormStep();
    formStep.setStepId(LONG_ID);
    formStep.setEScore(1F);
    project = new Project();
    project.setProjectId(LONG_ID);
    multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setMultiCriteriaAnalysisId(LONG_ID);
    multiCriteriaAnalysis.setProject(project);
    predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(LONG_ID);
    predictibilityAnalysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    formStep.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    formSteps.add(formStep);
    multiCriteriaAnalysis.setFormSteps(formSteps);
  }

  @Test
  void createPredictibilityForm_RuntimeException() {
    assertThrows(RuntimeException.class, () -> predictibilityAnalysisService.createPredictibilityForm(LONG_ID));
  }

  @Test
  void createPredictibilityForm_FORBIDDEN() {
    multiCriteriaAnalysis.setIsFinished(false);
    when(projectRepository.findById(LONG_ID)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(LONG_ID))
      .thenReturn(multiCriteriaAnalysis);
    assertEquals(HttpStatus.FORBIDDEN,predictibilityAnalysisService.createPredictibilityForm(LONG_ID).getStatusCode());
  }

  @Test
  void createPredictibilityForm_Success() {
    multiCriteriaAnalysis.setIsFinished(true);
    when(projectRepository.findById(LONG_ID)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(LONG_ID))
      .thenReturn(multiCriteriaAnalysis);
    when(predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(LONG_ID))
      .thenReturn(predictibilityAnalysis);
    assertEquals(HttpStatus.OK,predictibilityAnalysisService.createPredictibilityForm(LONG_ID).getStatusCode());
  }

  @Test
  void createPredictibilityForm_SuccessNull() {
    multiCriteriaAnalysis.setIsFinished(true);
    when(projectRepository.findById(LONG_ID)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(LONG_ID))
      .thenReturn(multiCriteriaAnalysis);
    when(predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(LONG_ID))
      .thenReturn(null);
    when(predictibilityAnalysisRepository.save(            PredictibilityAnalysis.builder()
      .multiCriteriaAnalysis(multiCriteriaAnalysis)
      .isFinished(false)
      .predictibilityAnalysisIteration(1)
      .build())).thenReturn(            PredictibilityAnalysis.builder()
      .multiCriteriaAnalysis(multiCriteriaAnalysis)
      .isFinished(false)
      .predictibilityAnalysisIteration(1)
      .build());
    assertEquals(HttpStatus.OK,predictibilityAnalysisService.createPredictibilityForm(LONG_ID).getStatusCode());
  }

  @Test
  void calculatePredictibilityAnalysisScore_PredictibilityAnalysisNotFoundException() {
    assertThrows(PredictibilityAnalysisNotFoundException.class, () -> predictibilityAnalysisService.calculatePredictibilityAnalysisScore(LONG_ID));
  }

  @Test
  void calculatePredictibilityAnalysisScore_Success() throws Exception {
    when(predictibilityAnalysisRepository.findById(LONG_ID)).thenReturn(Optional.ofNullable(predictibilityAnalysis));
    assertEquals(HttpStatus.OK,predictibilityAnalysisService.calculatePredictibilityAnalysisScore(LONG_ID).getStatusCode());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_PredictibilityAnalysisNotFoundException() {
    assertThrows(PredictibilityAnalysisNotFoundException.class, () -> predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(LONG_ID));
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_PredictibilityAnalysisAlreadyUpdated() {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("CDP");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    when(predictibilityAnalysisRepository.findById(LONG_ID)).thenReturn(Optional.of(predictibilityAnalysis));
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(user);
    when(projectUserPermissionRepository.findByProjectIdUserID(LONG_ID,LONG_ID)).thenReturn(projectUserPermission);
    assertThrows(PredictibilityAnalysisAlreadyUpdated.class, () -> predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(LONG_ID));
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_UserNotAuthorizedException() {
    predictibilityAnalysis.setIsFinished(false);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    when(predictibilityAnalysisRepository.findById(LONG_ID)).thenReturn(Optional.of(predictibilityAnalysis));
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(user);
    when(projectUserPermissionRepository.findByProjectIdUserID(LONG_ID,LONG_ID)).thenReturn(projectUserPermission);
    assertThrows(UserNotAuthorizedException.class, () -> predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(LONG_ID));
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_Success() throws Exception {
    predictibilityAnalysis.setIsFinished(false);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("CDP");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    when(predictibilityAnalysisRepository.findById(LONG_ID)).thenReturn(Optional.of(predictibilityAnalysis));
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(user);
    when(projectUserPermissionRepository.findByProjectIdUserID(LONG_ID,LONG_ID)).thenReturn(projectUserPermission);
    assertEquals(HttpStatus.OK, predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(LONG_ID).getStatusCode());
  }

  @Test
  void increasePredictAnalysisIterationNum_PredictibilityAnalysisNotFoundException() {
    assertThrows(PredictibilityAnalysisNotFoundException.class, () -> predictibilityAnalysisService.increasePredictAnalysisIterationNum(LONG_ID));
  }

  @Test
  void increasePredictAnalysisIterationNum_UserNotAuthorizedException() {
    predictibilityAnalysis.setIsFinished(false);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    when(predictibilityAnalysisRepository.findById(LONG_ID)).thenReturn(Optional.of(predictibilityAnalysis));
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(user);
    when(projectUserPermissionRepository.findByProjectIdUserID(LONG_ID,LONG_ID)).thenReturn(null);
    assertThrows(UserNotAuthorizedException.class, () -> predictibilityAnalysisService.increasePredictAnalysisIterationNum(LONG_ID));
  }

  @Test
  void increasePredictAnalysisIterationNum_Success() throws Exception {
    predictibilityAnalysis.setIsFinished(false);
    predictibilityAnalysis.setPredictibilityAnalysisIteration(0);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("CDP");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    when(predictibilityAnalysisRepository.findById(LONG_ID)).thenReturn(Optional.of(predictibilityAnalysis));
    when(userRepository.findByUserEmail("test@test.com")).thenReturn(user);
    when(projectUserPermissionRepository.findByProjectIdUserID(LONG_ID,LONG_ID)).thenReturn(projectUserPermission);
    assertEquals(HttpStatus.OK, predictibilityAnalysisService.increasePredictAnalysisIterationNum(LONG_ID).getStatusCode());
  }
}
