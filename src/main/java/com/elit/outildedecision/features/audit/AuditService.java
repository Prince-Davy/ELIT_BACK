package com.elit.outildedecision.features.audit;

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
}
