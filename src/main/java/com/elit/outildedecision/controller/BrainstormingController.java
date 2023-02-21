
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.model.BrainstormingModel;
import com.elit.outildedecision.service.BrainstormingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/brainstorming")
public class BrainstormingController {
  /**
   * The brainstorming service.
   */
  private final BrainstormingService brainstormingService;

  /**
   * BrainstormingController Constructor.
   * Dependency Injection of BrainstormingService
   * @param brainstormingService : the brainstorming service
   */
  public BrainstormingController(final BrainstormingService brainstormingService) {
    this.brainstormingService = brainstormingService;
  }

  /**
   * @param predictibilityAnalysisId : the predictibility analysis id
   * @return ResponseEntity<Object>
   * @throws Exception  : if the brainstorming is not found
   */
  @GetMapping("/{predictibilityAnalysisId}")
  public ResponseEntity<Object> getBrainstorming(@PathVariable final Long predictibilityAnalysisId)
      throws Exception {
    return brainstormingService.getBrainstorming(predictibilityAnalysisId);
  }

  /**
   * Get the brainstormings.
   * @param predictibilityAnalysisId : the predictibility analysis id
   * @return ResponseEntity<Object>
   * @throws Exception : if the brainstorming is not found
   */
  @GetMapping("/abstract/{predictibilityAnalysisId}")
  public ResponseEntity<Object> getBrainstormingAbstract(
      @PathVariable final Long predictibilityAnalysisId) throws Exception {
    return brainstormingService.getBrainstormingAbstract(predictibilityAnalysisId);
  }

  /**
   * Create a new brainstorming.
   * @param brainstormingModel : the brainstorming model
   * @return ResponseEntity<String>
   * @throws Exception : if the brainstorming is not found
   */
  @PostMapping("")
  public ResponseEntity<String> createBrainstorming(
      @Valid @RequestBody final BrainstormingModel brainstormingModel) throws Exception {
    return brainstormingService.createBrainstorming(brainstormingModel);
  }
  /**
   * Update a brainstorming.
   * @param brainstormingModel : the brainstorming model
   * @return ResponseEntity<String>
   * @throws Exception : if the brainstorming is not found
   */
  @PutMapping("")
  public ResponseEntity<String> updateBrainstorming(
      @RequestBody final BrainstormingModel brainstormingModel) throws Exception {
    return brainstormingService.updateBrainstorming(brainstormingModel);
  }
  /**
   * Delete a brainstorming.
   * @param brainstormingId : the brainstorming id
   * @return ResponseEntity<String>
   * @throws BrainstormingNotFoundException : if the brainstorming is not found
   */
  @DeleteMapping("/{brainstormingId}")
  public ResponseEntity<String> removeBrainstorming(@PathVariable final Long brainstormingId)
      throws BrainstormingNotFoundException {
    return brainstormingService.deleteBrainstorming(brainstormingId);
  }
  /**
   * Validate or reject a brainstorming.
   * @param brainstormingId : the brainstorming id
   * @param isChecked : true if the brainstorming is validated, false if it is rejected
   * @return ResponseEntity<String>
   * @throws BrainstormingNotFoundException : if the brainstorming is not found
   * @throws JsonProcessingException : if the json is not well formatted
   */
  @PutMapping("/validate/{brainstormingId}")
  public ResponseEntity<String> validateOrRejectBrainstorming(
      @PathVariable final Long brainstormingId, final HttpEntity<String> isChecked)
      throws BrainstormingNotFoundException, JsonProcessingException {
    return brainstormingService.validateOrRejectBrainstorming(brainstormingId, isChecked);
  }
}
