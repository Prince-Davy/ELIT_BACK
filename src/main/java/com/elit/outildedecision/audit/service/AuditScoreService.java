package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.model.AuditScoreDTO;

import jakarta.persistence.EntityNotFoundException;

/**
 * Service interface for managing audit scores.
 * Provides business logic for retrieving audit score data.
 */
public interface AuditScoreService {

  /**
   * Retrieves the audit score associated with a given audit ID.
   *
   * @param auditId The ID of the audit.
   * @return The {@link AuditScoreDTO} containing the score details.
   * @throws EntityNotFoundException if no audit score is found for the given audit ID.
   */
  AuditScoreDTO findAuditScoreByAuditId(Long auditId) throws EntityNotFoundException;
}
