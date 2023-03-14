package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.EscoreException;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.FormQuestionModel;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface MultiCriteriaAnalysisService {
  ResponseEntity<String> createMulticriteriaAnalysisForm(Long projectId)
      throws IOException, ParseException,
          org.json.simple.parser.ParseException;

  ResponseEntity<Object> getMulticriteriaAnalysis(Long projectId) throws ProjectNotFoundException;

  ResponseEntity<String> updateMultiCriteriaAnalysis(
      Long projectId, FormStep multiCriteriaAnalysis);

  ResponseEntity<String> updateMultiCriteriaQuestion(FormQuestionModel formQuestion)
      throws MulticriteriaNotFoundException;

  ResponseEntity calculateMultiCriteriaAnalysisScore(Long projectId)
      throws ProjectNotFoundException, QuestionsNotFoundException, EscoreException;

  ResponseEntity<String> increaseMulticriteriaIteration(Long analyseId)
      throws MulticriteriaNotFoundException, UserNotAuthorizedException;
}
