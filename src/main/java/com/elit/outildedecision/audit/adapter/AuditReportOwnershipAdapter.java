package com.elit.outildedecision.audit.adapter;

import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.shared.port.AuditReportOwnershipPort;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Audit-context adapter implementing the shared report ownership contract. */
@Component
public final class AuditReportOwnershipAdapter implements AuditReportOwnershipPort {
  private final AuditRepository repository;

  /**
   * Creates an ownership adapter.
   *
   * @param repository repository for audit records
   */
  public AuditReportOwnershipAdapter(final AuditRepository repository) {
    this.repository = repository;
  }

  /**
   * Verifies that the audit belongs to the project.
   *
   * @param analyseId the report analysis identifier
   * @param projectId the project identifier
   * @return true if a matching record belongs to this project
   */
  @Override
  public boolean belongsToProject(final Long analyseId, final Long projectId) {
    return projectId != null && repository.findById(analyseId)
      .filter(audit -> audit.getProject() != null
        && Objects.equals(audit.getProject().getProjectId(), projectId))
      .isPresent();
  }
}
