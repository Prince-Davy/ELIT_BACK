package com.elit.outildedecision.collaboration.executionplan.service;

import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanAnswerRequest;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanFormResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteRequest;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlan;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanVote;
import com.elit.outildedecision.collaboration.executionplan.exception.ExecutionPlanException;
import com.elit.outildedecision.collaboration.executionplan.exception.VoteAlreadyExistException;
import com.elit.outildedecision.collaboration.executionplan.calculate.ExecutionPlanScoreCalculator;
import com.elit.outildedecision.collaboration.executionplan.mapper.ExecutionPlanMapper;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanAnswerRepository;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanQuestionRepository;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanRepository;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanVoteRepository;
import com.elit.outildedecision.collaboration.executionplan.service.implementation.ExecutionPlanServiceImpl;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.shared.methodology.entity.Method;
import com.elit.outildedecision.shared.methodology.entity.Methodology;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.methodology.repository.MethodRepository;
import com.elit.outildedecision.shared.methodology.repository.MethodologyRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityResultsRepository;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.exception.NotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExecutionPlanServiceImplTest {

  @Mock
  ExecutionPlanAnswerRepository executionPlanAnswerRepository;
  @Mock
  ExecutionPlanQuestionRepository executionPlanQuestionRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  ProjectUserPermissionRepository projectUserPermissionRepository;
  @Mock
  MethodRepository methodRepository;
  @Mock
  ExecutionPlanVoteRepository executionPlanVoteRepository;
  @Mock
  MethodologyRepository methodologyRepository;
  @Mock
  ProjectRepository projectRepository;
  @Mock
  PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock
  MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  PredictibilityResultsRepository predictibilityResultsRepository;
  @Mock
  ExecutionPlanRepository executionPlanRepository;
  @Mock
  ExecutionPlanMapper executionPlanMapper;
  @Spy
  ExecutionPlanScoreCalculator executionPlanScoreCalculator = new ExecutionPlanScoreCalculator();
  @InjectMocks
  ExecutionPlanServiceImpl executionPlanService;

  Long longId;
  Project project;
  MultiCriteriaAnalysis multiCriteriaAnalysis;
  ExecutionPlan executionPlan;
  PredictibilityAnalysis predictibilityAnalysis;
  User user;

  @BeforeEach
  void setUp() {
    longId = 1L;
    project = new Project();
    project.setProjectId(longId);
    multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setProject(project);
    multiCriteriaAnalysis.setIsFinished(false);
    multiCriteriaAnalysis.setMultiCriteriaAnalysisId(longId);
    predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(longId);
    predictibilityAnalysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    executionPlan = new ExecutionPlan();
    executionPlan.setExecutionPlanId(longId);
    executionPlan.setPredictibilityAnalysis(predictibilityAnalysis);
    executionPlan.setExecutionIteration(0);
    executionPlan.setIsFinished(false);
    predictibilityAnalysis.setExecutionPlan(executionPlan);
    multiCriteriaAnalysis.setPredictibilityAnalysis(predictibilityAnalysis);
    user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");
    user.setUsername("test@test.com");
  }

  private void authenticate() {
    User authUser = new User();
    authUser.setUserEmail("test@test.fr");
    authUser.setUsername("test@test.fr");
    authUser.setUserId(1L);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(authUser));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(authUser);
  }

  private ProjectUserPermission ownerPermission() {
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("CDP");
    return projectUserPermission;
  }

  @Test
  void createExecutionPlan_throwRuntimeException() {
    when(projectRepository.findById(longId)).thenReturn(Optional.empty());
    Assertions.assertThrows(RuntimeException.class, () -> executionPlanService.createExecutionPlan(longId));
  }

  @Test
  void createExecutionPlan_Success() {
    predictibilityAnalysis.setIsFinished(true);
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(longId))
        .thenReturn(multiCriteriaAnalysis);
    when(predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(longId))
        .thenReturn(predictibilityAnalysis);
    when(executionPlanRepository.findByLastIterationAndPredictibilityId(longId)).thenReturn(null);
    when(executionPlanRepository.save(any(ExecutionPlan.class))).thenReturn(executionPlan);
    when(executionPlanQuestionRepository.findAll()).thenReturn(new ArrayList<>());
    Assertions.assertEquals(longId, executionPlanService.createExecutionPlan(longId));
  }

  @Test
  void createExecutionPlan_throwsNotFoundWhenNoMultiCriteriaAnalysis() {
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(longId))
        .thenReturn(null);
    Assertions.assertThrows(NotFoundException.class,
        () -> executionPlanService.createExecutionPlan(longId));
  }

  @Test
  void createExecutionPlan_throwsConflictWhenNoPredictibility() {
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(longId))
        .thenReturn(multiCriteriaAnalysis);
    // predictibility lookup left unstubbed -> returns null (precondition not met)
    Assertions.assertThrows(ConflictException.class,
        () -> executionPlanService.createExecutionPlan(longId));
  }

  @Test
  void createExecutionPlan_throwsConflictWhenPredictibilityNotFinalised() {
    predictibilityAnalysis.setIsFinished(false);
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(longId))
        .thenReturn(multiCriteriaAnalysis);
    when(predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(longId))
        .thenReturn(predictibilityAnalysis);
    Assertions.assertThrows(ConflictException.class,
        () -> executionPlanService.createExecutionPlan(longId));
  }

  @Test
  void getQuestionsAndAnswers_Success() throws Exception {
    executionPlan.setExecutionPlanAnswers(new ArrayList<>());
    List<ExecutionPlanFormResponse> expected = List.of(new ExecutionPlanFormResponse());
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(executionPlanMapper.toFormResponses(any())).thenReturn(expected);
    Assertions.assertEquals(expected, executionPlanService.getQuestionsAndAnswers(longId, longId));
  }

  @Test
  void getQuestionsAndAnswers_throwsExecutionPlanNotFoundException() {
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.empty());
    Assertions.assertThrows(ExecutionPlanNotFoundException.class,
        () -> executionPlanService.getQuestionsAndAnswers(longId, longId));
  }

  @Test
  void getQuestionsAndAnswers_throwsProjectNotFoundExceptionWhenMismatch() {
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    Assertions.assertThrows(ProjectNotFoundException.class,
        () -> executionPlanService.getQuestionsAndAnswers(2L, longId));
  }

  @Test
  void updateAnswers_Success() throws Exception {
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    ExecutionPlanAnswerRequest answerRequest = new ExecutionPlanAnswerRequest();
    answerRequest.setAnswer("");
    answerRequest.setExecutionPlanQuestionId(1L);
    when(executionPlanAnswerRepository.findByExecutionPlanIdAndExecutionPlanQuestionId(longId, longId))
        .thenReturn(null);
    Assertions.assertTrue(
        executionPlanService.updateAnswers(longId, longId, List.of(answerRequest)).isEmpty());
  }

  @Test
  void getMethodologies_Success() throws Exception {
    authenticate();
    Methodology methodology = new Methodology();
    methodology.setMethodologyId(1L);
    methodology.setName("");
    methodology.setMethods(new ArrayList<>());
    predictibilityAnalysis.setMethodology(methodology);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(projectUserPermissionRepository.findByProjectIdUserID(longId, longId))
        .thenReturn(ownerPermission());
    Assertions.assertTrue(executionPlanService.getMethodologies(longId, longId).isEmpty());
  }

  @Test
  void postMethodologiesVote_throwsVoteAlreadyExistException() {
    authenticate();
    ExecutionVoteRequest request = new ExecutionVoteRequest();
    request.setExecutionPlanId(1L);
    ExecutionPlanVote existingVote = new ExecutionPlanVote();
    existingVote.setId(1L);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(executionPlanVoteRepository.findVoteByUserIdAndExecutionPlanId(longId, longId))
        .thenReturn(existingVote);
    Assertions.assertThrows(VoteAlreadyExistException.class,
        () -> executionPlanService.postMethodologiesVote(longId, request));
  }

  @Test
  void postMethodologiesVote_Success() throws Exception {
    authenticate();
    ExecutionVoteRequest request = new ExecutionVoteRequest();
    request.setExecutionPlanId(1L);
    request.setMethodId2ForHybrid(1L);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(executionPlanVoteRepository.findVoteByUserIdAndExecutionPlanId(longId, longId))
        .thenReturn(null);
    Assertions.assertEquals("Le vote a bien été enregistré.",
        executionPlanService.postMethodologiesVote(longId, request));
  }

  @Test
  void getExecutionPlanScore_throwsExecutionPlanNotFoundException() {
    authenticate();
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.empty());
    ExecutionPlanNotFoundException e = Assertions.assertThrows(ExecutionPlanNotFoundException.class,
        () -> executionPlanService.getExecutionPlanScore(longId));
    Assertions.assertEquals("Plan d'exécution introuvable.", e.getMessage());
  }

  @Test
  void getExecutionPlanScore_throwsUserNotAuthorizedException() {
    authenticate();
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(projectUserPermissionRepository.findByProjectIdUserID(longId, longId)).thenReturn(null);
    Assertions.assertThrows(UserNotAuthorizedException.class,
        () -> executionPlanService.getExecutionPlanScore(longId));
  }

  @Test
  void getExecutionPlanScore_throwsRuntimeExceptionWhenNoMethods() {
    authenticate();
    Methodology methodology = new Methodology();
    methodology.setMethodologyId(1L);
    methodology.setName("");
    predictibilityAnalysis.setMethodology(methodology);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(projectUserPermissionRepository.findByProjectIdUserID(longId, longId))
        .thenReturn(ownerPermission());
    when(executionPlanVoteRepository.findAllVotesByExecutionPlanId(longId)).thenReturn(new ArrayList<>());
    when(methodRepository.findAll()).thenReturn(new ArrayList<Method>());
    Assertions.assertThrows(RuntimeException.class,
        () -> executionPlanService.getExecutionPlanScore(longId));
  }

  @Test
  void putIsFinishedExecutionPlan_throwsExecutionPlanNotFoundException() {
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.empty());
    Assertions.assertThrows(ExecutionPlanNotFoundException.class,
        () -> executionPlanService.putIsFinishedExecutionPlan(longId, longId));
  }

  @Test
  void putIsFinishedExecutionPlan_Success() throws Exception {
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    Assertions.assertTrue(executionPlanService.putIsFinishedExecutionPlan(longId, longId));
  }

  @Test
  void putIsFinishedExecutionPlan_throwsExecutionPlanException() {
    executionPlan.setIsFinished(true);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    Assertions.assertThrows(ExecutionPlanException.class,
        () -> executionPlanService.putIsFinishedExecutionPlan(longId, longId));
  }
}
