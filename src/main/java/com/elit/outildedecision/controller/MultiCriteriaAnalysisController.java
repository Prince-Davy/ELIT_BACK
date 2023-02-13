package com.elit.outildedecision.controller;


import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.FormAlreadyExistsException;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.exception.*;

import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.service.MultiCriteriaAnalysisService;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class MultiCriteriaAnalysisController {
    private final MultiCriteriaAnalysisService multiCriteriaAnalysisService;

    public MultiCriteriaAnalysisController( MultiCriteriaAnalysisService multiCriteriaAnalysisService) {
        this.multiCriteriaAnalysisService = multiCriteriaAnalysisService;
    }

    @PostMapping("/multi-criteria-analysis/{projectId}")
    @PreAuthorize("isProjectOwner(#projectId)")
    public ResponseEntity<String> createMultiCriteriaAnalysis(@PathVariable("projectId") Long projectId)
            throws FormAlreadyExistsException, IOException, ParseException, org.json.simple.parser.ParseException {
        return multiCriteriaAnalysisService.createMulticriteriaAnalysisForm(projectId);
    }

    @GetMapping("/multi-criteria-analysis/{projectId}")
    @PreAuthorize("isContributer(#projectId)")
    public ResponseEntity<Object> getMultiCriteriaAnalysis(@PathVariable Long projectId) {
        return multiCriteriaAnalysisService.getMulticriteriaAnalysis(projectId);
    }

    @PutMapping("/multi-criteria-analysis/iteration/{analyseId}")
    public ResponseEntity<String> increaseMulticriteriaIteration(@PathVariable Long analyseId) throws MulticriteriaNotFoundException, UserNotAuthorizedException {
        return multiCriteriaAnalysisService.increaseMulticriteriaIteration(analyseId);
    }

    @PutMapping("/multi-criteria-analysis/{projectId}")
    public ResponseEntity<String> deleteById(@PathVariable Long projectId, @RequestBody FormStep multiCriteriaAnalysisStep) {
        return multiCriteriaAnalysisService.updateMultiCriteriaAnalysis(projectId, multiCriteriaAnalysisStep);
    }

    @PutMapping("/multi-criteria-analysis/question")
    public ResponseEntity<String> updateQuestion(@RequestBody FormQuestionModel formQuestion) throws MulticriteriaNotFoundException {
        return multiCriteriaAnalysisService.updateMultiCriteriaQuestion(formQuestion);
    }


    @GetMapping("/multi-criteria-analysis/{projectId}/score")
    public ResponseEntity getMulticriteriaAnalysisScore(@PathVariable Long projectId) throws ProjectNotFoundException, QuestionsNotFoundException, IOException, EscoreException {
        return multiCriteriaAnalysisService.calculateMultiCriteriaAnalysisScore(projectId);
    }



}
