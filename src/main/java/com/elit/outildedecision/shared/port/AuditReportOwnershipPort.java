package com.elit.outildedecision.shared.port;

/** Checks that an audit exists and belongs to the requested project. */
public interface AuditReportOwnershipPort {
  /**
   * Checks that the audit exists and belongs to the project.
   *
   * @param analyseId the report analysis identifier
   * @param projectId the project identifier
   * @return true if the record belongs to the project
   */
  boolean belongsToProject(Long analyseId, Long projectId);
}
