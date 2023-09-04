
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.PredictibilityScoreModel;
import com.elit.outildedecision.service.PredictibilityAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PredictibilityAnalysisController.
 */
@RestController
@RequestMapping("/")
public class PredictibilityAnalysisController {

  /**
   * PredictibilityAnalysisService.
   */
  private final PredictibilityAnalysisService predictibilityAnalysisService;
  /**
   * Constructor for PredictibilityAnalysisController.
   * dependency injection for PredictibilityAnalysisService
   * @param predictibilityAnalysisService PredictibilityAnalysisService
   */
  public PredictibilityAnalysisController(
      final PredictibilityAnalysisService predictibilityAnalysisService) {
    this.predictibilityAnalysisService = predictibilityAnalysisService;
  }

  /**
   * Create a new predictability analyse.
   * @param projectId projectId
   * @return ResponseEntity<Long>
   */
  @PostMapping("/predictibility-analysis/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  ResponseEntity<Long> createPredictibilityAnalysis(@PathVariable final Long projectId) throws ProjectNotFoundException {
    return predictibilityAnalysisService.createPredictibilityForm(projectId);
  }

  /**
   * Get the predictability analyse score.
   * @param projectId projectId
   * @param analyseId analyseId
   * @return ResponseEntity<PredictibilityScoreModel>
   * @throws ProjectNotFoundException when the project is not found in the database
   * @throws PredictibilityAnalysisNotFoundException when the predictibility analyse is not found in the database
   */
  @GetMapping("/{projectId}/predictibility/{analyseId}/score")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId) or isObserver(#projectId)")
  public ResponseEntity<PredictibilityScoreModel> getPredictabilityAnalysisScore(@PathVariable final Long projectId,
    @PathVariable final Long analyseId) throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    return predictibilityAnalysisService.calculatePredictibilityAnalysisScore(projectId, analyseId);
  }

  /**
   * Get the predictability analyse score.
   * @param projectId projectId
   * @param analyseId analyseId
   * @return ResponseEntity<PredictibilityScoreModel>
   * @throws PredictibilityAnalysisNotFoundException when the predictibility analyse is not found in the database
   * @throws PredictibilityAnalysisAlreadyUpdated when the predictibility analyse is already updated
   */
  @PutMapping("/{projectId}/predictibility/isFinished/{analyseId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> putIsFinishedPredictibilityAnalysis(@PathVariable final Long projectId, @PathVariable final Long analyseId)
    throws PredictibilityAnalysisNotFoundException, PredictibilityAnalysisAlreadyUpdated, ProjectNotFoundException {
    return predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(projectId, analyseId);
  }

  /**
   * Increase the iteration number of the predictibility analyse.
   * @param projectId projetcId
   * @param analyseId analyseId
   * @return ResponseEntity<String>
   * @throws UserNotAuthorizedException UserNotAuthorizedException
   * @throws PredictibilityAnalysisNotFoundException PredictibilityAnaliseNotFoundException
   */
  @PutMapping("/{projectId}/predictibility/iteration/{analyseId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> increasePredictibilityAnalyseIterationNumber(@PathVariable final Long projectId,
   @PathVariable final Long analyseId)
    throws UserNotAuthorizedException, PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    return predictibilityAnalysisService.increasePredictAnalysisIterationNum(projectId, analyseId);
  }
}
