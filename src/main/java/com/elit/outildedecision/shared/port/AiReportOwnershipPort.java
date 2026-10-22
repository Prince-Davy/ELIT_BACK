package com.elit.outildedecision.shared.port;

/** Checks that an AI result exists and belongs to the requested project. */
public interface AiReportOwnershipPort {
  /**
   * Checks that the AI result exists and belongs to the project.
   *
   * @param analyseId the report analysis identifier
   * @param projectId the project identifier
   * @return true if the record belongs to the project
   */
  boolean belongsToProject(Long analyseId, Long projectId);
}
