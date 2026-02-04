package com.elit.outildedecision.service;

import com.elit.outildedecision.dto.FeedbackIaMethodCreateDto;
import com.elit.outildedecision.entity.FeedbackIaMethod;
import com.elit.outildedecision.entity.Method;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.repository.FeedbackIaMethodRepository;
import com.elit.outildedecision.repository.MethodRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.implementation.FeedbackIaMethodServiceImpl;
import com.elit.outildedecision.util.MethodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeedbackIaMethodServiceImplTest {

  @Mock
  private FeedbackIaMethodRepository feedbackIaMethodRepository;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private MethodRepository methodRepository;

  @InjectMocks
  private FeedbackIaMethodServiceImpl service;

  private Project project;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    project = new Project();
    project.setProjectId(1L);
  }

  @Test
  void saveFeedback_ShouldCreate_WhenNoExisting_VmodelNameInDb() throws ProjectNotFoundException {
    FeedbackIaMethodCreateDto dto = new FeedbackIaMethodCreateDto(true, "ok", 1L, 2L, MethodType.VMODEL.toString());

    // Simule une valeur en base différente mais convertible par MethodType.find
    Method dbMethod = new Method();
    dbMethod.setId(10L);
    dbMethod.setName("V-Model");

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(methodRepository.findAll()).thenReturn(List.of(dbMethod));
    when(feedbackIaMethodRepository.findByProject_ProjectIdAndUser_UserId(1L, 2L))
      .thenReturn(Optional.empty());

    ResponseEntity<Object> resp = service.saveFeedback(dto);

    assertNotNull(resp);
    assertEquals(200, resp.getStatusCode().value());
    verify(feedbackIaMethodRepository, times(1)).save(any(FeedbackIaMethod.class));
  }

  @Test
  void saveFeedback_ShouldThrowProjectNotFound_WhenProjectMissing() {
    FeedbackIaMethodCreateDto dto = new FeedbackIaMethodCreateDto(true, "ok", 1L, 2L, MethodType.SCRUM.toString());
    when(projectRepository.findById(1L)).thenReturn(Optional.empty());

    org.junit.jupiter.api.Assertions.assertThrows(ProjectNotFoundException.class, () -> service.saveFeedback(dto));
    verify(feedbackIaMethodRepository, never()).save(any());
  }
}
