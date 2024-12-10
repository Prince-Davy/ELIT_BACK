package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.model.ResultAiModel;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ResultIaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultIaServiceImplTest {

  @Mock
  private ResultIaRepository resultIaRepository;

  @Mock
  private ProjectRepository projectRepository;

  @InjectMocks
  private ResultIaServiceImpl resultIaService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getResultIa_Success() throws ProjectNotFoundException, ResponseNotFoundException {
    long projectId = 1L;
    Project project = new Project();
    project.setProjectId(projectId);
    ResultAi resultAi = new ResultAi(1L, "Agile", 0.85, project);

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi));

    ResultAiModel result = resultIaService.getResultIa(projectId);
    List<AnswerIa> answerIa = List.of(new AnswerIa("Question", "Category", "Response", project));
    ResponseEntity<Object> response = resultIaService.generateAndSaveResult(projectId, answerIa);

    // Vérifications sur la logique réelle
    assertNotNull(response);
    assertNotNull(result);
    assertEquals("Agile", result.getPredictedMethod());
    assertEquals(0.85, result.getPredictedProba());
  }

  @Test
  void getResultIa_ProjectNotFound() {
    long projectId = 1L;

    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

    assertThrows(ProjectNotFoundException.class, () -> resultIaService.getResultIa(projectId));
  }

  @Test
  void getResultIa_ResponseNotFound() {
    long projectId = 1L;
    Project project = new Project();
    project.setProjectId(projectId);

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.empty());

    assertThrows(ResponseNotFoundException.class, () -> resultIaService.getResultIa(projectId));
  }

  @Test
  void deleteResultAnalysisAI_Success() throws ResponseNotFoundException {
    long resultIaId = 1L;
    ResultAi resultAi = new ResultAi();
    resultAi.setId(resultIaId);

    when(resultIaRepository.findById(resultIaId)).thenReturn(Optional.of(resultAi));

    ResponseEntity<String> response = resultIaService.deleteResultAnalysisAI(resultIaId);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Le résultat de l'analyse IA pour ce projet a bien été supprimé", response.getBody());
    verify(resultIaRepository, times(1)).deleteById(resultIaId);
  }

  @Test
  void deleteResultAnalysisAI_ResponseNotFound() {
    long resultIaId = 1L;

    when(resultIaRepository.findById(resultIaId)).thenReturn(Optional.empty());

    assertThrows(ResponseNotFoundException.class, () -> resultIaService.deleteResultAnalysisAI(resultIaId));
  }
}
