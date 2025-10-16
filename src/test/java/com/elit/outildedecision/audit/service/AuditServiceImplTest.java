package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.calculate.AuditCategoryResultEnricher;
import com.elit.outildedecision.audit.calculate.ScoringStrategyFactory;
import com.elit.outildedecision.audit.calculate.ScrumScoringStrategy;
import com.elit.outildedecision.audit.entity.*;
import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.comment.ScrumCategoryComment;
import com.elit.outildedecision.audit.entity.comment.ScrumGlobalComment;
import com.elit.outildedecision.audit.mapper.AuditMapper;
import com.elit.outildedecision.audit.model.*;
import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.audit.repository.QuestionRepository;
import com.elit.outildedecision.audit.service.impl.AuditServiceImpl;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.AuditNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

  @Mock private AuditRepository auditRepository;
  @Mock private ProjectRepository projectRepository;
  @Mock private UserRepository userRepository;
  @Mock private QuestionRepository questionRepository;
  @Mock private AuditMapper auditMapper;
  @Mock private ScoringStrategyFactory scoringStrategyFactory;
  @Mock private AuditCategoryResultEnricher auditCategoryResultEnricher;
  @Mock private CategoryCommentService categoryCommentService;
  @Mock private GlobalCommentService globalCommentService;


  @InjectMocks
  private AuditServiceImpl auditService;

  private static final Long COMMON_ID = 1L;
  private static final String PROJECT_NAME = "Projet A";
  private static final String USER_EMAIL = "user@example.com";
  private static final String USER_FIRST_NAME = "Alice";
  private static final String USER_LAST_NAME = "Martin";
  private ScrumScoringStrategy scrumScoringStrategy;
  private Project project;
  private User user;
  private Audit audit;
  private Question question;

  @BeforeEach
  void setUp() {
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

    scrumScoringStrategy = new ScrumScoringStrategy(auditCategoryResultEnricher);

  }

  @Test
  void createAudit_success_withQuestions() throws Exception {
    // Arrange
    when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
    when(userRepository.findByUserEmail(any())).thenReturn(user);
    when(auditRepository.save(any(Audit.class))).thenReturn(audit);
    when(auditRepository.countAuditsByProjectId(anyLong())).thenReturn(1L);

    AuditRequestModel request = mock(AuditRequestModel.class);
    when(request.getProjectId()).thenReturn(COMMON_ID);

    QuestionResponseModel questionResponse = QuestionResponseModel.builder()
      .category(QuestionCategory.ORGANISATIONS)
      .questionRef(question.getQuestionRef())
      .weight(question.getWeight())
      .auditId(question.getQuestionId())
      .auditAnswerLabel(question.getAuditAnswerLabel())
      .auditAnswerValue(question.getAuditAnswerValue()).build();

    when(auditMapper.mapToQuestionResponseModel(any())).thenReturn(questionResponse);

    // Act
    ResponseEntity<AuditResponseModel> response = auditService.createAudit(request, "alice@example.com");

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    AuditResponseModel responseBody = response.getBody();
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
    AuditRequestModel request = mock(AuditRequestModel.class);
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
    AuditRequestModel request = mock(AuditRequestModel.class);
    when(userRepository.findByUserEmail("ghost@example.com")).thenReturn(null);

    // Act + Assert
    assertThrows(UserNotFoundException.class,
      () -> auditService.updateAudit(request, "ghost@example.com"));

    verifyNoInteractions(auditRepository);
    verifyNoInteractions(questionRepository);
  }

  @Test
  void getAuditsByProject_success() throws Exception {

    when(projectRepository.existsById(COMMON_ID)).thenReturn(true);

    when(auditRepository.findByProjectProjectIdOrderByCreatedAtDesc(COMMON_ID))
      .thenReturn(Collections.singletonList(audit));
    when(auditRepository.countAuditsByProjectId(COMMON_ID)).thenReturn(1L);

    // Act
    List<AuditResponseModel> responses = auditService.getAuditsByProject(COMMON_ID);

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
  void getAuditsByProjectPaginated_success() throws Exception {
    when(projectRepository.existsById(anyLong())).thenReturn(true);

    Pageable pageable = PageRequest.of(0, 5);
    Page<Audit> page = new PageImpl<>(Collections.singletonList(audit), pageable, 1);
    when(auditRepository.findByProjectProjectIdOrderByCreatedAtDesc(anyLong(), any()))
      .thenReturn(page);
    when(auditRepository.countAuditsByProjectId(anyLong())).thenReturn(COMMON_ID);

    // Act
    Page<AuditResponseModel> result = auditService.getAuditsByProjectPaginated(anyLong(), 0, 5);

    // Assert
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
    assertEquals(COMMON_ID, result.getContent().get(0).getProjectId());
    verify(auditRepository).findByProjectProjectIdOrderByCreatedAtDesc(anyLong(), any());
  }

  @Test
  void calculateScoringOfAudit_shouldSuccess() throws AuditNotFoundException {


    Audit audit = Audit.builder()
      .auditId(COMMON_ID)
      .method(MethodType.SCRUM)
      .build();

    Question question = Question.builder()
      .questionRef("Q1")
      .weight(1)
      .auditAnswerLabel("Oui")
      .auditAnswerValue(10)
      .category(QuestionCategory.ORGANISATIONS)
      .scoringType(ScoringType.DIRECT)
      .audit(audit).build();
    audit.setQuestions(List.of(question));
    QuestionResponseModel questionResponseModel = QuestionResponseModel.builder()
      .questionRef(question.getQuestionRef())
      .weight(question.getWeight())
      .auditAnswerLabel(question.getAuditAnswerLabel())
      .auditAnswerValue(question.getAuditAnswerValue())
      .category(question.getCategory())
      .scoringType(question.getScoringType())
      .build();

    CategoryComment organisationsComment = ScrumCategoryComment.builder()
      .id(COMMON_ID)
      .category(QuestionCategory.ORGANISATIONS)
      .text("text1")
      .level(LevelScore.BON)
      .build();

    AuditCategoryResult categoryResult = AuditCategoryResult.builder()
      .level(LevelScore.BON)
      .categoryComment(organisationsComment)
      .scorePercentageCategory(100.0)
      .category(question.getCategory())
      .build();

    QuestionResponseScore questionResponseScore = QuestionResponseScore.builder()
      .score(10.0)
      .build();

    AuditScore auditScore = AuditScore.builder()
      .audit(audit)
      .user(user)
      .globalScorePercentage(100.0)
      .submissionDate(audit.getAuditLaunchDate())
      .auditCategoryResults(List.of(categoryResult))
      .questionResponses(List.of(questionResponseScore))
      .build();

    AuditScoreDTO auditScoreDTO = AuditScoreDTO.builder()
      .auditScoreId(COMMON_ID)
      .auditId(audit.getAuditId())
      .globalScorePercentage(100.0)
      .questionResponses(List.of(
        QuestionResponseScoreDTO.builder()
          .score(10.0)
          .category(question.getCategory())
          .build()))
      .build();

    GlobalComment globalComment = ScrumGlobalComment.builder()
      .id(99L)
      .method(MethodType.SCRUM)
      .level(LevelScore.BON)
      .text("Global comment")
      .build();

    // Mocking
    when(auditRepository.findById(anyLong())).thenReturn(Optional.of(audit));
    when(scoringStrategyFactory.resolveStrategyByMethodType(any())).thenReturn(scrumScoringStrategy);
    when(globalCommentService.findByMethodAndLevel(any(), any())).thenReturn(globalComment);
    when(auditRepository.save(any())).thenReturn(audit);
    when(auditMapper.auditScoretoAuditScoreDto(any())).thenReturn(auditScoreDTO);

    // Act
    AuditScoreDTO result = auditService.calculateScoringOfAudit(COMMON_ID);

    // Assert
    assertNotNull(result);
    assertEquals(100.0, result.getGlobalScorePercentage());
    assertEquals(COMMON_ID, result.getAuditId());
    verify(auditRepository).findById(COMMON_ID);
    verify(scoringStrategyFactory).resolveStrategyByMethodType(MethodType.SCRUM);
    verify(globalCommentService).findByMethodAndLevel(MethodType.SCRUM, LevelScore.BON);
    verify(auditRepository).save(any());
    verify(auditMapper).auditScoretoAuditScoreDto(any());

}

  @Test
  void calculateScoringOfAudit_shouldThrowsException() {
    when(auditRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(AuditNotFoundException.class,
      () -> auditService.calculateScoringOfAudit(anyLong()));

  }
  }
