package com.elit.outildedecision.ai.answer.service;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.ai.answer.service.implementation.AiAnswerServiceImpl;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiAnswerServiceImplTest {

  @Mock
  private AiAnswerRepository aiAnswerRepository;

  @Mock
  private ProjectRepository projectRepository;

  @InjectMocks
  private AiAnswerServiceImpl aiAnswerService;

  @Test
  void saveAnswers_shouldSaveValidAnswers() throws Exception {
    // Given
    Long projectId = 1L;
    Project project = new Project();
    Map<String, String> data = Map.of("Q1", "A1", "Q2", "A2");

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

    // When
    aiAnswerService.saveAnswers(projectId, data);

    // Then
    // Vérifie l'ordre: findById -> delete -> saveAll
    InOrder inOrder = inOrder(projectRepository, aiAnswerRepository);
    inOrder.verify(projectRepository).findById(projectId);
    inOrder.verify(aiAnswerRepository).deleteByProject_ProjectId(projectId);
    ArgumentCaptor<List<AiAnswer>> listCaptor = ArgumentCaptor.forClass(List.class);
    inOrder.verify(aiAnswerRepository).saveAll(listCaptor.capture());

    List<AiAnswer> saved = listCaptor.getValue();
    assertThat(saved)
        .as("Le nombre d'answers sauvegardées doit correspondre à la taille de la map fournie")
        .hasSize(2);
  }

  @Test
  void saveAnswers_shouldThrowProjectNotFoundException() {
    // Given
    Long projectId = 1L;
    Map<String, String> data = Map.of("Q1", "A1");
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

    // When / Then
    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> aiAnswerService.saveAnswers(projectId, data));
    verify(aiAnswerRepository, never()).saveAll(anyList());
  }

  @Test
  void saveAnswers_shouldThrowAiAnswerNotFoundException_whenDataEmpty() {
    // Given
    Long projectId = 2L;
    Project project = new Project();
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

    // When / Then
    assertThatExceptionOfType(AiAnswerNotFoundException.class)
        .isThrownBy(() -> aiAnswerService.saveAnswers(projectId, Map.of()));
    verify(aiAnswerRepository, never()).saveAll(anyList());
  }

  @Test
  void getAnswersByProject_shouldThrowAiAnswerNotFoundException_whenEmpty() {
    // Given
    Long projectId = 99L;
    when(aiAnswerRepository.findByProject_ProjectId(projectId)).thenReturn(List.of());

    // When / Then
    assertThatExceptionOfType(AiAnswerNotFoundException.class)
        .isThrownBy(() -> aiAnswerService.getAnswersByProject(projectId));
  }

  @Test
  void saveAnswers_shouldThrowAiAnswerNotFoundException_whenDataNull() {
    // Given
    Long projectId = 10L;
    Project project = new Project();
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

    // When / Then
    assertThatExceptionOfType(AiAnswerNotFoundException.class)
        .isThrownBy(() -> aiAnswerService.saveAnswers(projectId, null));
    verify(aiAnswerRepository, never()).deleteByProject_ProjectId(anyLong());
    verify(aiAnswerRepository, never()).saveAll(anyList());
  }

  @Test
  void saveAnswers_shouldFilterOutInvalidEntries_andSaveOnlyValidOnes() throws Exception {
    // Given
    Long projectId = 11L;
    Project project = new Project();
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

    java.util.Map<String, String> data = new java.util.LinkedHashMap<>();
    data.put(" ", "A0");          // clé vide -> ignorée
    data.put(null, "A9");         // clé null -> ignorée
    data.put("Q1", null);         // valeur null -> ignorée
    data.put("Q2", " ");          // valeur vide -> ignorée
    data.put("Q3", "A3");         // valide
    data.put("  Q4  ", "  A4  "); // valide + trim

    // When
    aiAnswerService.saveAnswers(projectId, data);

    // Then
    InOrder inOrder = inOrder(projectRepository, aiAnswerRepository);
    inOrder.verify(projectRepository).findById(projectId);
    inOrder.verify(aiAnswerRepository).deleteByProject_ProjectId(projectId);
    ArgumentCaptor<java.util.List<AiAnswer>> captor = ArgumentCaptor.forClass(java.util.List.class);
    inOrder.verify(aiAnswerRepository).saveAll(captor.capture());

    java.util.List<AiAnswer> saved = captor.getValue();
    assertThat(saved)
        .as("Seules les paires valides doivent être sauvegardées")
        .hasSize(2);

    java.util.Set<String> questions = saved.stream()
      .map(AiAnswer::getQuestion)
      .collect(java.util.stream.Collectors.toSet());
    assertThat(questions).contains("Q3");
    assertThat(questions).contains("Q4"); // '  Q4  ' doit être trim -> 'Q4'

    java.util.Map<String, String> savedMap = saved.stream()
      .collect(java.util.stream.Collectors.toMap(AiAnswer::getQuestion, AiAnswer::getAnswer));
    assertThat(savedMap.get("Q3")).isEqualTo("A3");
    assertThat(savedMap.get("Q4")).isEqualTo("A4"); // '  A4  ' doit être trim -> 'A4'

    assertThat(saved)
        .as("Chaque réponse doit référencer le projet résolu")
        .allMatch(a -> a.getProject() == project);
  }

  @Test
  void saveAnswers_shouldThrowAiAnswerNotFoundException_whenAllEntriesInvalid() {
    // Given
    Long projectId = 12L;
    Project project = new Project();
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

    java.util.Map<String, String> data = new java.util.HashMap<>();
    data.put(" ", " ");    // invalide
    data.put(null, null);  // invalide
    data.put("Q1", " ");   // invalide

    // When / Then
    assertThatExceptionOfType(AiAnswerNotFoundException.class)
        .isThrownBy(() -> aiAnswerService.saveAnswers(projectId, data));

    // delete est appelé (car data non vide), mais saveAll ne doit pas l'être
    verify(aiAnswerRepository).deleteByProject_ProjectId(projectId);
    verify(aiAnswerRepository, never()).saveAll(anyList());
  }

  @Test
  void saveAnswers_shouldBubbleUpUnexpectedRepositoryError() {
    // Given
    Long projectId = 13L;
    Project project = new Project();
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    java.util.Map<String, String> data = java.util.Map.of("Q1", "A1");

    // Simule une erreur inattendue lors du delete
    doThrow(new RuntimeException("repo failure")).when(aiAnswerRepository).deleteByProject_ProjectId(projectId);

    // When / Then
    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> aiAnswerService.saveAnswers(projectId, data));
    verify(aiAnswerRepository, never()).saveAll(anyList());
  }

  @Test
  void getAnswersByProject_shouldReturnAnswers() {
    // Given
    Long projectId = 20L;
    AiAnswer a1 = new AiAnswer();
    a1.setQuestion("Q1"); a1.setAnswer("A1");
    AiAnswer a2 = new AiAnswer();
    a2.setQuestion("Q2"); a2.setAnswer("A2");

    when(aiAnswerRepository.findByProject_ProjectId(projectId)).thenReturn(java.util.List.of(a1, a2));

    // When
    java.util.List<AiAnswer> result = aiAnswerService.getAnswersByProject(projectId);

    // Then
    assertThat(result).hasSize(2);
    verify(aiAnswerRepository).findByProject_ProjectId(projectId);
  }

}
