
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.exception.ExistingBrainstormingException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.BrainstormingModel;
import com.elit.outildedecision.service.BrainstormingService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import javax.validation.Valid;

@RestController
@RequestMapping("/brainstorming")
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
   * @return ResponseEntity<Object>
   * @throws PredictibilityAnalysisNotFoundException : if the predictibility analysis is not found
   */
  @GetMapping("/{projectId}/{predictibilityAnalysisId}")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId) or isObserver(#projectId)")
  public ResponseEntity<Object> getBrainstorming(@PathVariable final Long projectId, @PathVariable final Long predictibilityAnalysisId)
    throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    return brainstormingService.getBrainstorming(projectId, predictibilityAnalysisId);
  }

  /**
   * Get the brainstormings.
   * @param projectId the id of the project
   * @param predictibilityAnalysisId : the predictibility analysis id
   * @return ResponseEntity<Object>
   * @throws PredictibilityAnalysisNotFoundException : if the predictibility analysis is not found
   */
  @GetMapping("/abstract/{projectId}/{predictibilityAnalysisId}")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId)")
  public ResponseEntity<Object> getBrainstormingAbstract(
    @PathVariable final Long projectId, @PathVariable final Long predictibilityAnalysisId) throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    return brainstormingService.getBrainstormingAbstract(projectId, predictibilityAnalysisId);
  }

  /**
   * Create a new brainstorming.
   *
   * @param projectId  the id of the project
   * @param brainstormingModel : the brainstorming model
   * @return ResponseEntity<String>
   * @throws PredictibilityAnalysisNotFoundException : if the predictibility analysis is not found
   * @throws ExistingBrainstormingException : if the brainstorming already exists
   */
  @PostMapping("/{projectId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> createBrainstorming(
    @PathVariable final Long projectId, @Valid @RequestBody final BrainstormingModel brainstormingModel) throws PredictibilityAnalysisNotFoundException, ExistingBrainstormingException, ProjectNotFoundException {
    return brainstormingService.createBrainstorming(projectId, brainstormingModel);
  }

  /**
   * Update a brainstorming.
   * @param projectId the id of the project
   * @param brainstormingModel : the brainstorming model
   * @return ResponseEntity<String>
   * @throws BrainstormingNotFoundException : if the brainstorming is not found
   */
  @PutMapping("/{projectId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> updateBrainstorming(
    @PathVariable final Long projectId, @RequestBody final BrainstormingModel brainstormingModel) throws BrainstormingNotFoundException, ProjectNotFoundException {
    return brainstormingService.updateBrainstorming(projectId, brainstormingModel);
  }

  /**
   * Delete a brainstorming.
   * @param projectId the id of the project
   * @param brainstormingId : the brainstorming id
   * @return ResponseEntity<String>
   * @throws BrainstormingNotFoundException : if the brainstorming is not found
   */
  @DeleteMapping("/{projectId}/{brainstormingId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> removeBrainstorming(@PathVariable final Long projectId, @PathVariable final Long brainstormingId)
    throws BrainstormingNotFoundException, ProjectNotFoundException {
    return brainstormingService.deleteBrainstorming(projectId, brainstormingId);
  }

  /**
   * Validate or reject a brainstorming.
   * @param projectId the id of the project
   * @param brainstormingId : the brainstorming id
   * @param isChecked : true if the brainstorming is validated, false if it is rejected
   * @return ResponseEntity<String>
   * @throws BrainstormingNotFoundException : if the brainstorming is not found
   * @throws JsonProcessingException : if the json is not well formatted
   */
  @PutMapping("/validate/{projectId}/{brainstormingId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> validateOrRejectBrainstorming(
    @PathVariable final Long projectId, @PathVariable final Long brainstormingId, final HttpEntity<String> isChecked)
    throws BrainstormingNotFoundException, JsonProcessingException, ProjectNotFoundException {
    return brainstormingService.validateOrRejectBrainstorming(projectId, brainstormingId, isChecked);
  }
}
