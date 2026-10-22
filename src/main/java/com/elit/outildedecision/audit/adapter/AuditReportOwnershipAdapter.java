package com.elit.outildedecision.audit.adapter;

import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.shared.port.AuditReportOwnershipPort;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Audit-context adapter implementing the shared report ownership contract. */
@Component
public class AuditReportOwnershipAdapter implements AuditReportOwnershipPort {
  private final AuditRepository repository;

  public AuditReportOwnershipAdapter(final AuditRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean belongsToProject(final Long analyseId, final Long projectId) {
    return projectId != null && repository.findById(analyseId)
      .filter(audit -> audit.getProject() != null
        && Objects.equals(audit.getProject().getProjectId(), projectId))
      .isPresent();
  }
}
