package com.elit.outildedecision.features.audit;

import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for audit operations.
 */
@RestController
@RequestMapping("/audit")
public class AuditController {

  private final AuditService auditService;

  /**
   * Constructor for AuditController.
   *
   * @param auditService the audit service
   */
  public AuditController(final AuditService auditService) {
    this.auditService = auditService;
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
}
