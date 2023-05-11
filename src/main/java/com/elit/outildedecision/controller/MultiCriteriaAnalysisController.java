
package com.elit.outildedecision.controller;


import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.EscoreException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.model.MultiCriteriaAnalysisScoreModel;
import com.elit.outildedecision.model.MultiCriteriaStepModel;
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
   * @throws IOException IOException
   * @throws ParseException ParseException
   * @throws org.json.simple.parser.ParseException org.json.simple.parser.ParseException
   */
  @PostMapping("/multi-criteria-analysis/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> createMultiCriteriaAnalysis(
      @PathVariable("projectId") final Long projectId)
      throws IOException, ParseException,
          org.json.simple.parser.ParseException {
    return multiCriteriaAnalysisService.createMulticriteriaAnalysisForm(projectId);
  }
  /**
   * get the multi-criteria analysis components.
   *
   * @param projectId the project id
   * @throws ProjectNotFoundException when the project is not found in the database
   * @return ResponseEntity<Object>
   */
  @GetMapping("/multi-criteria-analysis/{projectId}")
  public ResponseEntity<Object> getMultiCriteriaAnalysis(@PathVariable final Long projectId) throws ProjectNotFoundException {
    return multiCriteriaAnalysisService.getMulticriteriaAnalysis(projectId);
  }
  /**
   * increase the iteration of the multi-criteria analysis.
   *
   * @param projectId the project id
   * @param analyseId the multi-criteria analysis id
   * @throws MulticriteriaNotFoundException when the multi-criteria analysis is not found in the database
   * @throws UserNotAuthorizedException when the user is not authorized to perform this action
   * @throws ProjectNotFoundException if the specified project does not exist
   * @return ResponseEntity<Object>
   */
  @PutMapping("/{projectId}/multi-criteria-analysis/iteration/{analyseId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> increaseMulticriteriaIteration(@PathVariable final Long projectId, @PathVariable final Long analyseId)
      throws MulticriteriaNotFoundException, UserNotAuthorizedException, ProjectNotFoundException {
    return multiCriteriaAnalysisService.increaseMulticriteriaIteration(projectId, analyseId);
  }

  /**
   * update the multi-criteria analysis.
   * @param projectId the project id
   * @param multiCriteriaAnalysisStep the new multi-criteria analysis step
   * @return ResponseEntity<Object>
   */
  @PutMapping("/multi-criteria-analysis/{projectId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> updateMultiCriteriaAnalysis(
      @PathVariable final Long projectId, @RequestBody final MultiCriteriaStepModel multiCriteriaAnalysisStep) throws StepFormNotFoundException {
    return multiCriteriaAnalysisService.updateMultiCriteriaAnalysis(
        projectId, multiCriteriaAnalysisStep);
  }
  /**
   * update the multi-criteria analysis question.
   *
   * @param projectId the id of the project
   * @param formQuestion the new multi-criteria analysis question
   * @throws MulticriteriaNotFoundException when the multi-criteria analysis is not found in the database
   * @return ResponseEntity<Object>
   */
  @PutMapping("/{projectId}/multi-criteria-analysis/question")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> updateQuestion(@PathVariable final Long projectId, @RequestBody final FormQuestionModel formQuestion)
    throws MulticriteriaNotFoundException, StepFormNotFoundException {
    return multiCriteriaAnalysisService.updateMultiCriteriaQuestion(projectId, formQuestion);
  }
  /**
   * get the multi-criteria analysis score.
   *
   * @param projectId the project id
   * @return ResponseEntity<Object>
   * @throws ProjectNotFoundException   when the project is not found in the database
   * @throws QuestionsNotFoundException when the questions are not found in the database
   * @throws EscoreException            when there is an error in the escore calculation process
   */
  @GetMapping("/multi-criteria-analysis/{projectId}/score")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<MultiCriteriaAnalysisScoreModel> getMulticriteriaAnalysisScore(@PathVariable final Long projectId)
      throws ProjectNotFoundException, QuestionsNotFoundException, EscoreException {
    return multiCriteriaAnalysisService.calculateMultiCriteriaAnalysisScore(projectId);
  }

  /**
   * update the multi-criteria analysis question.
   * ToDo Delete this EndPoint and tell the FrontEnd
   * @param formQuestion the new multi-criteria analysis question
   * @throws MulticriteriaNotFoundException when the multi-criteria analysis is not found in the database
   * @return ResponseEntity<Object>
   */
  @PutMapping("/multi-criteria-analysis/question")
  public ResponseEntity<String> updateQuestion(@RequestBody final FormQuestionModel formQuestion)
    throws MulticriteriaNotFoundException {
    return multiCriteriaAnalysisService.updateMultiCriteriaQuestion(formQuestion);
  }

  /**
   * increase the iteration of the multi-criteria analysis.
   * ToDo delite this API ; it was replaced by increaseMulticriteriaIteration
   * @param analyseId the multi-criteria analysis id
   * @throws MulticriteriaNotFoundException when the multi-criteria analysis is not found in the database
   * @throws UserNotAuthorizedException when the user is not authorized to perform this action
   * @return ResponseEntity<Object>
   */
  @PutMapping("/multi-criteria-analysis/iteration/{analyseId}")
  public ResponseEntity<String> increaseMulticriteriaIterationV1(@PathVariable final Long analyseId)
    throws MulticriteriaNotFoundException, UserNotAuthorizedException {
    return multiCriteriaAnalysisService.increaseMulticriteriaIteration(analyseId);
  }
}
