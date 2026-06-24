package com.elit.outildedecision.ai.result.service.implementation;

import com.elit.outildedecision.ai.result.dto.ResultResponse;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.ai.result.mapper.ResultMapper;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultIaServiceImplTest {

  @Mock
  private ResultIaRepository resultIaRepository;

  @Mock
  private ProjectRepository projectRepository;

  private ResultIaServiceImpl resultIaService;

  @BeforeEach
  void setUp() {
    ResultMapper resultMapper = Mappers.getMapper(ResultMapper.class);
    resultIaService = new ResultIaServiceImpl(null, resultIaRepository, projectRepository, null, null, resultMapper,
        new com.fasterxml.jackson.databind.ObjectMapper());
  }

  @Test
  void getResultIa_Success() throws ProjectNotFoundException, ResponseNotFoundException {
    // Arrange
    long projectId = 1L;
    Project project = new Project();
    project.setProjectId(projectId);
    ResultAi resultAi = new ResultAi(1L, "Agile", 0.85, project);

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi));

    // Act
    ResultResponse result = resultIaService.getResultIa(projectId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getPredictedMethod()).isEqualTo("Agile");
    assertThat(result.getPredictedProba()).isEqualTo(0.85);
    assertThat(result.getProjectId()).isEqualTo(projectId);

    // Verify interactions
    verify(projectRepository, times(1)).findById(projectId);
    verify(resultIaRepository, times(1)).findByProject(project);
    verifyNoMoreInteractions(projectRepository, resultIaRepository);
  }


  @Test
  void getResultIa_ProjectNotFound() {
    long projectId = 1L;

    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> resultIaService.getResultIa(projectId));
  }

  @Test
  void getResultIa_ResponseNotFound() {
    long projectId = 1L;
    Project project = new Project();
    project.setProjectId(projectId);

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.empty());

    assertThatExceptionOfType(ResponseNotFoundException.class)
        .isThrownBy(() -> resultIaService.getResultIa(projectId));
  }

}
