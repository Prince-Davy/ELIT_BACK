package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.ExecutionPlanException;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.model.ExecutionPlanAnswerModel;
import com.elit.outildedecision.model.ExecutionVoteRequestModel;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.elit.outildedecision.service.implementation.ExecutionPlanServiceImpl;
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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExecutionPlanServiceImplTests {
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
  ExecutionPlanVoteRepo executionPlanVoteRepo;
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
    predictibilityAnalysis= new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(longId);
    predictibilityAnalysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    predictibilityAnalysis.setPredictibilityAnalysisId(longId);
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

  @Test
  void createExecutionPlan_throwRuntimeException() {
    when(projectRepository.findById(longId)).thenReturn(Optional.empty());
    Assertions.assertThrows(RuntimeException.class,() -> executionPlanService.createExecutionPlan(longId));
  }

  @Test
  void createExecutionPlan_Success() {
    List<ExecutionPlanQuestion> executionPlanQuestions = new ArrayList<>();
    when(projectRepository.findById(longId)).thenReturn(Optional.ofNullable(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(longId))
      .thenReturn(multiCriteriaAnalysis);
    when(predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(longId))
      .thenReturn(predictibilityAnalysis);
    ExecutionPlan executionPlanSave =
        ExecutionPlan.builder()
          .predictibilityAnalysis(predictibilityAnalysis)
          .isFinished(false)
          .executionIteration(1)
          .build();
    when(executionPlanRepository.save(executionPlanSave)).thenReturn(executionPlan);
    when(executionPlanQuestionRepository.findAll()).thenReturn(executionPlanQuestions);
    Assertions.assertEquals(HttpStatus.OK,executionPlanService.createExecutionPlan(longId).getStatusCode());
  }
  @Test
  void getQuestionsAndAnswers_throwsRuntimeException() {
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.empty());
    Assertions.assertThrows(RuntimeException.class,() -> executionPlanService.getQuestionsAndAnswers(longId));
  }

  @Test
  void getQuestionsAndAnswers_Success() {
    List<ExecutionPlanAnswer> executionPlanAnswers = new ArrayList<>();
    ExecutionPlanAnswer executionPlanAnswer = new ExecutionPlanAnswer();
    executionPlanAnswer.setAnswer("");
    executionPlanAnswer.setExecutionPlan(executionPlan);
    executionPlanAnswer.setExecutionPlanQuestion(new ExecutionPlanQuestion());
    executionPlanAnswers.add(executionPlanAnswer);
    executionPlan.setExecutionPlanAnswers(executionPlanAnswers);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    Assertions.assertEquals(HttpStatus.OK,executionPlanService.getQuestionsAndAnswers(longId).getStatusCode());
  }

  @Test
  void updateAnswers_Success() {
    List<ExecutionPlanAnswerModel> executionPlanAnswerModels = new ArrayList<>();
    ExecutionPlanAnswer executionPlanAnswer = new ExecutionPlanAnswer();
    executionPlanAnswer.setExecutionPlanAnswerId(1L);
    ExecutionPlanAnswerModel executionPlanAnswerModel = new ExecutionPlanAnswerModel();
    executionPlanAnswerModel.setAnswer("");
    executionPlanAnswerModel.setExecutionPlanQuestionId(1L);
    executionPlanAnswerModels.add(executionPlanAnswerModel);
    when(executionPlanAnswerRepository.findByExecutionPlanIdAndExecutionPlanQuestionId(longId,longId))
      .thenReturn(executionPlanAnswer);
    Assertions.assertEquals(HttpStatus.OK,executionPlanService.updateAnswers(executionPlanAnswerModels,longId).getStatusCode());
  }

  @Test
  void getMethodologies_throwsRuntimeException() {
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
    SecurityContextHolder.setContext(securityContext);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.empty());
    Assertions.assertThrows(RuntimeException.class,() -> executionPlanService.getMethodologies(longId));
  }

  @Test
  void postMethodologiesVote_throwsRuntimeException() {
    ExecutionVoteRequestModel executionVoteRequestModel = new ExecutionVoteRequestModel();
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
    SecurityContextHolder.setContext(securityContext);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.empty());
    Assertions.assertThrows(RuntimeException.class,() -> executionPlanService.postMethodologiesVote(executionVoteRequestModel));
  }

  @Test
  void postMethodologiesVote_throwsVoteAlreadyExistException() throws VoteAlreadyExistException {
    ExecutionVoteRequestModel executionVoteRequestModel = new ExecutionVoteRequestModel();
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("CDP");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    ExecutionPlanVote executionPlanVote = new ExecutionPlanVote();
    executionPlanVote.setExecutionPlan(executionPlan);
    executionPlanVote.setId(1L);
    executionPlanVote.setUser(user);
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
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(projectUserPermissionRepository.findByProjectIdUserID(longId,longId))
      .thenReturn(projectUserPermission);
    when(executionPlanVoteRepo.findVoteByUserIdAndExecutionPlanId(longId,longId))
      .thenReturn(executionPlanVote);
    executionVoteRequestModel.setExecutionPlanId(1L);
    Assertions.assertThrows(VoteAlreadyExistException.class,() -> executionPlanService.postMethodologiesVote(executionVoteRequestModel));
  }

  @Test
  void postMethodologiesVote_Success() throws VoteAlreadyExistException {

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

    ExecutionVoteRequestModel executionVoteRequestModel = new ExecutionVoteRequestModel();
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("CDP");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    ExecutionPlanVote executionPlanVote = new ExecutionPlanVote();
    executionPlanVote.setExecutionPlan(executionPlan);
    executionPlanVote.setId(1L);
    executionPlanVote.setUser(user);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(projectUserPermissionRepository.findByProjectIdUserID(longId,longId))
      .thenReturn(projectUserPermission);
    when(executionPlanVoteRepo.findVoteByUserIdAndExecutionPlanId(longId,longId))
      .thenReturn(null);
    executionVoteRequestModel.setExecutionPlanId(1L);
    executionVoteRequestModel.setMethodId2ForHybrid(1L);
    Assertions.assertEquals(HttpStatus.OK,executionPlanService.postMethodologiesVote(executionVoteRequestModel).getStatusCode());
  }

  @Test
  void getExecutionPlanScore_throwsExecutionPlanNotFoundException() throws Exception {
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
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.empty());
    ExecutionPlanNotFoundException e = Assertions.assertThrows(ExecutionPlanNotFoundException.class, () -> executionPlanService.getExecutionPlanScore(longId));
    Assertions.assertEquals("Plan d'exécution introuvable.", e.getMessage());
  }

  @Test
  void getExecutionPlanScore_throwsUserNotAuthorizedException() throws Exception {
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
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(projectUserPermissionRepository.findByProjectIdUserID(longId,longId)).thenReturn(null);
    Assertions.assertThrows(UserNotAuthorizedException.class, () -> executionPlanService.getExecutionPlanScore(longId));
  }

  @Test
  void getExecutionPlanScore_RuntimeException() throws Exception {
    Methodology methodology = new Methodology();
    methodology.setMethodologyId(1L);
    methodology.setName("");
    List<Method> listMethods = new ArrayList<>();
    predictibilityAnalysis.setMethodology(methodology);
    methodology.setMethods(listMethods);
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
    List<ExecutionPlanVote> votesList = new ArrayList<>();
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(projectUserPermissionRepository.findByProjectIdUserID(longId,longId)).thenReturn(projectUserPermission);
    when(executionPlanVoteRepo.findAllVotesByExecutionPlanId(longId))
      .thenReturn(votesList);
    Assertions.assertThrows(RuntimeException.class, () -> executionPlanService.getExecutionPlanScore(longId));
  }

  @Test
  void getMethodologies_Success() throws Exception {
    Methodology methodology = new Methodology();
    methodology.setMethodologyId(1L);
    methodology.setName("");
    List<Method> listMethods = new ArrayList<>();
    methodology.setMethods(listMethods);
    predictibilityAnalysis.setMethodology(methodology);
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
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
    when(projectUserPermissionRepository.findByProjectIdUserID(longId,longId))
      .thenReturn(projectUserPermission);
    Assertions.assertEquals(HttpStatus.OK,executionPlanService.getMethodologies(longId).getStatusCode());
  }

  @Test
  void putIsFinishedExecutionPlan_throwsExecutionPlanNotFoundException() {
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.empty());
    Assertions.assertThrows(ExecutionPlanNotFoundException.class,() -> executionPlanService.putIsFinishedExecutionPlan(longId));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"},username = "user@user.com")
  void putIsFinishedExecutionPlan_throwsUserNotAuthorizedException() throws UserNotAuthorizedException, ExecutionPlanNotFoundException, ExecutionPlanException {

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

    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
     SecurityContextHolder.setContext(securityContext);
    Assertions.assertThrows(UserNotAuthorizedException.class,()->executionPlanService.putIsFinishedExecutionPlan(longId));
  }

  @Test
  void putIsFinishedExecutionPlan_Success() throws UserNotAuthorizedException, ExecutionPlanNotFoundException, ExecutionPlanException {
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
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
    SecurityContextHolder.setContext(securityContext);
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission("CDP");
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    project.setProjectUserPermissions(projectUserPermissionList);
    when(projectUserPermissionRepository.findByProjectIdUserID(longId,longId))
      .thenReturn(projectUserPermission);
    Assertions.assertEquals(HttpStatus.OK,executionPlanService.putIsFinishedExecutionPlan(longId).getStatusCode());
  }

  @Test
  void putIsFinishedExecutionPlan_ExecutionPlanException() throws UserNotAuthorizedException, ExecutionPlanNotFoundException, ExecutionPlanException {
    executionPlan.setIsFinished(true);
    when(executionPlanRepository.findById(longId)).thenReturn(Optional.of(executionPlan));
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
    when(projectUserPermissionRepository.findByProjectIdUserID(longId,longId))
      .thenReturn(projectUserPermission);
    Assertions.assertThrows(ExecutionPlanException.class,() -> executionPlanService.putIsFinishedExecutionPlan(longId));
  }
}
