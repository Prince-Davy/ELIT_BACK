package com.elit.outildedecision.collaboration.predictibility.service;

import com.elit.outildedecision.collaboration.predictibility.dto.ChosenMethodologyRequest;
import com.elit.outildedecision.collaboration.predictibility.dto.PredictibilityScoreResponse;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;

/**
 * Contract for the predictibility analysis feature: creation of the analysis form, methodology
 * scoring, final methodology selection, completion flag and iteration handling.
 */
public interface PredictibilityAnalysisService {

  /**
   * Creates (or returns the existing) predictibility analysis for a project's latest multi-criteria
   * analysis.
   *
   * @param projectId the project id
   * @return the created (or existing) predictibility analysis id
   * @throws ProjectNotFoundException if the project does not exist
   */
  Long createPredictibilityForm(Long projectId) throws ProjectNotFoundException;

  /**
   * Stores the final methodology chosen by the team.
   *
   * @param request the request carrying the {@code chosenMethodology} value
   * @param projectId the project id
   * @param analyseId the predictibility analysis id
   * @return a confirmation message holding the persisted methodology
   * @throws ProjectNotFoundException if the project does not exist
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   */
  String addFinalChosenMethodology(ChosenMethodologyRequest request, Long projectId, Long analyseId)
      throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException;

  /**
   * Computes the predictibility score for an analysis (per-methodology aggregation of confirmed
   * votes) and persists the results.
   *
   * @param projectId the project id
   * @param analyseId the predictibility analysis id
   * @return the computed predictibility score
   * @throws ProjectNotFoundException if the analysis does not belong to the given project
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   */
  PredictibilityScoreResponse calculatePredictibilityAnalysisScore(Long projectId, Long analyseId)
      throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException;

  /**
   * Marks the predictibility analysis as finished.
   *
   * @param projectId the project id
   * @param analyseId the predictibility analysis id
   * @return a confirmation message
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   * @throws PredictibilityAnalysisAlreadyUpdated if the analysis is already finished
   * @throws ProjectNotFoundException if the analysis does not belong to the given project
   */
  String putIsFinishedPredictibilityAnalysis(Long projectId, Long analyseId)
      throws PredictibilityAnalysisNotFoundException, PredictibilityAnalysisAlreadyUpdated,
          ProjectNotFoundException;

  /**
   * Increases the iteration number of the predictibility analysis.
   *
   * @param projectId the project id
   * @param analyseId the predictibility analysis id
   * @return a confirmation message
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   * @throws ProjectNotFoundException if the analysis does not belong to the given project
   */
  String increasePredictAnalysisIterationNum(Long projectId, Long analyseId)
      throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException;
}
