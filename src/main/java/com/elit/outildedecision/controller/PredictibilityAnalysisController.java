
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.PredictibilityScoreModel;
import com.elit.outildedecision.service.PredictibilityAnalysisService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
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
  ResponseEntity<Long> createPredictibilityAnalysis(@PathVariable final Long projectId) {
    return predictibilityAnalysisService.createPredictibilityForm(projectId);
  }
  /**
   * Get the predictability analyse score.
   * @param analyseId analyseId
   * @return ResponseEntity<PredictibilityScoreModel>
   * @throws ProjectNotFoundException when the project is not found in the database
   * @throws PredictibilityAnalysisNotFoundException when the predictibility analyse is not found in the database
   */
  @GetMapping("/predictibility/{analyseId}/score")
  public ResponseEntity<PredictibilityScoreModel> getPredictabilityAnalysisScore(
      @PathVariable final Long analyseId) throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    return predictibilityAnalysisService.calculatePredictibilityAnalysisScore(analyseId);
  }
  /**
   * Get the predictability analyse score.
   * @param analyseId analyseId
   * @return ResponseEntity<PredictibilityScoreModel>
   * @throws PredictibilityAnalysisNotFoundException when the predictibility analyse is not found in the database
   * @throws UserNotAuthorizedException when the user is not authorized to access the project
   * @throws PredictibilityAnalysisAlreadyUpdated when the predictibility analyse is already updated
   */
  @PutMapping("predictibility/isFinished/{analyseId}")
  public ResponseEntity<String> putIsFinishedPredictibilityAnalysis(@PathVariable final Long analyseId)
    throws UserNotAuthorizedException, PredictibilityAnalysisNotFoundException, PredictibilityAnalysisAlreadyUpdated {
    return predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(analyseId);
  }
  /**
   * Increase the iteration number of the predictibility analyse.
   * @param analyseId analyseId
   * @return ResponseEntity<String>
   * @throws UserNotAuthorizedException UserNotAuthorizedException
   * @throws PredictibilityAnalysisNotFoundException PredictibilityAnaliseNotFoundException
   */
  // @ApiOperation(value = "Increase the iteration number of the predictibility analyse")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully"),
        @ApiResponse(
            responseCode = "403",
            description = " PredictibilityAnaliseNotFoundException, UserNotAuthorizedException")
      })
  @PutMapping("predictibility/iteration/{analyseId}")
  public ResponseEntity<String> increasePredictibilityAnalyseIterationNumber(
       @PathVariable final Long analyseId)
      throws UserNotAuthorizedException, PredictibilityAnalysisNotFoundException {
    return predictibilityAnalysisService.increasePredictAnalysisIterationNum(analyseId);
  }
}
