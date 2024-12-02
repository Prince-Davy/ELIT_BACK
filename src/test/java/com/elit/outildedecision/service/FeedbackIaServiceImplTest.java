package com.elit.outildedecision.service;

import com.elit.outildedecision.controller.FeedbackIaController;
import com.elit.outildedecision.entity.FeedbackIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.model.FeedBackIaModel;
import com.elit.outildedecision.repository.FeedbackIaRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.implementation.FeedbackIaServiceImpl;
import com.elit.outildedecision.util.ProjectPermession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class FeedbackIaServiceImplTest {

  MockMvc mockMvc;
  @Mock
  private FeedbackIaRepository feedbackIaRepository;
  @Mock
  private ProjectRepository projectRepository;
  @InjectMocks
  private FeedbackIaServiceImpl feedbackIaService;
  @InjectMocks
  private FeedbackIaController feedbackIaController;
  private FeedbackIa feedbackIa;
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
    feedbackIa = new FeedbackIa(1L, true, null, null, project, user);
  }

  @Test
  void saveFeedback_ShouldSaveSuccessfully_WhenAllFieldsAreValid() throws ProjectNotFoundException {
    // Arrange
    FeedbackIa feedbackIa = new FeedbackIa();
    feedbackIa.setIsLiked(false);
    feedbackIa.setMethodology("Agile");
    feedbackIa.setFeedbackAnswer("Excellent feedback");
    feedbackIa.setProject(project);
    feedbackIa.setUser(user);

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(1L, user.getUserId())).thenReturn(null);

    // Act
    ResponseEntity<Object> response = feedbackIaService.saveFeedback(feedbackIa);

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Feedback enregistré avec succès.", response.getBody());
    verify(feedbackIaRepository, times(1)).save(feedbackIa);
  }

  @Test
  void saveFeedback_ShouldReturnConflict_WhenFeedbackAlreadyExists() throws ProjectNotFoundException {
    // Arrange
    FeedbackIa existingFeedback = new FeedbackIa();
    existingFeedback.setIsLiked(false);
    existingFeedback.setMethodology("Agile");
    existingFeedback.setFeedbackAnswer("Existing feedback");
    existingFeedback.setProject(project);
    existingFeedback.setUser(user);

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(1L, user.getUserId())).thenReturn(existingFeedback);

    // Act
    ResponseEntity<Object> response = feedbackIaService.saveFeedback(feedbackIa);

    // Assert
    assertNotNull(response);
    assertEquals(409, response.getStatusCodeValue());
    assertEquals("Vous avez déjà un feedback pour ce projet!", response.getBody());
    verify(feedbackIaRepository, never()).save(feedbackIa);
  }

  @Test
  void saveFeedback_ShouldThrowException_WhenProjectNotFound() {
    // Arrange
    when(projectRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ProjectNotFoundException.class, () -> feedbackIaService.saveFeedback(feedbackIa));
    verify(feedbackIaRepository, never()).save(feedbackIa);
  }

  @Test
  void getAllFeedbacks_ShouldReturnListOfFeedbackModels() {
    // Arrange
    List<FeedbackIa> feedbackList = List.of(feedbackIa);
    when(feedbackIaRepository.findAll()).thenReturn(feedbackList);

    // Act
    List<FeedBackIaModel> feedbacks = feedbackIaService.getAllFeedbacks();

    // Assert
    assertNotNull(feedbacks);
    assertEquals(1, feedbacks.size());
    assertEquals(null, feedbacks.get(0).getFeedbackAnswer());
  }

  @Test
  void getFeedbacksByProject_ShouldReturnListOfFeedbackModels() {
    // Arrange
    List<FeedbackIa> feedbackList = List.of(feedbackIa);
    when(feedbackIaRepository.findByProject_ProjectId(1L)).thenReturn(feedbackList);

    // Act
    List<FeedBackIaModel> feedbacks = feedbackIaService.getFeedbacksByProject(1L);

    // Assert
    assertNotNull(feedbacks);
    assertEquals(1, feedbacks.size());
    assertEquals(null, feedbacks.get(0).getMethodology());
  }

  @Test
  void getFeedbackByProjectIdAndUserId_ShouldReturnFeedbackModel() {
    // Arrange
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(1L, 1L)).thenReturn(feedbackIa);

    // Act
    FeedBackIaModel feedbackModel = feedbackIaService.getFeedbackByProjectIdAndUserId(1L, 1L);

    // Assert
    assertNotNull(feedbackModel);
    assertEquals(null, feedbackModel.getFeedbackAnswer());
  }

  @Test
  void deleteFeedbackByUserAndProject_ShouldDeleteFeedbackSuccessfully() {
    // Arrange
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(1L, 1L)).thenReturn(feedbackIa);

    // Act
    ResponseEntity<Object> response = feedbackIaService.deleteFeedbackByUserAndProject(1L, 1L);

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Le FeedBack a bien été supprimé", response.getBody());
    verify(feedbackIaRepository, times(1)).deleteByProject_ProjectIdAndUser_UserId(1L, 1L);
  }

  @Test
  void deleteFeedbackByUserAndProject_ShouldReturnNotFound_WhenFeedbackDoesNotExist() {
    // Arrange
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(1L, 1L)).thenReturn(null);

    // Act
    ResponseEntity<Object> response = feedbackIaService.deleteFeedbackByUserAndProject(1L, 1L);

    // Assert
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
    assertEquals("Feedback non trouvé pour le projet ID 1 et l'utilisateur ID 1", response.getBody());
  }

  @Test
  void deleteFeedbacksByProjectId_ShouldDeleteAllFeedbacksForProject() {
    // Arrange
    List<FeedbackIa> feedbackList = List.of(feedbackIa);
    when(feedbackIaRepository.findByProject_ProjectId(1L)).thenReturn(feedbackList);

    // Act
    ResponseEntity<Object> response = feedbackIaService.deleteFeedbacksByProjectId(1L);

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Tous les FeedBacks du projet ont bien été supprimé", response.getBody());
    verify(feedbackIaRepository, times(1)).deleteAll(feedbackList);
  }
}
