package com.elit.outildedecision.controller;


import com.elit.outildedecision.entity.FeedbackIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.FeedBackNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.model.FeedBackIaModel;
import com.elit.outildedecision.service.FeedbackIaService;
import com.elit.outildedecision.enumeration.ProjectPermession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ActiveProfiles("Test")
@RunWith(MockitoJUnitRunner.class)
class FeedbackIaControllerTest {

  @Mock
  private FeedbackIaService feedbackIaService;
  @InjectMocks
  private FeedbackIaController feedbackIaController;
  MockMvc mockMvc;
  private FeedbackIa feedbackIa;
  private FeedBackIaModel feedbackIaModel;
  private User user;
  private Project project;
  private ProjectUserPermission permission;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(feedbackIaController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();
    project = new Project();
    project.setProjectId(1L);
    user = new User();
    user.setUserEmail("user@user.com");
    user.setUsername("user@user.com");
    permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(ProjectPermession.CONTRIBUTER.toString());
    user.setUserProjectPermissions(List.of(permission));
    feedbackIa = new FeedbackIa(1L, true, "Agile", "Great project", project, user);
    feedbackIaModel = new FeedBackIaModel(true, "Agile", "Great project", 1L, 1L);
  }

  @Test
  void contextLoad() {
    assertNotNull(feedbackIaController);
  }

  @Test
  void shouldSaveFeedback() throws ProjectNotFoundException, FeedBackNotFoundException {
    // Arrange
    Long projectId = 1L;
    ResponseEntity<Object> expectedResponse = ResponseEntity.status(201).body(feedbackIa);
    when(feedbackIaService.saveFeedback(feedbackIa)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<Object> response = feedbackIaController.saveFeedback(feedbackIa, projectId);

    // Assert
    assertThat(response.getStatusCodeValue()).isEqualTo(201);
    assertThat(response.getBody()).isEqualTo(feedbackIa);
    verify(feedbackIaService, times(1)).saveFeedback(feedbackIa);
  }

  @Test
  void shouldGetAllFeedbacks() {
    // Arrange
    when(feedbackIaService.getAllFeedbacks()).thenReturn(List.of(feedbackIaModel));

    // Act
    ResponseEntity<List<FeedBackIaModel>> response = feedbackIaController.getAllFeedbacks();

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertTrue(response.getBody().contains(feedbackIaModel));

    verify(feedbackIaService, times(1)).getAllFeedbacks();
  }


  @Test
  void shouldGetFeedbacksByProjectId() {
    // Arrange
    Long projectId = 1L;
    when(feedbackIaService.getFeedbacksByProject(projectId)).thenReturn(List.of(feedbackIaModel));

    // Act
    ResponseEntity<List<FeedBackIaModel>> response = feedbackIaController.getFeedbacksByProjectId(projectId);

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertTrue(response.getBody().contains(feedbackIaModel));

    verify(feedbackIaService, times(1)).getFeedbacksByProject(projectId);
  }

  @Test
  void shouldGetFeedbackByProjectIdAndUserId() throws FeedBackNotFoundException {
    // Arrange
    Long projectId = 1L;
    Long userId = 1L;
    when(feedbackIaService.getFeedbackByProjectIdAndUserId(projectId, userId)).thenReturn(feedbackIaModel);

    // Act
    ResponseEntity<FeedBackIaModel> response = feedbackIaController.getFeedbackByProjectIdAndUserId(projectId, userId);

    // Assert
    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody()).isEqualTo(feedbackIaModel);
    verify(feedbackIaService, times(1)).getFeedbackByProjectIdAndUserId(projectId, userId);
  }

  @Test
  void shouldDeleteFeedbackByUserAndProject() {
    // Arrange
    Long projectId = 1L;
    Long userId = 1L;
    ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
    when(feedbackIaService.deleteFeedbackByUserAndProject(projectId, userId)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<Object> response = feedbackIaController.deleteFeedbackByUserAndProject(projectId, userId);

    // Assert
    assertThat(response.getStatusCodeValue()).isEqualTo(204);
    verify(feedbackIaService, times(1)).deleteFeedbackByUserAndProject(projectId, userId);
  }

  @Test
  void shouldDeleteFeedbacksByProjectId() {
    // Arrange
    Long projectId = 1L;
    ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
    when(feedbackIaService.deleteFeedbacksByProjectId(projectId)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<Object> response = feedbackIaController.deleteFeedbacksByProjectId(projectId);

    // Assert
    assertThat(response.getStatusCodeValue()).isEqualTo(204);
    verify(feedbackIaService, times(1)).deleteFeedbacksByProjectId(projectId);
  }
}
