package com.elit.outildedecision.ai.result.adapter;

import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.shared.port.AiReportOwnershipPort;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** AI-context adapter implementing the shared report ownership contract. */
@Component
public class AiReportOwnershipAdapter implements AiReportOwnershipPort {
  private final ResultIaRepository repository;

  public AiReportOwnershipAdapter(final ResultIaRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean belongsToProject(final Long analyseId, final Long projectId) {
    return projectId != null && repository.findById(analyseId)
      .filter(result -> result.getProject() != null
        && Objects.equals(result.getProject().getProjectId(), projectId))
      .isPresent();
  }
}
