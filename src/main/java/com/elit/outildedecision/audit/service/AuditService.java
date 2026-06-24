package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.dto.AuditRequest;
import com.elit.outildedecision.audit.dto.AuditResponse;
import com.elit.outildedecision.audit.dto.AuditScoreResponse;
import com.elit.outildedecision.audit.exception.AuditNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;

import java.util.List;

/**
 * Service interface for Audit operations.
 */
public interface AuditService {

  /**
   * Create a new audit entry.
   *
   * @param auditRequest the audit request
   * @param userEmail    the user email
   * @return the created audit response
   * @throws ProjectNotFoundException if project not found
   * @throws UserNotFoundException    if user not found
   */
  AuditResponse createAudit(AuditRequest auditRequest, String userEmail)
      throws ProjectNotFoundException, UserNotFoundException;

  /**
   * Get audits by project ID.
   *
   * @param projectId the project ID
   * @return list of audits
   * @throws ProjectNotFoundException if project not found
   */
  List<AuditResponse> getAuditsByProject(Long projectId) throws ProjectNotFoundException;

  /**
   * Update an audit entry.
   *
   * @param auditRequest the audit request
   * @param userEmail    the user email
   * @return the updated audit response
   * @throws UserNotFoundException  if user not found
   * @throws AuditNotFoundException if audit not found
   */
  AuditResponse updateAudit(AuditRequest auditRequest, String userEmail) throws UserNotFoundException, AuditNotFoundException;

  /**
   * Calculate the scoring of the audit entry.
   *
   * @param auditId the audit ID
   * @return the audit scoring
   * @throws AuditNotFoundException if audit not found
   */
  AuditScoreResponse calculateScoringOfAudit(Long auditId) throws AuditNotFoundException;

  /**
   * Get a specific audit by project ID and audit ID.
   *
   * @param projectId the project ID
   * @param auditId   the audit ID
   * @return the audit response
   * @throws ProjectNotFoundException if project not found
   * @throws AuditNotFoundException   if audit not found
   */
  AuditResponse getAuditByProjectAndAuditId(Long projectId, Long auditId) throws ProjectNotFoundException, AuditNotFoundException;

  /**
   * Check whether a project exists.
   *
   * @param projectId the project ID
   * @return {@code true} if the project exists, {@code false} otherwise
   */
  boolean projectExists(Long projectId);

  /**
   * Check whether a project has at least one audit.
   *
   * @param projectId the project ID
   * @return {@code true} if the project has at least one audit, {@code false} otherwise
   * @throws ProjectNotFoundException if the project does not exist
   */
  boolean hasAuditForProject(Long projectId) throws ProjectNotFoundException;
}
