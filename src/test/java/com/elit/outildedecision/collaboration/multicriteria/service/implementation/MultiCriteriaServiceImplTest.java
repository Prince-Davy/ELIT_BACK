package com.elit.outildedecision.collaboration.multicriteria.service.implementation;

import com.elit.outildedecision.collaboration.brainstorming.entity.Brainstorming;
import com.elit.outildedecision.collaboration.executionplan.service.ExecutionPlanService;
import com.elit.outildedecision.collaboration.multicriteria.calculate.MultiCriteriaScoreCalculator;
import com.elit.outildedecision.collaboration.multicriteria.dto.MulticriteriaAnalysisResponse;
import com.elit.outildedecision.collaboration.multicriteria.mapper.MulticriteriaMapper;
import com.elit.outildedecision.collaboration.multicriteria.entity.Criteria;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormQuestion;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.collaboration.multicriteria.entity.StepWeight;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.collaboration.multicriteria.exception.EscoreException;
import com.elit.outildedecision.collaboration.multicriteria.exception.FormValidationException;
import com.elit.outildedecision.collaboration.multicriteria.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.exception.StepFormNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.dto.CriteriaModel;
import com.elit.outildedecision.collaboration.multicriteria.dto.FormQuestionModel;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepRequest;
import com.elit.outildedecision.collaboration.multicriteria.repository.CriteriaRepository;
import com.elit.outildedecision.collaboration.multicriteria.repository.FormQuestionRepository;
import com.elit.outildedecision.collaboration.multicriteria.repository.FormStepRepository;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.collaboration.multicriteria.repository.StepWeightRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MultiCriteriaServiceImplTest {

  PredictibilityAnalysis predictibilityAnalysis;
  MultiCriteriaAnalysis multiCriteriaAnalysis;
  List<FormStep> formSteps;
  FormStep formStep;
  Project project;
  Brainstorming brainstorming;
  User user;
  Criteria criteria;
  FormQuestionModel formQuestionModel;

  MultiCriteriaStepRequest multiCriteriaAnalysisStep;
  FormQuestion formQuestion;

  @InjectMocks
  private MultiCriteriaServiceImpl multiCriteriaServiceImpl;
  @Mock
  private MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  private FormStepRepository formStepRepo;
  @Mock
  private FormQuestionRepository formQuestionRepository;
  @Mock
  private CriteriaRepository criteriaRepository;
  @Mock
  private StepWeightRepository stepWeightRepo;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ProjectUserPermissionRepository projectUserPermissionRepository;
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private MultiCriteriaScoreCalculator multiCriteriaScoreCalculator;
  @Mock
  private MulticriteriaMapper multicriteriaMapper;
  @Mock
  private ExecutionPlanService executionPlanService;

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
    formQuestion.setAnswerText("Une réponse de test");
    formStep.getFormQuestions().add(formQuestion);
    formQuestion.setCriterias(new HashSet<>());

    criteria = new Criteria();
    criteria.setCriteriaName("criteria name");
    criteria.setCriteriaValue(1);
    formQuestion.getCriterias().add(criteria);

    multiCriteriaAnalysisStep = new MultiCriteriaStepRequest();

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
      "com/elit/outildedecision/collaboration/multicriteria/multi-criteria-form-schema.json");
    ReflectionTestUtils.setField(multiCriteriaServiceImpl, "resourceLoader", new DefaultResourceLoader());

  }

  @Test
  public void createMulticriteriaAnalysisFormTest() throws IOException, ParseException, ProjectNotFoundException {

    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(anyLong())).thenReturn(multiCriteriaAnalysis);
    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.save(any())).thenReturn(multiCriteriaAnalysis);

    multiCriteriaServiceImpl.createMulticriteriaAnalysisForm(1L);

    verify(multiCriteriaAnalysisRepository, times(1)).save(any());
  }

  @Test
  public void buildProjectAnalysesShouldMapAnalysisAndSkipExecutionPlanWhenNoPredictibility() throws Exception {
    // The single analysis carries no predictibility analysis: the embedded model stays null and the
    // execution-plan service is never queried.
    when(multicriteriaMapper.toFormStepResponses(any())).thenReturn(List.of());

    List<MulticriteriaAnalysisResponse> responses = multiCriteriaServiceImpl.buildProjectAnalyses(project);

    Assertions.assertEquals(1, responses.size());
    verify(multicriteriaMapper, times(1)).toFormStepResponses(any());
    verify(executionPlanService, never()).getExecutionPlanScore(anyLong());
  }

  @Test
  public void createMulticriteriaAnalysisFormTestAnalyseEnCours() throws IOException, ParseException, ProjectNotFoundException {
    multiCriteriaAnalysis.setIsFinished(false);
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(anyLong())).thenReturn(multiCriteriaAnalysis);
    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

    multiCriteriaServiceImpl.createMulticriteriaAnalysisForm(1L);

    // An analysis is already in progress: the call is a no-op, no new analysis is persisted.
    verify(multiCriteriaAnalysisRepository, times(0)).save(any());
  }

  @Test
  public void createMulticriteriaAnalysisFormTestExistingNull() throws IOException, ParseException, ProjectNotFoundException {
    multiCriteriaAnalysis = null;
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(anyLong())).thenReturn(multiCriteriaAnalysis);
    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.save(any())).thenReturn(new MultiCriteriaAnalysis());

    multiCriteriaServiceImpl.createMulticriteriaAnalysisForm(1L);

    verify(multiCriteriaAnalysisRepository, times(1)).save(any());
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
    verify(multiCriteriaAnalysisRepository, never()).save(any());
  }

  @Test
  public void shouldMarkMultiCriteriaAnalysisAsFinishedWhenAnalysisBelongsToProject()
      throws MulticriteriaNotFoundException {
    when(multiCriteriaAnalysisRepository.findById(1L)).thenReturn(Optional.of(multiCriteriaAnalysis));

    multiCriteriaServiceImpl.markMultiCriteriaAnalysisAsFinished(1L, 1L);

    Assertions.assertTrue(multiCriteriaAnalysis.getIsFinished());
    verify(multiCriteriaAnalysisRepository, times(1)).save(multiCriteriaAnalysis);
  }

  @Test
  public void shouldThrowNotFoundWhenMarkingAnalysisOfAnotherProjectAsFinished() {
    when(multiCriteriaAnalysisRepository.findById(1L)).thenReturn(Optional.of(multiCriteriaAnalysis));

    Assertions.assertThrows(MulticriteriaNotFoundException.class,
        () -> multiCriteriaServiceImpl.markMultiCriteriaAnalysisAsFinished(999L, 1L));

    verify(multiCriteriaAnalysisRepository, never()).save(any());
  }

  @Test
  public void shouldThrowNotFoundWhenMarkingUnknownAnalysisAsFinished() {
    when(multiCriteriaAnalysisRepository.findById(1L)).thenReturn(Optional.empty());

    Assertions.assertThrows(MulticriteriaNotFoundException.class,
        () -> multiCriteriaServiceImpl.markMultiCriteriaAnalysisAsFinished(1L, 1L));
  }

  @Test
  public void shouldThrowNotFoundWhenNoMultiCriteriaAnalysisForScore() {
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(1L)).thenReturn(null);

    Assertions.assertThrows(MulticriteriaNotFoundException.class,
        () -> multiCriteriaServiceImpl.calculateMultiCriteriaAnalysisScore(1L));
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


    User localUser = new User();
    localUser.setUserId(1l);

    multiCriteriaServiceImpl.increaseMulticriteriaIteration(1l,1l);
    verify(multiCriteriaAnalysisRepository, times(1)).save(any());

  }
}
