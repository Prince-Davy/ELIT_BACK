package com.elit.outildedecision.ai.result.adapter;

import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.shared.port.AiReportOwnershipPort;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** AI-context adapter implementing the shared report ownership contract. */
@Component
public final class AiReportOwnershipAdapter implements AiReportOwnershipPort {
  private final ResultIaRepository repository;

  /**
   * Creates an ownership adapter.
   *
   * @param repository repository for AI result records
   */
  public AiReportOwnershipAdapter(final ResultIaRepository repository) {
    this.repository = repository;
  }

  /**
   * Verifies that the AI result belongs to the project.
   *
   * @param analyseId the report analysis identifier
   * @param projectId the project identifier
   * @return true if a matching record belongs to this project
   */
  @Override
  public boolean belongsToProject(final Long analyseId, final Long projectId) {
    return projectId != null && repository.findById(analyseId)
      .filter(result -> result.getProject() != null
        && Objects.equals(result.getProject().getProjectId(), projectId))
      .isPresent();
  }
}
