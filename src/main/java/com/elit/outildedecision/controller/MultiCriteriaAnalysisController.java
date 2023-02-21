
package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.EscoreException;
import com.elit.outildedecision.exception.FormAlreadyExistsException;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.service.MultiCriteriaAnalysisService;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Multi-criteria analysis controller.
 */
@RestController
@RequestMapping("/api")
public class MultiCriteriaAnalysisController {
  /**
   * MultiCriteriaAnalysis Service.
   */
  private final MultiCriteriaAnalysisService multiCriteriaAnalysisService;

  /**
   * Constructor for MultiCriteriaAnalysisController.
   * dependency injection for MultiCriteriaAnalysisService
   * @param multiCriteriaAnalysisService MultiCriteriaAnalysisService
   */
  public MultiCriteriaAnalysisController(
      final MultiCriteriaAnalysisService multiCriteriaAnalysisService) {
    this.multiCriteriaAnalysisService = multiCriteriaAnalysisService;
  }

  /**
   * @param projectId the project id
   * @return ResponseEntity<String>
   * @throws FormAlreadyExistsException FormAlreadyExistsException
   * @throws IOException IOException
   * @throws ParseException ParseException
   * @throws org.json.simple.parser.ParseException org.json.simple.parser.ParseException
   */
  @PostMapping("/multi-criteria-analysis/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> createMultiCriteriaAnalysis(
      @PathVariable("projectId") final Long projectId)
      throws FormAlreadyExistsException, IOException, ParseException,
          org.json.simple.parser.ParseException {
    return multiCriteriaAnalysisService.createMulticriteriaAnalysisForm(projectId);
  }
  /**
   * get the multi-criteria analysis components.
   * @param projectId the project id
   * @return ResponseEntity<Object>
   */
  @GetMapping("/multi-criteria-analysis/{projectId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<Object> getMultiCriteriaAnalysis(@PathVariable final Long projectId) {
    return multiCriteriaAnalysisService.getMulticriteriaAnalysis(projectId);
  }
  /**
   * increase the iteration of the multi-criteria analysis.
   * @param analyseId the multi-criteria analysis id
   * @return ResponseEntity<Object>
   */
  @PutMapping("/multi-criteria-analysis/iteration/{analyseId}")
  public ResponseEntity<String> increaseMulticriteriaIteration(@PathVariable final Long analyseId)
      throws MulticriteriaNotFoundException, UserNotAuthorizedException {
    return multiCriteriaAnalysisService.increaseMulticriteriaIteration(analyseId);
  }
  /**
   * update the multi-criteria analysis.
   * @param projectId the project id
   * @param multiCriteriaAnalysisStep the new multi-criteria analysis step
   * @return ResponseEntity<Object>
   */
  @PutMapping("/multi-criteria-analysis/{projectId}")
  public ResponseEntity<String> updateMultiCriteriaAnalysis(
      @PathVariable final Long projectId, @RequestBody final FormStep multiCriteriaAnalysisStep) {
    return multiCriteriaAnalysisService.updateMultiCriteriaAnalysis(
        projectId, multiCriteriaAnalysisStep);
  }
  /**
   * update the multi-criteria analysis question.
   * @param formQuestion the new multi-criteria analysis question
   * @return ResponseEntity<Object>
   */
  @PutMapping("/multi-criteria-analysis/question")
  public ResponseEntity<String> updateQuestion(@RequestBody final FormQuestionModel formQuestion)
      throws MulticriteriaNotFoundException {
    return multiCriteriaAnalysisService.updateMultiCriteriaQuestion(formQuestion);
  }
  /**
   * get the multi-criteria analysis score.
   * @param projectId the project id
   * @return ResponseEntity<Object>
   */
  @GetMapping("/multi-criteria-analysis/{projectId}/score")
  public ResponseEntity getMulticriteriaAnalysisScore(@PathVariable final Long projectId)
      throws ProjectNotFoundException, QuestionsNotFoundException, IOException, EscoreException {
    return multiCriteriaAnalysisService.calculateMultiCriteriaAnalysisScore(projectId);
  }
}
