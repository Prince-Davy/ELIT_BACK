package com.elit.outildedecision.ai.result.adapter;

import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiReportOwnershipAdapterTest {
  @Mock private ResultIaRepository repository;
  @InjectMocks private AiReportOwnershipAdapter adapter;

  @Test
  void acceptsResultBelongingToProject() {
    Project project = new Project();
    project.setProjectId(1L);
    ResultAi result = new ResultAi();
    result.setProject(project);
    when(repository.findById(10L)).thenReturn(Optional.of(result));
    assertTrue(adapter.belongsToProject(10L, 1L));
  }

  @Test
  void rejectsResultFromAnotherProject() {
    Project project = new Project();
    project.setProjectId(2L);
    ResultAi result = new ResultAi();
    result.setProject(project);
    when(repository.findById(10L)).thenReturn(Optional.of(result));
    assertFalse(adapter.belongsToProject(10L, 1L));
  }

  @Test
  void rejectsMissingResult() {
    when(repository.findById(10L)).thenReturn(Optional.empty());
    assertFalse(adapter.belongsToProject(10L, 1L));
  }

  @Test
  void rejectsResultWithoutProject() {
    when(repository.findById(10L)).thenReturn(Optional.of(new ResultAi()));
    assertFalse(adapter.belongsToProject(10L, 1L));
  }
}
