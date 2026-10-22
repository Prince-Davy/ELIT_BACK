package com.elit.outildedecision.shared.port;

/** Checks that an audit exists and belongs to the requested project. */
public interface AuditReportOwnershipPort {
  boolean belongsToProject(Long analyseId, Long projectId);
}
