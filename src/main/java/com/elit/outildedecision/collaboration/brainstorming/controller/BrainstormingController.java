package com.elit.outildedecision.collaboration.brainstorming.controller;

import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingRequest;
import com.elit.outildedecision.collaboration.brainstorming.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.collaboration.brainstorming.exception.ExistingBrainstormingException;
import com.elit.outildedecision.collaboration.brainstorming.service.BrainstormingService;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/** Brainstorming controller class. */
@RestController
@RequestMapping("/brainstorming")
@Tag(name = "Collaboration - Brainstorming", description = "Gestion des sessions de brainstorming")
public class BrainstormingController {
  /**
   * The brainstorming service.
   */
  private final BrainstormingService brainstormingService;

  /**
   *
   * BrainstormingController Constructor.
   * Dependency Injection of BrainstormingService
   * @param brainstormingService : the brainstorming service
   */
  public BrainstormingController(final BrainstormingService brainstormingService) {
    this.brainstormingService = brainstormingService;
  }

  /**
   * @param projectId the id of the project
   * @param predictibilityAnalysisId : the predictibility analysis id
   * @return ResponseEntity&lt;Object&gt;
   * @throws PredictibilityAnalysisNotFoundException : if the predictibility analysis is not found
   * @throws ProjectNotFoundException : if the analysis does not belong to the project
   */
  @GetMapping("/{projectId}/{predictibilityAnalysisId}")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId) or isObserver(#projectId)")
  public ResponseEntity<Object> getBrainstorming(@PathVariable final Long projectId, @PathVariable final Long predictibilityAnalysisId)
    throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    return ResponseEntity.ok().body(brainstormingService.getBrainstorming(projectId, predictibilityAnalysisId));
  }

  /**
   * Get the brainstormings.
   * @param projectId the id of the project
   * @param predictibilityAnalysisId : the predictibility analysis id
   * @return ResponseEntity&lt;Object&gt;
   * @throws PredictibilityAnalysisNotFoundException : if the predictibility analysis is not found
   * @throws ProjectNotFoundException : if the analysis does not belong to the project
   */
  @GetMapping("/abstract/{projectId}/{predictibilityAnalysisId}")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId)")
  public ResponseEntity<Object> getBrainstormingAbstract(
    @PathVariable final Long projectId, @PathVariable final Long predictibilityAnalysisId) throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    return ResponseEntity.ok().body(brainstormingService.getBrainstormingAbstract(projectId, predictibilityAnalysisId));
  }

  /**
   * Get the brainstorming abstract from the predictibility analysis id alone.
   * @param prevId : the predictibility analysis id
   * @return ResponseEntity&lt;Object&gt;
   * @throws PredictibilityAnalysisNotFoundException : if the predictibility analysis is not found
   */
  @GetMapping("/abstract/{prevId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Object> getBrainstormingAbstractByAnalysisId(@PathVariable final Long prevId)
    throws PredictibilityAnalysisNotFoundException {
    return ResponseEntity.ok().body(brainstormingService.getBrainstormingAbstractByAnalysisId(prevId));
  }

  /**
   * Create a new brainstorming.
   *
   * @param projectId  the id of the project
   * @param request : the brainstorming payload
   * @return ResponseEntity&lt;Long&gt; the id of the created brainstorming
   * @throws PredictibilityAnalysisNotFoundException : if the predictibility analysis is not found
   * @throws ExistingBrainstormingException : if the brainstorming already exists
   * @throws ProjectNotFoundException : if the analysis does not belong to the project
   */
  @PostMapping("/{projectId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<Long> createBrainstorming(
    @PathVariable final Long projectId, @Valid @RequestBody final BrainstormingRequest request) throws PredictibilityAnalysisNotFoundException, ExistingBrainstormingException, ProjectNotFoundException {
    return ResponseEntity.ok().body(brainstormingService.createBrainstorming(projectId, request));
  }

  /**
   * Update a brainstorming.
   * @param projectId the id of the project
   * @param request : the brainstorming payload
   * @return ResponseEntity&lt;String&gt;
   * @throws BrainstormingNotFoundException : if the brainstorming is not found
   * @throws ProjectNotFoundException : if the brainstorming does not belong to the project
   */
  @PutMapping("/{projectId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> updateBrainstorming(
    @PathVariable final Long projectId, @RequestBody final BrainstormingRequest request) throws BrainstormingNotFoundException, ProjectNotFoundException {
    return ResponseEntity.ok().body(brainstormingService.updateBrainstorming(projectId, request));
  }

  /**
   * Delete a brainstorming.
   * @param projectId the id of the project
   * @param brainstormingId : the brainstorming id
   * @return ResponseEntity&lt;String&gt;
   * @throws BrainstormingNotFoundException : if the brainstorming is not found
   * @throws ProjectNotFoundException : if the brainstorming does not belong to the project
   */
  @DeleteMapping("/{projectId}/{brainstormingId}")
  @PreAuthorize("isProjectOwner(#projectId) or isContributer(#projectId)")
  public ResponseEntity<String> removeBrainstorming(@PathVariable final Long projectId, @PathVariable final Long brainstormingId)
    throws BrainstormingNotFoundException, ProjectNotFoundException {
    return ResponseEntity.ok().body(brainstormingService.deleteBrainstorming(projectId, brainstormingId));
  }

  /**
   * Validate or reject a brainstorming.
   * @param projectId the id of the project
   * @param brainstormingId : the brainstorming id
   * @param isChecked : true if the brainstorming is validated, false if it is rejected
   * @return ResponseEntity&lt;String&gt;
   * @throws BrainstormingNotFoundException : if the brainstorming is not found
   * @throws JsonProcessingException : if the json is not well formatted
   * @throws ProjectNotFoundException : if the brainstorming does not belong to the project
   */
  @PutMapping("/validate/{projectId}/{brainstormingId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> validateOrRejectBrainstorming(
    @PathVariable final Long projectId, @PathVariable final Long brainstormingId, final HttpEntity<String> isChecked)
    throws BrainstormingNotFoundException, JsonProcessingException, ProjectNotFoundException {
    return ResponseEntity.ok().body(brainstormingService.validateOrRejectBrainstorming(projectId, brainstormingId, isChecked));
  }
}
