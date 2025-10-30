package com.elit.outildedecision.audit.controller;

import com.elit.outildedecision.audit.model.AuditRequestModel;
import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.AuditScoreDTO;
import com.elit.outildedecision.audit.service.AuditScoreService;
import com.elit.outildedecision.audit.service.AuditService;
import com.elit.outildedecision.exception.AuditNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

/**
 * Controller for audit operations.
 */
@RestController
@RequestMapping("/audit")
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
   * @param auditRequest the audit request model
   * @return the created audit response
   * @throws ProjectNotFoundException if project not found
   * @throws UserNotFoundException    if user not found
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthority('USER') and (isProjectOwner(#auditRequest.projectId) or isContributer(#auditRequest.projectId))")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Audit created successfully"),
      @ApiResponse(responseCode = "404", description = "Project or User not found"),
      @ApiResponse(responseCode = "403", description = "Access denied")
  })
  public ResponseEntity<AuditResponseModel> createAudit(@Valid @RequestBody final AuditRequestModel auditRequest)
      throws ProjectNotFoundException, UserNotFoundException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails user = (UserDetails) auth.getPrincipal();

    return auditService.createAudit(auditRequest, user.getUsername());
  }

  /**
   * Get audits by project ID.
   *
   * @param projectId the project ID
   * @return list of audits
   * @throws ProjectNotFoundException if project not found
   */
  @GetMapping("/project/{projectId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Audits retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Project not found"),
      @ApiResponse(responseCode = "403", description = "Access denied")
  })
  public List<AuditResponseModel> getAuditsByProject(@PathVariable final Long projectId)
      throws ProjectNotFoundException {
    return auditService.getAuditsByProject(projectId);
  }

  /**
   * Get audits by project ID with pagination.
   *
   * @param projectId the project ID
   * @param page      page number (default: 0)
   * @param size      page size (default: 10)
   * @return page of audits
   * @throws ProjectNotFoundException if project not found
   */
  @GetMapping("/project/{projectId}/paginated")
  @PreAuthorize("hasAnyAuthority('USER', 'ADMIN') and (isProjectOwner(#projectId) or isContributer(#projectId) or isObserver(#projectId))")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Audits retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Project not found"),
      @ApiResponse(responseCode = "403", description = "Access denied")
  })
  public Page<AuditResponseModel> getAuditsByProjectPaginated(
      @PathVariable final Long projectId,
      @RequestParam(defaultValue = "0") final int page,
      @RequestParam(defaultValue = "10") final int size)
      throws ProjectNotFoundException {
    return auditService.getAuditsByProjectPaginated(projectId, page, size);
  }

  /**
   * update audit entry.
   *
   * @param auditRequest the audit request model
   * @return the created audit response
   * @throws UserNotFoundException    if user not found
   */
  @PostMapping("/updateAudit")
  @PreAuthorize("hasAnyAuthority('USER') and (isProjectOwner(#auditRequest.projectId) or isContributer(#auditRequest.projectId))")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Audit update successfully"),
          @ApiResponse(responseCode = "404", description = "Project or User not found"),
          @ApiResponse(responseCode = "403", description = "Access denied")
  })
  public ResponseEntity<AuditResponseModel> updateAudit(@Valid @RequestBody final AuditRequestModel auditRequest)
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
   */
  @PostMapping("/{auditId}/calculate")
  @PreAuthorize("hasAnyAuthority('USER') and (isProjectOwnerByAuditId(#auditId))")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Score calculated and saved successfully"),
    @ApiResponse(responseCode = "404", description = "Audit not found"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  public ResponseEntity<AuditScoreDTO> calculateScoreOfTheAudit(@PathVariable final Long auditId) throws AuditNotFoundException {
    try {
      AuditScoreDTO auditScoreDTO = this.auditService.calculateScoringOfAudit(auditId);
      return ResponseEntity.ok(auditScoreDTO);
    }
    catch (AuditNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * find the Audit score.
   *
   * @param auditId the Audit ID
   * @return the score of the audit
   */
  @GetMapping("/{auditId}/auditScore")
  @PreAuthorize("hasAnyAuthority('USER') and (isProjectOwnerByAuditId(#auditId))")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Audit score found"),
    @ApiResponse(responseCode = "404", description = "Audit score not found"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  public ResponseEntity<AuditScoreDTO> findAuditScoreByAuditId(@PathVariable final Long auditId) throws EntityNotFoundException {
    try {
      AuditScoreDTO auditScoreDTO = this.auditScoreService.findAuditScoreByAuditId(auditId);
      return ResponseEntity.ok(auditScoreDTO);
    }
    catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
