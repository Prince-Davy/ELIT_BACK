package com.elit.outildedecision.audit.controller;

import com.elit.outildedecision.audit.dto.AuditRequest;
import com.elit.outildedecision.audit.dto.AuditResponse;
import com.elit.outildedecision.audit.dto.AuditScoreResponse;
import com.elit.outildedecision.audit.service.AuditScoreService;
import com.elit.outildedecision.audit.service.AuditService;
import com.elit.outildedecision.audit.exception.AuditNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for audit operations.
 */
@RestController
@RequestMapping("/audit")
@Tag(name = "Audit", description = "Gestion du parcours audit")
public class AuditController {

  private final AuditService auditService;
  private final AuditScoreService auditScoreService;

  /**
   * Constructor for AuditController.
   *
   * @param auditService the audit service
   * @param auditScoreService the audit score service
   */
  public AuditController(final AuditService auditService, final AuditScoreService auditScoreService) {
    this.auditService = auditService;
    this.auditScoreService = auditScoreService;
  }

  /**
   * Create a new audit entry.
   *
   * @param auditRequest the audit request
   * @return the created audit response
   * @throws ProjectNotFoundException if project not found
   * @throws UserNotFoundException    if user not found
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthority('USER') and (isProjectOwner(#auditRequest.projectId) or isContributer(#auditRequest.projectId))")
  public ResponseEntity<AuditResponse> createAudit(@Valid @RequestBody final AuditRequest auditRequest)
      throws ProjectNotFoundException, UserNotFoundException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails user = (UserDetails) auth.getPrincipal();

    return ResponseEntity.status(HttpStatus.CREATED).body(auditService.createAudit(auditRequest, user.getUsername()));
  }

  /**
   * Get a specific audit by project ID and audit ID.
   *
   * @param projectId the project ID
   * @param auditId the audit ID
   * @return the audit response
   * @throws ProjectNotFoundException if project not found
   * @throws AuditNotFoundException   if audit not found
   */
  @GetMapping("/project/{projectId}/audit/{auditId}")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and (isProjectOwner(#projectId) or isContributer(#projectId) or isObserver(#projectId))")
  public AuditResponse getAuditByProjectAndAuditId(
    @PathVariable final Long projectId,
    @PathVariable final Long auditId) throws ProjectNotFoundException, AuditNotFoundException {
    return auditService.getAuditByProjectAndAuditId(projectId, auditId);
  }

  /**
   * Get audits by project ID.
   *
   * @param projectId the project ID
   * @return list of audits
   * @throws ProjectNotFoundException if project not found
   */
  @GetMapping("/project/{projectId}")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and (isProjectOwner(#projectId) or isContributer(#projectId) or isObserver(#projectId))")
  public List<AuditResponse> getAuditsByProject(@PathVariable final Long projectId)
      throws ProjectNotFoundException {
    return auditService.getAuditsByProject(projectId);
  }

  /**
   * Update an audit entry.
   *
   * @param auditRequest the audit request
   * @return the updated audit response
   * @throws UserNotFoundException  if user not found
   * @throws AuditNotFoundException if audit not found
   */
  @PostMapping("/updateAudit")
  @PreAuthorize("hasAnyAuthority('USER') and (isProjectOwner(#auditRequest.projectId) or isContributer(#auditRequest.projectId))")
  public AuditResponse updateAudit(@Valid @RequestBody final AuditRequest auditRequest)
    throws UserNotFoundException, AuditNotFoundException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails user = (UserDetails) auth.getPrincipal();

    return auditService.updateAudit(auditRequest, user.getUsername());
  }

  /**
   * Calculate the score of the audit.
   *
   * @param auditId the Audit ID
   * @return the score of the audit
   * @throws AuditNotFoundException if audit not found
   */
  @PostMapping("/{auditId}/calculate")
  @PreAuthorize("hasAnyAuthority('USER') and (isProjectOwnerByAuditId(#auditId))")
  public AuditScoreResponse calculateScoreOfTheAudit(@PathVariable final Long auditId) throws AuditNotFoundException {
    return this.auditService.calculateScoringOfAudit(auditId);
  }


  /**
   * find the Audit score.
   *
   * @param auditId the Audit ID
   * @return the score of the audit
   * @throws AuditNotFoundException if no audit score exists for the audit
   */
  @GetMapping("/{auditId}/auditScore")
  @PreAuthorize("hasAnyAuthority('USER') and (isProjectOwnerByAuditId(#auditId) or isContributorOrObserverByAuditId(#auditId))")
  public AuditScoreResponse findAuditScoreByAuditId(@PathVariable final Long auditId) throws AuditNotFoundException {
    return this.auditScoreService.findAuditScoreByAuditId(auditId);
  }

  /**
   * Vérifie si un projet possède au moins un audit.
   *
   * @param projectId l'identifiant du projet
   * @return true si le projet a au moins un audit, sinon false
   * @throws ProjectNotFoundException if project not found
   */
  @GetMapping("/project/{projectId}/has-audit")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and (isProjectOwner(#projectId) or isContributer(#projectId) or isObserver(#projectId))")
  public boolean hasAuditForProject(@PathVariable final Long projectId) throws ProjectNotFoundException {
    return auditService.hasAuditForProject(projectId);
  }
}
