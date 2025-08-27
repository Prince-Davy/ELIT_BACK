package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.model.AuditRequestModel;
import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.QuestionRequestModel;
import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.audit.repository.QuestionRepository;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.util.QuestionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

  @Mock private AuditRepository auditRepository;
  @Mock private ProjectRepository projectRepository;
  @Mock private UserRepository userRepository;
  @Mock private QuestionRepository questionRepository;

  @InjectMocks
  private AuditServiceImpl auditService;

  private Project project;
  private User user;

  @BeforeEach
  void setUp() {
    project = new Project();
    // use setters to avoid depending on builder here
    // assuming setters exist via Lombok; otherwise adjust accordingly
    try {
      project.getClass().getMethod("setProjectId", Long.class).invoke(project, 100L);
      project.getClass().getMethod("setName", String.class).invoke(project, "Projet A");
    } catch (Exception ignored) { /* If no setters, will be populated via reflection fallback not needed for tests that only read getters */ }

    user = new User();
    try {
      user.getClass().getMethod("setUserId", Long.class).invoke(user, 200L);
      user.getClass().getMethod("setUserFirstName", String.class).invoke(user, "Alice");
      user.getClass().getMethod("setUserLastName", String.class).invoke(user, "Martin");
      user.getClass().getMethod("setUserEmail", String.class).invoke(user, "alice@example.com");
    } catch (Exception ignored) { }
  }

  @Test
  void createAudit_success_withQuestions() throws Exception {
    // Arrange
    Long projectId = 100L;
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(userRepository.findByUserEmail("alice@example.com")).thenReturn(user);

    Audit savedAudit = Audit.builder()
      .auditId(1L)
      .project(project)
      .user(user)
      .auditLaunchDate(new Timestamp(System.currentTimeMillis()))
      .questions(new ArrayList<>())
      .build();

    when(auditRepository.save(any(Audit.class))).thenReturn(savedAudit);
    when(auditRepository.countAuditsByProjectId(projectId)).thenReturn(1L);

    // Mock AuditRequestModel and QuestionRequestModel
    AuditRequestModel request = mock(AuditRequestModel.class);
    when(request.getProjectId()).thenReturn(projectId);

    QuestionRequestModel q1 = mock(QuestionRequestModel.class);
    when(q1.getCategory()).thenReturn(QuestionCategory.ORGANISATIONS);
    when(q1.getQuestionRef()).thenReturn("Q-001");
    when(q1.getWeight()).thenReturn((int) 1.0);
    when(q1.getAuditAnswerLabel()).thenReturn("OUI");
    when(q1.getAuditAnswerValue()).thenReturn((int) 5.0);

    when(request.getQuestions()).thenReturn(List.of(q1));

    // Act
    ResponseEntity<AuditResponseModel> response = auditService.createAudit(request, "alice@example.com");

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getTotalAuditCount());
    assertEquals(projectId, response.getBody().getProjectId());
    assertEquals("Projet A", response.getBody().getProjectName());
    assertEquals(user.getUserId(), response.getBody().getUserId());
    assertEquals(1, response.getBody().getQuestions().size());

    // Verify interactions
    verify(projectRepository).findById(projectId);
    verify(userRepository).findByUserEmail("alice@example.com");
    verify(auditRepository).save(any(Audit.class));
    verify(questionRepository).saveAll(anyList());
    verify(auditRepository).countAuditsByProjectId(projectId);

    // Capture saved questions to ensure they are linked to savedAudit
    ArgumentCaptor<List<Question>> captor = ArgumentCaptor.forClass(List.class);
    verify(questionRepository).saveAll(captor.capture());
    List<Question> savedQuestions = captor.getValue();
    assertEquals(1, savedQuestions.size());
    assertEquals("Q-001", savedQuestions.get(0).getQuestionRef());
    assertEquals(savedAudit.getAuditId(), savedQuestions.get(0).getAudit().getAuditId());
  }

  @Test
  void createAudit_projectNotFound_throws() {
    // Arrange
    AuditRequestModel request = mock(AuditRequestModel.class);
    when(request.getProjectId()).thenReturn(999L);
    when(projectRepository.findById(999L)).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(ProjectNotFoundException.class,
      () -> auditService.createAudit(request, "user@example.com"));

    verify(projectRepository).findById(999L);
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
    // Arrange
    Long projectId = 100L;
    when(projectRepository.existsById(projectId)).thenReturn(true);

    Audit audit = Audit.builder()
      .auditId(1L)
      .project(project)
      .user(user)
      .auditLaunchDate(new Timestamp(System.currentTimeMillis()))
      .questions(Collections.emptyList())
      .build();

    when(auditRepository.findByProjectProjectIdOrderByCreatedAtDesc(projectId))
      .thenReturn(Collections.singletonList(audit));
    when(auditRepository.countAuditsByProjectId(projectId)).thenReturn(1L);

    // Act
    List<AuditResponseModel> responses = auditService.getAuditsByProject(projectId);

    // Assert
    assertNotNull(responses);
    assertEquals(1, responses.size());
    assertEquals(projectId, responses.get(0).getProjectId());
    verify(auditRepository).findByProjectProjectIdOrderByCreatedAtDesc(projectId);
  }

  @Test
  void getAuditsByProject_projectNotFound_throws() {
    // Arrange
    when(projectRepository.existsById(555L)).thenReturn(false);

    // Act + Assert
    assertThrows(ProjectNotFoundException.class,
      () -> auditService.getAuditsByProject(555L));
    verify(projectRepository).existsById(555L);
    verifyNoInteractions(auditRepository);
  }

  @Test
  void getAuditsByProjectPaginated_success() throws Exception {
    // Arrange
    Long projectId = 100L;
    when(projectRepository.existsById(projectId)).thenReturn(true);

    Audit audit = Audit.builder()
      .auditId(2L)
      .project(project)
      .user(user)
      .questions(Collections.emptyList())
      .build();

    Pageable pageable = PageRequest.of(0, 5);
    Page<Audit> page = new PageImpl<>(Collections.singletonList(audit), pageable, 1);
    when(auditRepository.findByProjectProjectIdOrderByCreatedAtDesc(projectId, pageable))
      .thenReturn(page);
    when(auditRepository.countAuditsByProjectId(projectId)).thenReturn(1L);

    // Act
    Page<AuditResponseModel> result = auditService.getAuditsByProjectPaginated(projectId, 0, 5);

    // Assert
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
    assertEquals(projectId, result.getContent().get(0).getProjectId());
    verify(auditRepository).findByProjectProjectIdOrderByCreatedAtDesc(projectId, pageable);
  }
}
