package com.elit.outildedecision.shared.port;

import com.elit.outildedecision.shared.util.ReportTypes;

/** Contract for validating collaborative report ownership. */
public interface CollaborationReportOwnershipPort {
  /**
   * Checks whether a collaborative analysis exists and belongs to the project.
   *
   * @param type the type of collaborative report
   * @param analyseId the analysis identifier
   * @param projectId the project identifier
   * @return true when the referenced analysis belongs to the project
   */
  boolean belongsToProject(ReportTypes type, Long analyseId, Long projectId);
}
