package com.elit.outildedecision.shared.port;

/** Checks that an AI result exists and belongs to the requested project. */
public interface AiReportOwnershipPort {
  boolean belongsToProject(Long analyseId, Long projectId);
}
