package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.model.AuditRequestModel;
import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.AuditScoreDTO;
import com.elit.outildedecision.exception.AuditNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Service interface for Audit operations.
 */
public interface AuditService {

  /**
   * Create a new audit entry.
   *
   * @param auditRequest the audit request model
   * @param userEmail    the user email
   * @return the created audit response
   * @throws ProjectNotFoundException if project not found
   * @throws UserNotFoundException    if user not found
   */
  ResponseEntity<AuditResponseModel> createAudit(AuditRequestModel auditRequest, String userEmail)
      throws ProjectNotFoundException, UserNotFoundException;

  /**
   * Get audits by project ID.
   *
   * @param projectId the project ID
   * @return list of audits
   * @throws ProjectNotFoundException if project not found
   */
  List<AuditResponseModel> getAuditsByProject(Long projectId) throws ProjectNotFoundException;

  /**
   * Get audits by project ID with pagination.
   *
   * @param projectId the project ID
   * @param page      page number
   * @param size      page size
   * @return page of audits
   * @throws ProjectNotFoundException if project not found
   */
  Page<AuditResponseModel> getAuditsByProjectPaginated(Long projectId, int page, int size)
      throws ProjectNotFoundException;

  /**
   * update audit entry.
   *
   * @param auditRequest the audit request model
   * @param userEmail    the user email
   * @return the created audit response
   * @throws UserNotFoundException    if user not found
   */
  ResponseEntity<AuditResponseModel> updateAudit(AuditRequestModel auditRequest, String userEmail) throws UserNotFoundException, AuditNotFoundException;

  /**
   * calculate the scoring of the Audit entry.
   *
   * @param auditId the audit ID
   * @return the audit Scoring
   */
  AuditScoreDTO calculateScoringOfAudit(Long auditId) throws AuditNotFoundException;
}
