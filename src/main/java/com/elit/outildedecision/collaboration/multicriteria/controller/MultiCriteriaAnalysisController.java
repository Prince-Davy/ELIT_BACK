package com.elit.outildedecision.collaboration.multicriteria.controller;

import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaAnalysisScoreResponse;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepRequest;
import com.elit.outildedecision.collaboration.multicriteria.dto.MulticriteriaAnalysisResponse;
import com.elit.outildedecision.collaboration.multicriteria.exception.EscoreException;
import com.elit.outildedecision.collaboration.multicriteria.exception.FormValidationException;
import com.elit.outildedecision.collaboration.multicriteria.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.exception.StepFormNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.collaboration.multicriteria.dto.FormQuestionModel;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Multi-criteria analysis controller.
 */
@RestController
@RequestMapping("/")
@Tag(name = "Collaboration - Multi-criteria Analysis", description = "Analyse multicritère d'un projet")
public class MultiCriteriaAnalysisController {
  /**
   * MultiCriteriaAnalysis Service.
   */
  private final MultiCriteriaAnalysisService multiCriteriaAnalysisService;
  /**
   * Project Service.
   */
  private final ProjectService projectService;

  /**
   *
   * Constructor for MultiCriteriaAnalysisController.
   * dependency injection for MultiCriteriaAnalysisService
   * @param multiCriteriaAnalysisService MultiCriteriaAnalysisService
   * @param projectService projectService
   */
  public MultiCriteriaAnalysisController(final MultiCriteriaAnalysisService multiCriteriaAnalysisService, final ProjectService projectService) {
    this.multiCriteriaAnalysisService = multiCriteriaAnalysisService;
    this.projectService = projectService;
  }

  /**
   * get the multi-criteria analysis components.
   *
   * @param projectId the project id
   * @return ResponseEntity<Object>
   * @throws ProjectNotFoundException when the project is not found in the database
   */
  @GetMapping("/multi-criteria-analysis/{projectId}")
  public ResponseEntity<MulticriteriaAnalysisResponse> getMultiCriteriaAnalysis(@PathVariable final Long projectId)
    throws ProjectNotFoundException, MulticriteriaNotFoundException {
    MulticriteriaAnalysisResponse analysis = multiCriteriaAnalysisService.getMulticriteriaAnalysis(projectId)
      .orElseThrow(() -> new MulticriteriaNotFoundException("Aucune analyse multicritère pour ce projet."));
    return ResponseEntity.ok(analysis);
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
    multiCriteriaAnalysisService.increaseMulticriteriaIteration(projectId, analyseId);
    return ResponseEntity.ok().body("L'analyse multicritères a bien changé d'itération.");
  }

  /**
   * update the multi-criteria analysis.
   *
   * @param projectId the project id
   * @param multiCriteriaAnalysisStep the new multi-criteria analysis step
   * @return ResponseEntity<Object>
   */
  @PutMapping("/multi-criteria-analysis/{projectId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> updateMultiCriteriaAnalysis(@PathVariable final Long projectId,
    @RequestBody final MultiCriteriaStepRequest multiCriteriaAnalysisStep) throws StepFormNotFoundException, ProjectNotFoundException, FormValidationException {
    multiCriteriaAnalysisService.updateMultiCriteriaAnalysis(projectId, multiCriteriaAnalysisStep);
    projectService.updateFormTypeStatus(projectId, FormProgressStatus.COLLABORATIVE);
    return ResponseEntity.ok().body("Votre Modification sont enregistrées avec succés");
  }

  /**
   * update the multi-criteria analysis question.
   *
   * @param projectId the id of the project
   * @param formQuestion the new multi-criteria analysis question
   * @return ResponseEntity<Object>
   * @throws MulticriteriaNotFoundException when the multi-criteria analysis is not found in the database
   */
  @PutMapping("/{projectId}/multi-criteria-analysis/question")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> updateQuestion(@PathVariable final Long projectId, @RequestBody final FormQuestionModel formQuestion)
    throws MulticriteriaNotFoundException, StepFormNotFoundException {
    multiCriteriaAnalysisService.updateMultiCriteriaQuestion(projectId, formQuestion);
    return ResponseEntity.ok().body("La questions a été bien modifié.");
  }

  /**
   * mark the multi-criteria analysis as finished.
   *
   * @param projectId the project id
   * @param analyseId the multi-criteria analysis id
   * @return ResponseEntity<String>
   * @throws MulticriteriaNotFoundException when the multi-criteria analysis is not found in the database
   */
  @PutMapping("/{projectId}/multi-criteria-analysis/isFinished/{analyseId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> markMultiCriteriaAnalysisAsFinished(@PathVariable final Long projectId, @PathVariable final Long analyseId)
    throws MulticriteriaNotFoundException {
    multiCriteriaAnalysisService.markMultiCriteriaAnalysisAsFinished(projectId, analyseId);
    return ResponseEntity.ok().body("L'analyse multicritères a bien été finalisée.");
  }

  /**
   * get the multi-criteria analysis score.
   *
   * @param projectId the project id
   * @return ResponseEntity<Object>
   * @throws ProjectNotFoundException when the project is not found in the database
   * @throws QuestionsNotFoundException when the questions are not found in the database
   * @throws EscoreException when there is an error in the escore calculation process
   */
  @GetMapping("/multi-criteria-analysis/{projectId}/score")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<MultiCriteriaAnalysisScoreResponse> getMulticriteriaAnalysisScore(@PathVariable final Long projectId)
    throws ProjectNotFoundException, QuestionsNotFoundException, EscoreException {
    return ResponseEntity.ok().body(multiCriteriaAnalysisService.calculateMultiCriteriaAnalysisScore(projectId));
  }

}
