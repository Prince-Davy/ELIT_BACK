package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.repository.AnswerIaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnswerIaServiceImplTest {

  @Mock
  private AnswerIaRepository answerIaRepository;

  @InjectMocks
  private AnswerIaServiceImpl answerIaService;

  private List<AnswerIa> answers;
  private Project project;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    project = new Project();
    project.setProjectId(1L);

    answers = new ArrayList<>();
    AnswerIa answer1 = new AnswerIa("question1", "category1", "answer1", project);
    AnswerIa answer2 = new AnswerIa("question2", "category2", "answer2", project);

    answers.add(answer1);
    answers.add(answer2);
  }

  @Test
  void getAllAnswers_shouldReturnAnswers() {
    // Arrange
    when(answerIaRepository.findByProject_ProjectId(project.getProjectId())).thenReturn(answers);

    // Act
    List<AnswerIa> result = answerIaService.getAllAnswers(project.getProjectId());

    // Assert
    assertEquals(answers.size(), result.size());
    assertEquals(answers, result);
    verify(answerIaRepository, times(1)).findByProject_ProjectId(project.getProjectId());
  }

  @Test
  void saveAll_shouldSaveNewAnswers() {
    // Arrange
    when(answerIaRepository.findByQuestionAndProject_ProjectId(anyString(), anyLong()))
      .thenReturn(Optional.empty());

    // Act
    answerIaService.saveAll(answers);

    // Assert
    verify(answerIaRepository, times(answers.size())).findByQuestionAndProject_ProjectId(anyString(), anyLong());
    verify(answerIaRepository, times(1)).saveAll(answers);
  }

  @Test
  void saveAll_shouldUpdateExistingAnswer() {
    // Arrange
    AnswerIa existingAnswer = new AnswerIa("question1", "category1", "oldAnswer", project);

    when(answerIaRepository.findByQuestionAndProject_ProjectId("question1", project.getProjectId()))
      .thenReturn(Optional.of(existingAnswer));
    when(answerIaRepository.findByQuestionAndProject_ProjectId("question2", project.getProjectId()))
      .thenReturn(Optional.empty());

    // Act
    answerIaService.saveAll(answers);

    // Assert
    verify(answerIaRepository, times(2)).findByQuestionAndProject_ProjectId(anyString(), anyLong());
    verify(answerIaRepository, times(1)).delete(existingAnswer);
    verify(answerIaRepository, times(1)).saveAll(anyList());
  }

  @Test
  void deleteAnswersByProjectId_shouldDeleteAnswers() {
    // Act
    answerIaService.deleteAnswersByProjectId(project.getProjectId());

    // Assert
    verify(answerIaRepository, times(1)).deleteByProject_ProjectId(project.getProjectId());
  }
}
