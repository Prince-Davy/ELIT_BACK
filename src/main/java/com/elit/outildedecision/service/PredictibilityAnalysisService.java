
package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.PredictibilityScoreModel;
import org.springframework.http.ResponseEntity;
/**
 * This interface defines the contract for the Predictibility Analysis Service, which provides methods to create, retrieve,
 * update, and calculate scores for predictibility analyses.
 */
public interface PredictibilityAnalysisService {

  /**
   * Creates a new predictibility analysis form for the specified project.
   *
   * @param projectId the ID of the project for which to create the form
   * @return a ResponseEntity containing the ID of the newly created predictibility analysis
   */
  ResponseEntity<Long> createPredictibilityForm(Long projectId);

  /**
   * Calculates the score for the specified predictibility analysis.
   *
   * @param analyseId the ID of the predictibility analysis for which to calculate the score
   * @return a ResponseEntity containing the calculated score
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws PredictibilityAnalysisNotFoundException if the specified predictibility analysis does not exist
   */
  ResponseEntity<PredictibilityScoreModel> calculatePredictibilityAnalysisScore(Long analyseId)
    throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException;

  /**
   * Sets the "isFinished" flag for the specified predictibility analysis to true.
   *
   * @param analyseId the ID of the predictibility analysis for which to set the flag
   * @return a ResponseEntity indicating the success or failure of the operation
   * @throws UserNotAuthorizedException if the user is not authorized to update the specified predictibility analysis
   * @throws PredictibilityAnalysisNotFoundException if the specified predictibility analysis does not exist
   * @throws PredictibilityAnalysisAlreadyUpdated if the specified predictibility analysis has already been updated
   */
  ResponseEntity<String> putIsFinishedPredictibilityAnalysis(Long analyseId)
    throws UserNotAuthorizedException, PredictibilityAnalysisNotFoundException, PredictibilityAnalysisAlreadyUpdated;

  /**
   * Increases the iteration count for the specified predictibility analysis.
   *
   * @param analyseId the ID of the predictibility analysis for which to increase the iteration count
   * @return a ResponseEntity indicating the success or failure of the operation
   * @throws PredictibilityAnalysisNotFoundException if the specified predictibility analysis does not exist
   * @throws UserNotAuthorizedException if the user is not authorized to update the specified predictibility analysis
   */
  ResponseEntity<String> increasePredictAnalysisIterationNum(Long analyseId)
    throws PredictibilityAnalysisNotFoundException, UserNotAuthorizedException;
}
