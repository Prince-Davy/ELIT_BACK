package com.elit.outildedecision.collaboration.brainstorming.service;

import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingRequest;
import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingResponse;
import com.elit.outildedecision.collaboration.brainstorming.dto.FormStepModelForBrainstorming;
import com.elit.outildedecision.collaboration.brainstorming.entity.Brainstorming;
import com.elit.outildedecision.collaboration.brainstorming.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.collaboration.brainstorming.exception.ExistingBrainstormingException;
import com.elit.outildedecision.collaboration.brainstorming.mapper.BrainstormingMapper;
import com.elit.outildedecision.collaboration.brainstorming.repository.BrainstormingRepository;
import com.elit.outildedecision.collaboration.brainstorming.service.implementation.BrainstormingServiceImpl;
import com.elit.outildedecision.collaboration.multicriteria.dto.CriteriaModel;
import com.elit.outildedecision.collaboration.multicriteria.dto.FormQuestionModel;
import com.elit.outildedecision.collaboration.multicriteria.entity.Criteria;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormQuestion;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.repository.FormQuestionRepository;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.exception.ForbiddenException;
import com.elit.outildedecision.shared.exception.NotFoundException;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.elit.outildedecision.shared.util.Constants.ANALYSIS_DOESNT_EXIST;
import static com.elit.outildedecision.shared.util.Constants.BRAINSTORMING_DOESNT_EXIST;
import static com.elit.outildedecision.shared.util.Constants.BRAINSTORMING_SAVED;
import static com.elit.outildedecision.shared.util.Constants.PROJECT_DOESNT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrainstormingServiceImplTest {

  private static final Long PROJECT_ID = 1L;
  private static final Long OTHER_PROJECT_ID = 999L;
  private static final Long ANALYSIS_ID = 1L;
  private static final Long BRAINSTORMING_ID = 1L;

  @Mock
  private BrainstormingRepository brainstormingRepository;
  @Mock
  private FormQuestionRepository formQuestionRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock
  private ProjectUserPermissionRepository projectUserPermissionRepository;
  @Mock
  private BrainstormingMapper brainstormingMapper;
  @InjectMocks
  private BrainstormingServiceImpl brainstormingService;

  private User author;

  @BeforeEach
  void setUp() {
    author = sampleUser(1L);
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  // ---------------------------------------------------------------------------
  // createBrainstorming
  // ---------------------------------------------------------------------------

  @Test
  void shouldCreateBrainstormingWhenRequestIsValid() {
    // given
    authenticateAs(author);
    PredictibilityAnalysis analysis = buildAnalysisOwnedByProject(PROJECT_ID);
    analysis.getMultiCriteriaAnalysis().setMultiCriteriaAnalysisId(ANALYSIS_ID);
    when(userRepository.findByUserEmail(anyString())).thenReturn(author);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(analysis));
    when(brainstormingRepository.findByUserQuestionPredictibilityId(anyLong(), anyLong(), anyLong()))
        .thenReturn(null);
    when(formQuestionRepository.findById(anyLong()))
        .thenReturn(Optional.of(questionForAnalysis(ANALYSIS_ID)));
    when(brainstormingMapper.toEntity(any(BrainstormingRequest.class))).thenReturn(new Brainstorming());
    Brainstorming created = new Brainstorming();
    created.setBrainstormingId(77L);
    when(brainstormingRepository.save(any(Brainstorming.class))).thenReturn(created);

    // when
    Long result = brainstormingService.createBrainstorming(PROJECT_ID, sampleRequest());

    // then
    assertThat(result).isEqualTo(77L);
  }

  @Test
  void shouldThrowNotFoundWhenCreatingForUnknownQuestion() {
    // given
    authenticateAs(author);
    PredictibilityAnalysis analysis = buildAnalysisOwnedByProject(PROJECT_ID);
    analysis.getMultiCriteriaAnalysis().setMultiCriteriaAnalysisId(ANALYSIS_ID);
    when(userRepository.findByUserEmail(anyString())).thenReturn(author);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(analysis));
    when(brainstormingRepository.findByUserQuestionPredictibilityId(anyLong(), anyLong(), anyLong()))
        .thenReturn(null);
    when(formQuestionRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(NotFoundException.class)
        .isThrownBy(() -> brainstormingService.createBrainstorming(PROJECT_ID, sampleRequest()));
  }

  @Test
  void shouldThrowBadRequestWhenQuestionBelongsToAnotherAnalysis() {
    // given
    authenticateAs(author);
    PredictibilityAnalysis analysis = buildAnalysisOwnedByProject(PROJECT_ID);
    analysis.getMultiCriteriaAnalysis().setMultiCriteriaAnalysisId(ANALYSIS_ID);
    when(userRepository.findByUserEmail(anyString())).thenReturn(author);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(analysis));
    when(brainstormingRepository.findByUserQuestionPredictibilityId(anyLong(), anyLong(), anyLong()))
        .thenReturn(null);
    when(formQuestionRepository.findById(anyLong()))
        .thenReturn(Optional.of(questionForAnalysis(42L)));

    // when / then
    assertThatExceptionOfType(BadRequestException.class)
        .isThrownBy(() -> brainstormingService.createBrainstorming(PROJECT_ID, sampleRequest()));
  }

  @Test
  void shouldThrowPredictibilityNotFoundWhenCreatingForUnknownAnalysis() {
    // given
    authenticateAs(author);
    when(userRepository.findByUserEmail(anyString())).thenReturn(author);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(PredictibilityAnalysisNotFoundException.class)
        .isThrownBy(() -> brainstormingService.createBrainstorming(PROJECT_ID, sampleRequest()))
        .withMessage(ANALYSIS_DOESNT_EXIST);
  }

  @Test
  void shouldThrowProjectNotFoundWhenCreatingForAnalysisOfAnotherProject() {
    // given
    authenticateAs(author);
    when(userRepository.findByUserEmail(anyString())).thenReturn(author);
    when(predictibilityAnalysisRepository.findById(anyLong()))
        .thenReturn(Optional.of(buildAnalysisOwnedByProject(PROJECT_ID)));

    // when / then
    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> brainstormingService.createBrainstorming(OTHER_PROJECT_ID, sampleRequest()))
        .withMessage(PROJECT_DOESNT_EXIST);
  }

  @Test
  void shouldThrowExistingBrainstormingWhenUserAlreadyCommented() {
    // given
    authenticateAs(author);
    when(userRepository.findByUserEmail(anyString())).thenReturn(author);
    when(predictibilityAnalysisRepository.findById(anyLong()))
        .thenReturn(Optional.of(buildAnalysisOwnedByProject(PROJECT_ID)));
    when(brainstormingRepository.findByUserQuestionPredictibilityId(anyLong(), anyLong(), anyLong()))
        .thenReturn(new Brainstorming());

    // when / then
    assertThatExceptionOfType(ExistingBrainstormingException.class)
        .isThrownBy(() -> brainstormingService.createBrainstorming(PROJECT_ID, sampleRequest()));
  }

  // ---------------------------------------------------------------------------
  // updateBrainstorming
  // ---------------------------------------------------------------------------

  @Test
  void shouldUpdateBrainstormingWhenUserIsAuthor() throws Exception {
    // given
    Brainstorming brainstorming = brainstormingOwnedByProject(PROJECT_ID);
    brainstorming.setUser(author);
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.of(brainstorming));
    authenticateAs(author);
    when(userRepository.findByUserEmail(anyString())).thenReturn(author);

    // when
    String result = brainstormingService.updateBrainstorming(PROJECT_ID, sampleRequest());

    // then
    assertThat(result).isEqualTo(BRAINSTORMING_SAVED);
  }

  @Test
  void shouldThrowForbiddenWhenUpdatingBrainstormingOfAnotherUser() throws Exception {
    // given
    Brainstorming brainstorming = brainstormingOwnedByProject(PROJECT_ID);
    brainstorming.setUser(author);
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.of(brainstorming));
    authenticateAs(author);
    when(userRepository.findByUserEmail(anyString())).thenReturn(sampleUser(2L));

    // when / then
    assertThatExceptionOfType(ForbiddenException.class)
        .isThrownBy(() -> brainstormingService.updateBrainstorming(PROJECT_ID, sampleRequest()))
        .withMessage("L'utilisateur enregistré ne correspond pas à l'utilisateur du brainstorming");
  }

  @Test
  void shouldThrowBrainstormingNotFoundWhenUpdatingUnknownBrainstorming() {
    // given
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(BrainstormingNotFoundException.class)
        .isThrownBy(() -> brainstormingService.updateBrainstorming(PROJECT_ID, sampleRequest()))
        .withMessage(BRAINSTORMING_DOESNT_EXIST);
  }

  @Test
  void shouldThrowProjectNotFoundWhenUpdatingBrainstormingOfAnotherProject() {
    // given
    when(brainstormingRepository.findById(anyLong()))
        .thenReturn(Optional.of(brainstormingOwnedByProject(PROJECT_ID)));

    // when / then
    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> brainstormingService.updateBrainstorming(OTHER_PROJECT_ID, sampleRequest()))
        .withMessage(PROJECT_DOESNT_EXIST);
  }

  // ---------------------------------------------------------------------------
  // deleteBrainstorming
  // ---------------------------------------------------------------------------

  @Test
  void shouldDeleteBrainstormingWhenItBelongsToProject() throws Exception {
    // given
    when(brainstormingRepository.findById(anyLong()))
        .thenReturn(Optional.of(brainstormingOwnedByProject(PROJECT_ID)));

    // when
    String result = brainstormingService.deleteBrainstorming(PROJECT_ID, BRAINSTORMING_ID);

    // then
    assertThat(result).isEqualTo("Le commentaire du brainstorming a bien été supprimé.");
  }

  @Test
  void shouldThrowBrainstormingNotFoundWhenDeletingUnknownBrainstorming() {
    // given
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(BrainstormingNotFoundException.class)
        .isThrownBy(() -> brainstormingService.deleteBrainstorming(PROJECT_ID, BRAINSTORMING_ID))
        .withMessage(BRAINSTORMING_DOESNT_EXIST);
  }

  @Test
  void shouldThrowProjectNotFoundWhenDeletingBrainstormingOfAnotherProject() {
    // given
    when(brainstormingRepository.findById(anyLong()))
        .thenReturn(Optional.of(brainstormingOwnedByProject(PROJECT_ID)));

    // when / then
    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> brainstormingService.deleteBrainstorming(OTHER_PROJECT_ID, BRAINSTORMING_ID))
        .withMessage(PROJECT_DOESNT_EXIST);
  }

  // ---------------------------------------------------------------------------
  // validateOrRejectBrainstorming
  // ---------------------------------------------------------------------------

  @Test
  void shouldMarkBrainstormingCheckedWhenValidated() throws Exception {
    // given
    Brainstorming brainstorming = brainstormingOwnedByProject(PROJECT_ID);
    brainstorming.setIsChecked(false);
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.of(brainstorming));

    // when
    String result =
        brainstormingService.validateOrRejectBrainstorming(PROJECT_ID, BRAINSTORMING_ID, confirmedBody());

    // then
    assertThat(result).isEqualTo("Le commentaire a bien été enregistré.");
    assertThat(brainstorming.getIsChecked()).isTrue();
  }

  @Test
  void shouldThrowBrainstormingNotFoundWhenValidatingUnknownBrainstorming() {
    // given
    when(brainstormingRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(BrainstormingNotFoundException.class)
        .isThrownBy(() ->
            brainstormingService.validateOrRejectBrainstorming(PROJECT_ID, BRAINSTORMING_ID, confirmedBody()))
        .withMessage(BRAINSTORMING_DOESNT_EXIST);
  }

  @Test
  void shouldThrowProjectNotFoundWhenValidatingBrainstormingOfAnotherProject() {
    // given
    when(brainstormingRepository.findById(anyLong()))
        .thenReturn(Optional.of(brainstormingOwnedByProject(PROJECT_ID)));

    // when / then
    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() ->
            brainstormingService.validateOrRejectBrainstorming(OTHER_PROJECT_ID, BRAINSTORMING_ID, confirmedBody()))
        .withMessage(PROJECT_DOESNT_EXIST);
  }

  // ---------------------------------------------------------------------------
  // getBrainstorming / getBrainstormingAbstract
  // ---------------------------------------------------------------------------

  @Test
  void shouldReturnEmptyListWhenAnalysisHasNoFormSteps() throws Exception {
    // given
    when(predictibilityAnalysisRepository.findById(anyLong()))
        .thenReturn(Optional.of(buildAnalysisOwnedByProject(PROJECT_ID)));

    // when
    List<FormStepModelForBrainstorming> result =
        brainstormingService.getBrainstorming(PROJECT_ID, ANALYSIS_ID);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldThrowPredictibilityNotFoundWhenGettingBrainstormingForUnknownAnalysis() {
    // given
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(PredictibilityAnalysisNotFoundException.class)
        .isThrownBy(() -> brainstormingService.getBrainstorming(PROJECT_ID, ANALYSIS_ID))
        .withMessage(ANALYSIS_DOESNT_EXIST);
  }

  @Test
  void shouldThrowProjectNotFoundWhenGettingBrainstormingForAnotherProject() {
    // given
    when(predictibilityAnalysisRepository.findById(anyLong()))
        .thenReturn(Optional.of(buildAnalysisOwnedByProject(PROJECT_ID)));

    // when / then
    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> brainstormingService.getBrainstorming(OTHER_PROJECT_ID, ANALYSIS_ID))
        .withMessage(PROJECT_DOESNT_EXIST);
  }

  @Test
  void shouldReturnAbstractWhenAnalysisExists() throws Exception {
    // given
    when(predictibilityAnalysisRepository.findById(anyLong()))
        .thenReturn(Optional.of(buildAnalysisOwnedByProject(PROJECT_ID)));

    // when
    List<FormStepModelForBrainstorming> result =
        brainstormingService.getBrainstormingAbstract(PROJECT_ID, ANALYSIS_ID);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnAbstractByAnalysisIdWhenAnalysisExists() throws Exception {
    // given
    when(predictibilityAnalysisRepository.findById(anyLong()))
        .thenReturn(Optional.of(buildAnalysisOwnedByProject(PROJECT_ID)));

    // when
    List<FormStepModelForBrainstorming> result =
        brainstormingService.getBrainstormingAbstractByAnalysisId(ANALYSIS_ID);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldThrowPredictibilityNotFoundWhenAbstractByAnalysisIdUnknown() {
    // given
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(PredictibilityAnalysisNotFoundException.class)
        .isThrownBy(() -> brainstormingService.getBrainstormingAbstractByAnalysisId(ANALYSIS_ID))
        .withMessage(ANALYSIS_DOESNT_EXIST);
  }

  @Test
  void shouldMapWeakStepsOnlyWithTheirQuestionsCriteriaAndBrainstormings() throws Exception {
    // given : one weak step (eScore < 0.5) carrying a question, a criteria and a brainstorming,
    // plus one strong step (eScore >= 0.5) that must be filtered out.
    PredictibilityAnalysis analysis = buildAnalysisOwnedByProject(PROJECT_ID);
    MultiCriteriaAnalysis mca = analysis.getMultiCriteriaAnalysis();
    mca.setMultiCriteriaAnalysisId(7L);

    Brainstorming brainstorming = new Brainstorming();
    brainstorming.setBrainstormingId(40L);
    brainstorming.setUser(sampleUser(50L));

    Criteria criteria = new Criteria();
    criteria.setCriteriaId(30L);
    criteria.setCriteriaName("delais");
    criteria.setCriteriaValue(5);

    FormQuestion question = new FormQuestion();
    question.setQuestionId(20L);
    question.setQuestionText("question ?");
    question.setCriterias(Set.of(criteria));
    question.setBrainstormings(List.of(brainstorming));

    FormStep weakStep = new FormStep();
    weakStep.setStepId(10L);
    weakStep.setStepName("weak");
    weakStep.setStepRef(100L);
    weakStep.setEScore(0.3f);
    weakStep.setMultiCriteriaAnalysis(mca);
    weakStep.setFormQuestions(Set.of(question));

    FormStep strongStep = new FormStep();
    strongStep.setStepId(11L);
    strongStep.setEScore(0.8f);
    strongStep.setMultiCriteriaAnalysis(mca);
    strongStep.setFormQuestions(Set.of());

    mca.setFormSteps(new ArrayList<>(List.of(weakStep, strongStep)));

    BrainstormingResponse mappedResponse =
        BrainstormingResponse.builder().brainstormingId(40L).role("CONTRIBUTER").build();

    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(analysis));
    when(userRepository.getById(50L)).thenReturn(sampleUser(50L));
    when(projectUserPermissionRepository.findByProjectIdUserID(PROJECT_ID, 50L))
        .thenReturn(permissionWith("CONTRIBUTER"));
    when(brainstormingMapper.toResponse(eq(brainstorming), eq(20L), eq("CONTRIBUTER")))
        .thenReturn(mappedResponse);

    // when
    List<FormStepModelForBrainstorming> result =
        brainstormingService.getBrainstorming(PROJECT_ID, ANALYSIS_ID);

    // then : the strong step is filtered out, the weak one is fully mapped
    assertThat(result).hasSize(1);
    FormStepModelForBrainstorming step = result.get(0);
    assertThat(step.getStepId()).isEqualTo(10L);
    assertThat(step.getMultiCriteriaAnalysisId()).isEqualTo(7L);
    assertThat(step.getFormQuestions()).hasSize(1);

    FormQuestionModel questionModel = step.getFormQuestions().get(0);
    assertThat(questionModel.getQuestionId()).isEqualTo(20L);
    assertThat(questionModel.getCriterias())
        .extracting(CriteriaModel::getCriteriaId)
        .containsExactly(30L);
    assertThat(questionModel.getBrainstormings()).containsExactly(mappedResponse);
  }

  // ---------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------

  private BrainstormingRequest sampleRequest() {
    BrainstormingRequest request = new BrainstormingRequest();
    request.setBrainstormingId(BRAINSTORMING_ID);
    request.setPredictibilityAnalysisId(ANALYSIS_ID);
    request.setQuestionId(0L);
    request.setBrainstormingText("idée");
    request.setIsChecked(false);
    return request;
  }

  private FormQuestion questionForAnalysis(final Long multiCriteriaAnalysisId) {
    MultiCriteriaAnalysis mca = new MultiCriteriaAnalysis();
    mca.setMultiCriteriaAnalysisId(multiCriteriaAnalysisId);
    FormStep step = new FormStep();
    step.setMultiCriteriaAnalysis(mca);
    FormQuestion question = new FormQuestion();
    question.setQuestionId(20L);
    question.setFormStep(step);
    return question;
  }

  private User sampleUser(final Long id) {
    User user = new User();
    user.setUserId(id);
    user.setUserEmail("user" + id + "@elit.test");
    user.setUsername("user" + id + "@elit.test");
    return user;
  }

  private PredictibilityAnalysis buildAnalysisOwnedByProject(final Long projectId) {
    Project project = new Project();
    project.setProjectId(projectId);
    MultiCriteriaAnalysis mca = new MultiCriteriaAnalysis();
    mca.setProject(project);
    mca.setFormSteps(new ArrayList<>());
    PredictibilityAnalysis analysis = new PredictibilityAnalysis();
    analysis.setPredictibilityAnalysisId(ANALYSIS_ID);
    analysis.setMultiCriteriaAnalysis(mca);
    return analysis;
  }

  private Brainstorming brainstormingOwnedByProject(final Long projectId) {
    Brainstorming brainstorming = new Brainstorming();
    brainstorming.setBrainstormingId(BRAINSTORMING_ID);
    brainstorming.setBrainstormingText("idée");
    brainstorming.setPredictibilityAnalysis(buildAnalysisOwnedByProject(projectId));
    return brainstorming;
  }

  private ProjectUserPermission permissionWith(final String permission) {
    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setPermission(permission);
    return projectUserPermission;
  }

  private HttpEntity<String> confirmedBody() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>("{\"isConfirmed\": true}", headers);
  }

  private void authenticateAs(final User user) {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }
}
