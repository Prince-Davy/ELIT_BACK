package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.calculate.AgilityScoringStrategy;
import com.elit.outildedecision.audit.calculate.AuditCategoryResultEnricher;
import com.elit.outildedecision.audit.calculate.ScoringStrategyFactory;
import com.elit.outildedecision.audit.dto.AuditRequest;
import com.elit.outildedecision.audit.dto.AuditResponse;
import com.elit.outildedecision.audit.dto.AuditScoreResponse;
import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.entity.ScoringType;
import com.elit.outildedecision.audit.mapper.AuditMapper;
import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.audit.repository.QuestionRepository;
import com.elit.outildedecision.audit.service.implementation.AuditServiceImpl;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.audit.exception.AuditNotFoundException;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.MethodType;
import com.elit.outildedecision.shared.util.QuestionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

  @Mock private AuditRepository auditRepository;
  @Mock private ProjectRepository projectRepository;
  @Mock private UserRepository userRepository;
  @Mock private QuestionRepository questionRepository;
  @Mock private ScoringStrategyFactory scoringStrategyFactory;
  @Mock private AuditCategoryResultEnricher auditCategoryResultEnricher;
  @Mock private AgilityScoringStrategy scrumScoringStrategy;

  private final AuditMapper auditMapper = Mappers.getMapper(AuditMapper.class);

  private AuditServiceImpl auditService;

  private static final Long COMMON_ID = 1L;
  private static final String PROJECT_NAME = "Projet A";
  private static final String USER_EMAIL = "user@example.com";
  private static final String USER_FIRST_NAME = "Alice";
  private static final String USER_LAST_NAME = "Martin";
  private Project project;
  private User user;
  private Audit audit;
  private Question question;

  @BeforeEach
  void setUp() {
    auditService = new AuditServiceImpl(auditRepository, projectRepository, userRepository,
        scoringStrategyFactory, auditMapper, auditCategoryResultEnricher);

    project = new Project();
    ReflectionTestUtils.setField(project, "projectId", COMMON_ID);
    ReflectionTestUtils.setField(project, "name", PROJECT_NAME);

    user = new User();
    ReflectionTestUtils.setField(user, "userId", COMMON_ID);
    ReflectionTestUtils.setField(user, "userFirstName", USER_FIRST_NAME);
    ReflectionTestUtils.setField(user, "userLastName", USER_LAST_NAME);
    ReflectionTestUtils.setField(user, "userEmail", USER_EMAIL);

    question = Question.builder()
      .questionId(COMMON_ID)
      .questionRef("Q1")
      .category(QuestionCategory.ORGANISATIONS)
      .weight(2)
      .audit(audit)
      .auditAnswerLabel("NON")
      .auditAnswerValue(2)
      .scoringType(ScoringType.DIRECT)
      .build();

    audit = Audit.builder()
      .auditId(COMMON_ID)
      .project(project)
      .method(MethodType.SCRUM)
      .user(user)
      .auditLaunchDate(new Timestamp(System.currentTimeMillis()))
      .questions(List.of(question))
      .build();

  }

  @Test
  void createAudit_success_withQuestions() throws Exception {
    // Arrange
    when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
    when(userRepository.findByUserEmail(any())).thenReturn(user);
    when(auditRepository.save(any(Audit.class))).thenReturn(audit);
    when(auditRepository.countAuditsByProjectId(anyLong())).thenReturn(1L);

    AuditRequest request = mock(AuditRequest.class);
    when(request.getProjectId()).thenReturn(COMMON_ID);

    // Act
    AuditResponse responseBody = auditService.createAudit(request, "alice@example.com");

    // Assert
    assertNotNull(responseBody);
    assertEquals(1L, responseBody.getTotalAuditCount());
    assertEquals(COMMON_ID, responseBody.getProjectId());
    assertEquals("Projet A", responseBody.getProjectName());
    assertEquals(user.getUserId(), responseBody.getUserId());
    assertEquals(1, responseBody.getQuestions().size());

    // Verify repository interactions
    verify(projectRepository).findById(anyLong());
    verify(userRepository).findByUserEmail("alice@example.com");
    verify(auditRepository).save(any(Audit.class));
    verify(auditRepository).countAuditsByProjectId(anyLong());
  }


  @Test
  void createAudit_projectNotFound_throws() {
    // Arrange
    AuditRequest request = mock(AuditRequest.class);
    when(request.getProjectId()).thenReturn(COMMON_ID);
    when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(ProjectNotFoundException.class,
      () -> auditService.createAudit(request, "user@example.com"));

    verify(projectRepository).findById(COMMON_ID);
    verifyNoInteractions(userRepository);
    verifyNoInteractions(questionRepository);
  }


  @Test
  void updateAudit_userNotFound_throws() {
    // Arrange
    AuditRequest request = mock(AuditRequest.class);
    when(userRepository.findByUserEmail("ghost@example.com")).thenReturn(null);

    // Act + Assert
    assertThrows(UserNotFoundException.class,
      () -> auditService.updateAudit(request, "ghost@example.com"));

    verifyNoInteractions(auditRepository);
    verifyNoInteractions(questionRepository);
  }

  @Test
  void updateAudit_nullAuditId_throwsBadRequest() {
    // Arrange
    AuditRequest request = mock(AuditRequest.class);
    when(userRepository.findByUserEmail(USER_EMAIL)).thenReturn(user);
    when(request.getAuditId()).thenReturn(null);

    // Act + Assert
    assertThrows(BadRequestException.class,
      () -> auditService.updateAudit(request, USER_EMAIL));

    verifyNoInteractions(auditRepository);
  }

  @Test
  void getAuditsByProject_success() throws Exception {

    when(projectRepository.existsById(COMMON_ID)).thenReturn(true);
    when(auditRepository.findByProjectProjectIdOrderByCreatedAtDesc(COMMON_ID))
      .thenReturn(Collections.singletonList(audit));
    when(auditRepository.countAuditsByProjectId(COMMON_ID)).thenReturn(1L);

    // Act
    List<AuditResponse> responses = auditService.getAuditsByProject(COMMON_ID);

    // Assert
    assertNotNull(responses);
    assertEquals(1, responses.size());
    assertEquals(COMMON_ID, responses.get(0).getProjectId());
    verify(auditRepository).findByProjectProjectIdOrderByCreatedAtDesc(COMMON_ID);
  }

  @Test
  void getAuditsByProject_projectNotFound_throws() {
    // Arrange
    when(projectRepository.existsById(anyLong())).thenReturn(false);

    // Act + Assert
    assertThrows(ProjectNotFoundException.class,
      () -> auditService.getAuditsByProject(anyLong()));
    verify(projectRepository).existsById(anyLong());
    verifyNoInteractions(auditRepository);
  }

  @Test
  void calculateScoringOfAudit_shouldSuccess() throws AuditNotFoundException {
    // Arrange
    Audit scoringAudit = Audit.builder()
      .auditId(COMMON_ID)
      .method(MethodType.SCRUM)
      .build();

    Question scoringQuestion = Question.builder()
      .questionRef("Q1")
      .weight(1)
      .auditAnswerLabel("Oui")
      .auditAnswerValue(10)
      .category(QuestionCategory.ORGANISATIONS)
      .scoringType(ScoringType.DIRECT)
      .audit(scoringAudit)
      .build();

    scoringAudit.setQuestions(List.of(scoringQuestion));

    AuditScore auditScore = AuditScore.builder()
      .id(COMMON_ID)
      .globalScorePercentage(100.0)
      .auditCategoryResults(List.of())
      .audit(scoringAudit).user(user).build();

    when(auditRepository.findById(COMMON_ID)).thenReturn(Optional.of(scoringAudit));
    when(scoringStrategyFactory.resolveStrategyByMethodType(MethodType.SCRUM)).thenReturn(scrumScoringStrategy);
    when(scrumScoringStrategy.calculateScoring(scoringAudit)).thenReturn(auditScore);
    doNothing().when(auditCategoryResultEnricher).enrichAuditComment(auditScore, MethodType.SCRUM, auditScore.getAuditCategoryResults());
    when(auditRepository.save(scoringAudit)).thenReturn(scoringAudit);

    // Act
    AuditScoreResponse result = auditService.calculateScoringOfAudit(COMMON_ID);

    // Assert
    assertNotNull(result);
    assertEquals(100.0, result.getGlobalScorePercentage());
    assertEquals(COMMON_ID, result.getAuditId());

    verify(auditRepository).findById(COMMON_ID);
    verify(scoringStrategyFactory).resolveStrategyByMethodType(MethodType.SCRUM);
    verify(scrumScoringStrategy).calculateScoring(scoringAudit);
    verify(auditCategoryResultEnricher).enrichAuditComment(auditScore, MethodType.SCRUM, auditScore.getAuditCategoryResults());
    verify(auditRepository).save(scoringAudit);
  }


  @Test
  void calculateScoringOfAudit_shouldThrowsException() {
    when(auditRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(AuditNotFoundException.class,
      () -> auditService.calculateScoringOfAudit(anyLong()));

  }

  @Test
  void calculateScoringOfAudit_nullMethod_throwsBadRequest() {
    // Arrange : audit without a methodology cannot be scored
    Audit methodlessAudit = Audit.builder().auditId(COMMON_ID).method(null).build();
    when(auditRepository.findById(COMMON_ID)).thenReturn(Optional.of(methodlessAudit));

    // Act + Assert
    assertThrows(BadRequestException.class,
      () -> auditService.calculateScoringOfAudit(COMMON_ID));

    verifyNoInteractions(scoringStrategyFactory);
  }

  @Test
  void calculateScoringOfAudit_unsupportedMethod_throwsBadRequest() {
    // Arrange : KANBAN has no registered scoring strategy
    Audit kanbanAudit = Audit.builder().auditId(COMMON_ID).method(MethodType.KANBAN).build();
    when(auditRepository.findById(COMMON_ID)).thenReturn(Optional.of(kanbanAudit));
    when(scoringStrategyFactory.resolveStrategyByMethodType(MethodType.KANBAN))
      .thenThrow(new IllegalArgumentException("Unsupported method type: kanban"));

    // Act + Assert
    assertThrows(BadRequestException.class,
      () -> auditService.calculateScoringOfAudit(COMMON_ID));

    verify(scoringStrategyFactory).resolveStrategyByMethodType(MethodType.KANBAN);
  }

  @Test
  void getAuditByProjectAndAuditId_success() throws Exception {
    // Arrange
    when(projectRepository.existsById(COMMON_ID)).thenReturn(true);
    when(auditRepository.findById(COMMON_ID))
      .thenReturn(Optional.of(audit));

    // Act
    AuditResponse response = auditService.getAuditByProjectAndAuditId(COMMON_ID, COMMON_ID);

    // Assert
    assertNotNull(response);
    assertEquals(COMMON_ID, response.getAuditId());
    assertEquals(COMMON_ID, response.getProjectId());
    verify(projectRepository).existsById(COMMON_ID);
    verify(auditRepository).findById(COMMON_ID);
  }

  @Test
  void getAuditByProjectAndAuditId_projectNotFound_throws() {
    when(projectRepository.existsById(COMMON_ID)).thenReturn(false);
    assertThrows(ProjectNotFoundException.class,
      () -> auditService.getAuditByProjectAndAuditId(COMMON_ID, COMMON_ID));
    verify(projectRepository).existsById(COMMON_ID);
    verifyNoInteractions(auditRepository);
  }

  @Test
  void getAuditByProjectAndAuditId_auditNotFound_throws() {
    when(projectRepository.existsById(COMMON_ID)).thenReturn(true);
    when(auditRepository.findById(COMMON_ID)).thenReturn(Optional.empty());
    assertThrows(AuditNotFoundException.class,
      () -> auditService.getAuditByProjectAndAuditId(COMMON_ID, COMMON_ID));
    verify(projectRepository).existsById(COMMON_ID);
    verify(auditRepository).findById(COMMON_ID);
  }

  @Test
  void hasAuditForProject_shouldReturnTrue_whenProjectHasAudit() throws ProjectNotFoundException {
    when(projectRepository.existsById(COMMON_ID)).thenReturn(true);
    when(auditRepository.countAuditsByProjectId(COMMON_ID)).thenReturn(2L);
    boolean result = auditService.hasAuditForProject(COMMON_ID);
    assertTrue(result);
    verify(projectRepository).existsById(COMMON_ID);
    verify(auditRepository).countAuditsByProjectId(COMMON_ID);
  }

  @Test
  void hasAuditForProject_shouldReturnFalse_whenProjectHasNoAudit() throws ProjectNotFoundException {
    when(projectRepository.existsById(COMMON_ID)).thenReturn(true);
    when(auditRepository.countAuditsByProjectId(COMMON_ID)).thenReturn(0L);
    boolean result = auditService.hasAuditForProject(COMMON_ID);
    assertFalse(result);
    verify(projectRepository).existsById(COMMON_ID);
    verify(auditRepository).countAuditsByProjectId(COMMON_ID);
  }

  @Test
  void hasAuditForProject_shouldThrow_whenProjectNotFound() {
    when(projectRepository.existsById(COMMON_ID)).thenReturn(false);
    assertThrows(ProjectNotFoundException.class, () -> auditService.hasAuditForProject(COMMON_ID));
    verify(projectRepository).existsById(COMMON_ID);
    verifyNoInteractions(auditRepository);
  }
}
