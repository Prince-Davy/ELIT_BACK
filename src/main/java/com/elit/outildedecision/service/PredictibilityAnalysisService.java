package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.PredictibilityAnaliseNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.PredictibilityScoreModel;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface PredictibilityAnalysisService {

    ResponseEntity<Long> createPredictibilityForm(Long projectId);

    ResponseEntity<PredictibilityScoreModel>  calculatePredictibilityAnalysisScore(Long analyseId) throws ProjectNotFoundException;

    ResponseEntity<String> putIsFinishedPredictibilityAnalysis(Long analyseId) throws UserNotAuthorizedException, PredictibilityAnaliseNotFoundException;

    ResponseEntity<String> increasePredictAnalysisIterationNum(Long analyseId) throws PredictibilityAnaliseNotFoundException, UserNotAuthorizedException;
}
