package com.elit.outildedecision.service;


import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.model.ReportResponseModel;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.Set;

public interface MultiCriteriaAnalysisService {
    ResponseEntity<String> createMulticriteriaAnalysisForm(Long projectId) throws FormAlreadyExistsException, IOException, ParseException, org.json.simple.parser.ParseException;

    ResponseEntity<Object> getMulticriteriaAnalysis(Long projectId);

    ResponseEntity<String> updateMultiCriteriaAnalysis(Long projectId, FormStep multiCriteriaAnalysis);

    ResponseEntity<String> updateMultiCriteriaQuestion(FormQuestionModel formQuestion) throws MulticriteriaNotFoundException;

    ResponseEntity calculateMultiCriteriaAnalysisScore(Long projectId) throws ProjectNotFoundException, QuestionsNotFoundException, IOException, EscoreException;


    ResponseEntity<String> increaseMulticriteriaIteration(Long analyseId) throws MulticriteriaNotFoundException, UserNotAuthorizedException;
}
