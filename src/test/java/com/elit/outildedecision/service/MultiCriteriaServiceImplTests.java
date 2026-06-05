package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Brainstorming;
import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.StepWeight;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.CriteriaModel;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.model.MultiCriteriaStepModel;
import com.elit.outildedecision.repository.CriteriaRepository;
import com.elit.outildedecision.repository.FormQuestionRepository;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.StepWeightRepo;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.elit.outildedecision.service.implementation.MultiCriteriaServiceImpl;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MultiCriteriaServiceImplTests {

  PredictibilityAnalysis predictibilityAnalysis;
  MultiCriteriaAnalysis multiCriteriaAnalysis;
  List<FormStep> formSteps;
  FormStep formStep;
  Project project;
  Brainstorming brainstorming;
  User user;
  Criteria criteria;
  FormQuestionModel formQuestionModel;

  MultiCriteriaStepModel multiCriteriaAnalysisStep;
  FormQuestion formQuestion;

  @InjectMocks
  private MultiCriteriaServiceImpl multiCriteriaServiceImpl;
  @Mock
  private MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  private FormStepRepo formStepRepo;
  @Mock
  private FormQuestionRepository formQuestionRepository;
  @Mock
  private CriteriaRepository criteriaRepository;
  @Mock
  private StepWeightRepo stepWeightRepo;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ProjectUserPermissionRepository projectUserPermissionRepository;
  @Mock
  private ProjectRepository projectRepository;

  @BeforeEach
  void setUp() {
    brainstorming = new Brainstorming();
    brainstorming.setBrainstormingId(1L);
    brainstorming.setBrainstormingText("brainstorming");
    project = new Project();
    project.setProjectId(1L);
    project.setMultiCriteriaAnalysisList(new ArrayList<>());

    formStep = new FormStep();
    formStep.setStepId(1L);
    formStep.setStepName("step 1");
    formStep.setFormQuestions(new HashSet<>());

    formSteps = new ArrayList<>();
    multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setFormSteps(formSteps);
    multiCriteriaAnalysis.setProject(project);
    multiCriteriaAnalysis.setIsFinished(true);
    multiCriteriaAnalysis.setMultiCriteriaAnalysisIteration(0);

    project.getMultiCriteriaAnalysisList().add(multiCriteriaAnalysis);
    predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(1L);
    predictibilityAnalysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");

    formQuestion = new FormQuestion();
    formQuestion.setQuestionRef("QREF");
    formStep.getFormQuestions().add(formQuestion);
    formQuestion.setCriterias(new HashSet<>());

    criteria = new Criteria();
    criteria.setCriteriaName("criteria name");
    criteria.setCriteriaValue(1);
    formQuestion.getCriterias().add(criteria);

    multiCriteriaAnalysisStep = new MultiCriteriaStepModel();

    multiCriteriaAnalysisStep.setStepId(1l);
    multiCriteriaAnalysisStep.setStepWeights(new HashSet<>());
    StepWeight stepWeight = new StepWeight();
    stepWeight.setWeightValue(1l);
    stepWeight.setCriteriaName("criteria");
    stepWeight.setCriteriaDescription("description");

    multiCriteriaAnalysisStep.getStepWeights().add(stepWeight);
    multiCriteriaAnalysisStep.setStepName("multi");
    multiCriteriaAnalysisStep.setEScore(1.25f);
    multiCriteriaAnalysisStep.setFormQuestions(new HashSet<>());
    formQuestion.setFormStep(formStep);
    formStep.setStepWeights(new HashSet<>());
    formStep.getStepWeights().add(stepWeight);

    formStep.setMultiCriteriaAnalysis(multiCriteriaAnalysis);

    formQuestionModel = new FormQuestionModel();
    formQuestionModel.setQuestionId(1l);
    formQuestionModel.setAnswerText("answer");
    formQuestionModel.setCriterias(new ArrayList<>());
    CriteriaModel criteriaModel = new CriteriaModel();
    criteriaModel.setCriteriaId(1l);
    criteriaModel.setCriteriaValue(1);
    formQuestionModel.getCriterias().add(criteriaModel);

    multiCriteriaAnalysisStep.getFormQuestions().add(formQuestion);

    ReflectionTestUtils.setField(multiCriteriaServiceImpl, "questionsJsonFile",
      "com/elit/outildedecision/service/implementation/MultiCriteriaAnalysisFormSchema.Json");
    ReflectionTestUtils.setField(multiCriteriaServiceImpl, "resourceLoader", new DefaultResourceLoader());

  }

  @Test
  public void createMulticriteriaAnalysisFormTest() throws IOException, ParseException, ProjectNotFoundException {

    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(anyLong())).thenReturn(multiCriteriaAnalysis);
    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    ResponseEntity<String> response = multiCriteriaServiceImpl.createMulticriteriaAnalysisForm(1L);

    assertEquals("Votre Analyse multicritère est crée avec succés", response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());

  }

  @Test
  public void createMulticriteriaAnalysisFormTestAnalyseEnCours() throws IOException, ParseException, ProjectNotFoundException {
    multiCriteriaAnalysis.setIsFinished(false);
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(anyLong())).thenReturn(multiCriteriaAnalysis);
    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    ResponseEntity<String> response = multiCriteriaServiceImpl.createMulticriteriaAnalysisForm(1L);

    assertEquals("Une Analyse multicritère est déjà en cours pour ce projet", response.getBody());
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

  }

  @Test
  public void createMulticriteriaAnalysisFormTestExistingNull() throws IOException, ParseException, ProjectNotFoundException {
    multiCriteriaAnalysis = null;
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(anyLong())).thenReturn(multiCriteriaAnalysis);
    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    ResponseEntity<String> response = multiCriteriaServiceImpl.createMulticriteriaAnalysisForm(1L);

    assertEquals("Votre Analyse multicritère est crée avec succés", response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());

  }

  @Test
  public void createMulticriteriaAnalysisFormTestExistingExistingProject() {
    Assertions.assertThrows(ProjectNotFoundException.class, () -> multiCriteriaServiceImpl.createMulticriteriaAnalysisForm(1L));
  }

  @Test
  public void getMulticriteriaAnalysisException() {
    Assertions.assertThrows(ProjectNotFoundException.class, () -> multiCriteriaServiceImpl.getMulticriteriaAnalysis(1L));
  }

  @Test
  public void updateMultiCriteriaAnalysisTest() throws StepFormNotFoundException, FormValidationException {

    when(formStepRepo.findById(1l)).thenReturn(Optional.of(formStep));

    multiCriteriaServiceImpl.updateMultiCriteriaAnalysis(1L, multiCriteriaAnalysisStep);

    verify(stepWeightRepo, times(1)).save(any());
    verify(criteriaRepository, times(1)).save(any());
    verify(formQuestionRepository, times(1)).save(any());

  }

  @Test
  public void updateMultiCriteriaQuestionTest() throws StepFormNotFoundException, MulticriteriaNotFoundException {

    when(formQuestionRepository.findById(1l)).thenReturn(Optional.of(formQuestion));
    when(formStepRepo.findById(formQuestionModel.getStepId())).thenReturn(Optional.of(formStep));
    when(criteriaRepository.getById(1l)).thenReturn(criteria);

    multiCriteriaServiceImpl.updateMultiCriteriaQuestion(1L, formQuestionModel);

    verify(formQuestionRepository, times(1)).save(any());
    verify(criteriaRepository, times(1)).save(any());

  }

  @Test
  public void calculateMultiCriteriaAnalysisScoreTest() throws QuestionsNotFoundException, EscoreException {
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(1l)).thenReturn(multiCriteriaAnalysis);

    multiCriteriaServiceImpl.calculateMultiCriteriaAnalysisScore(1L);
    verify(multiCriteriaAnalysisRepository, times(1)).save(any());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"},username = "user@user.com")
  public void increaseMulticriteriaIterationTest() throws MulticriteriaNotFoundException, UserNotAuthorizedException {

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(multiCriteriaAnalysisRepository.findById(1l)).thenReturn(Optional.ofNullable(multiCriteriaAnalysis));
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setPermission("CDP");

    when(projectUserPermissionRepository.findByProjectIdUserID(any(), any())).
      thenReturn(projectUserPermission);


    multiCriteriaServiceImpl.increaseMulticriteriaIteration(1l);
    verify(multiCriteriaAnalysisRepository, times(1)).save(any());

  }

  @Test
  public void increaseMulticriteriaIterationAnalyseTest() throws MulticriteriaNotFoundException, UserNotAuthorizedException {
    when(multiCriteriaAnalysisRepository.findById(1l)).thenReturn(Optional.ofNullable(multiCriteriaAnalysis));
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setPermission("CDP");


    User user = new User();
    user.setUserId(1l);

    multiCriteriaServiceImpl.increaseMulticriteriaIteration(1l,1l);
    verify(multiCriteriaAnalysisRepository, times(1)).save(any());

  }
}
