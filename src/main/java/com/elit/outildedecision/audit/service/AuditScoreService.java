package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.dto.AuditScoreResponse;

/**
 * Service interface for managing audit scores.
 * Provides business logic for retrieving audit score data.
 */
public interface AuditScoreService {

  /**
   * Retrieves the audit score associated with a given audit ID.
   *
   * @param auditId The ID of the audit.
   * @return The {@link AuditScoreResponse} containing the score details.
   * @throws AuditNotFoundException if no audit score is found for the given audit ID.
   */
  AuditScoreResponse findAuditScoreByAuditId(Long auditId);
}
