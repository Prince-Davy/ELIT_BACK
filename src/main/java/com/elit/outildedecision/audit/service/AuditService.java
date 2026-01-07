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

  /**
   * Get a specific audit by project ID and audit ID.
   *
   * @param projectId the project ID
   * @param auditId the audit ID
   * @return the audit response model
   * @throws ProjectNotFoundException if project not found
   * @throws AuditNotFoundException if audit not found
   */
  AuditResponseModel getAuditByProjectAndAuditId(Long projectId, Long auditId) throws ProjectNotFoundException, AuditNotFoundException;

  /**
   * Vérifie si un projet existe.
   * @param projectId l'identifiant du projet
   * @return true si le projet existe, sinon false
   */
  boolean projectExists(Long projectId);

  /**
   * Vérifie si un projet possède au moins un audit.
   * @param projectId l'identifiant du projet
   * @return true si le projet a au moins un audit, sinon false
   * @throws ProjectNotFoundException si le projet n'existe pas
   */
  boolean hasAuditForProject(Long projectId) throws ProjectNotFoundException;
}
