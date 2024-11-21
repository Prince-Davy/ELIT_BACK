package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.elit.outildedecision.service.implementation.PredictibilityAnalysisServiceImpl;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictibilityAnalysisServiceImplTests {
  @Mock
  ProjectRepository projectRepository;
  @Mock
  MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  ProjectUserPermissionRepository projectUserPermissionRepository;
  @InjectMocks
  PredictibilityAnalysisServiceImpl predictibilityAnalysisService;
  Project project;
  MultiCriteriaAnalysis multiCriteriaAnalysis;
  PredictibilityAnalysis predictibilityAnalysis;
  FormStep formStep;
  List<FormStep> formSteps = new ArrayList<>();
  User user;
  Long LongId = 1L;
  String chosenMethodology;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");
    formStep = new FormStep();
    formStep.setStepId(LongId);
    formStep.setEScore(1F);
    project = new Project();
    project.setProjectId(LongId);
    multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setMultiCriteriaAnalysisId(LongId);
    multiCriteriaAnalysis.setProject(project);
    predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(LongId);
    predictibilityAnalysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    predictibilityAnalysis.setChosenMethodology("Agile");
    formStep.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    formSteps.add(formStep);
    multiCriteriaAnalysis.setFormSteps(formSteps);

    chosenMethodology = "{\"chosenMethodology\": \"Agile\"}";
  }

  @Test
  void createPredictibilityForm_RuntimeException() {
    assertThrows(ProjectNotFoundException.class, () -> predictibilityAnalysisService.createPredictibilityForm(LongId));
  }

  @Test
  void createPredictibilityForm_FORBIDDEN() throws ProjectNotFoundException {
    multiCriteriaAnalysis.setIsFinished(false);
    when(projectRepository.findById(LongId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(LongId))
      .thenReturn(multiCriteriaAnalysis);
    assertEquals(HttpStatus.FORBIDDEN, predictibilityAnalysisService.createPredictibilityForm(LongId).getStatusCode());
  }

  @Test
  void createPredictibilityForm_Success() throws ProjectNotFoundException {
    multiCriteriaAnalysis.setIsFinished(true);
    when(projectRepository.findById(LongId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(LongId))
      .thenReturn(multiCriteriaAnalysis);
    when(predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(LongId))
      .thenReturn(predictibilityAnalysis);
    assertEquals(HttpStatus.OK, predictibilityAnalysisService.createPredictibilityForm(LongId).getStatusCode());
  }

  @Test
  void createPredictibilityForm_SuccessNull() throws ProjectNotFoundException {
    multiCriteriaAnalysis.setIsFinished(true);
    when(projectRepository.findById(LongId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(LongId))
      .thenReturn(multiCriteriaAnalysis);
    when(predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(LongId))
      .thenReturn(null);
    when(predictibilityAnalysisRepository.save(PredictibilityAnalysis.builder()
      .multiCriteriaAnalysis(multiCriteriaAnalysis)
      .isFinished(false)
      .predictibilityAnalysisIteration(1)
      .build())).thenReturn(PredictibilityAnalysis.builder()
      .multiCriteriaAnalysis(multiCriteriaAnalysis)
      .isFinished(false)
      .predictibilityAnalysisIteration(1)
      .build());
    assertEquals(HttpStatus.OK, predictibilityAnalysisService.createPredictibilityForm(LongId).getStatusCode());
  }

  @Test
  void addFinalChosenMethodology() throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    when(projectRepository.findById(LongId)).thenReturn(Optional.of(project));
    when(predictibilityAnalysisRepository
      .findById(LongId))
      .thenReturn(Optional.ofNullable(predictibilityAnalysis));
    when(predictibilityAnalysisRepository.save(predictibilityAnalysis))
      .thenReturn(predictibilityAnalysis);
    assertEquals(HttpStatus.OK, predictibilityAnalysisService.addFinalChosenMethodology(chosenMethodology, LongId, LongId).getStatusCode());
  }

  @Test
  void addFinalChosenMethodology_whenProjectDosntExist() {
    when(projectRepository.findById(LongId)).thenReturn(Optional.empty());
    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, () -> predictibilityAnalysisService.addFinalChosenMethodology(chosenMethodology, LongId, LongId));
    Assertions.assertEquals("Ce projet n'existe pas", exception.getMessage());
  }

  @Test
  void addFinalChosenMethodology_whenPredictibilityAnalysisDosntExist() {
    when(projectRepository.findById(LongId)).thenReturn(Optional.of(project));
    when(predictibilityAnalysisRepository
      .findById(LongId))
      .thenReturn(Optional.empty());
    PredictibilityAnalysisNotFoundException exception = Assertions.assertThrows(PredictibilityAnalysisNotFoundException.class, () -> predictibilityAnalysisService.addFinalChosenMethodology(chosenMethodology, LongId, LongId));
    Assertions.assertEquals("Cette analyse n'existe pas", exception.getMessage());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_PredictibilityAnalysisNotFoundException() {
    assertThrows(PredictibilityAnalysisNotFoundException.class, () -> predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(LongId));
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_PredictibilityAnalysisAlreadyUpdated() {
    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("CDP");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    when(predictibilityAnalysisRepository.findById(LongId)).thenReturn(Optional.of(predictibilityAnalysis));
    when(projectUserPermissionRepository.findByProjectIdUserID(LongId, LongId)).thenReturn(projectUserPermission);
    assertThrows(PredictibilityAnalysisAlreadyUpdated.class, () -> predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(LongId));
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_UserNotAuthorizedException() {
    predictibilityAnalysis.setIsFinished(false);
    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    when(predictibilityAnalysisRepository.findById(LongId)).thenReturn(Optional.of(predictibilityAnalysis));
    when(projectUserPermissionRepository.findByProjectIdUserID(LongId, LongId)).thenReturn(projectUserPermission);
    assertThrows(UserNotAuthorizedException.class, () -> predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(LongId));
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_Success() throws Exception {
    predictibilityAnalysis.setIsFinished(false);
    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("CDP");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    when(predictibilityAnalysisRepository.findById(LongId)).thenReturn(Optional.of(predictibilityAnalysis));
    when(projectUserPermissionRepository.findByProjectIdUserID(LongId, LongId)).thenReturn(projectUserPermission);
    assertEquals(HttpStatus.OK, predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(LongId).getStatusCode());
  }


}
