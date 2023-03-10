package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.PredictibilityScoreModel;
import org.springframework.http.ResponseEntity;

public interface PredictibilityAnalysisService {

  ResponseEntity<Long> createPredictibilityForm(Long projectId);

  ResponseEntity<PredictibilityScoreModel> calculatePredictibilityAnalysisScore(Long analyseId)
    throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException;

  ResponseEntity<String> putIsFinishedPredictibilityAnalysis(Long analyseId)
          throws UserNotAuthorizedException, PredictibilityAnalysisNotFoundException, PredictibilityAnalysisAlreadyUpdated;

  ResponseEntity<String> increasePredictAnalysisIterationNum(Long analyseId)
      throws PredictibilityAnalysisNotFoundException, UserNotAuthorizedException;
}
