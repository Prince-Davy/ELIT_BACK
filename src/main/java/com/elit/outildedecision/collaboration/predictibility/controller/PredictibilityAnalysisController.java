package com.elit.outildedecision.collaboration.predictibility.controller;

import com.elit.outildedecision.collaboration.predictibility.dto.ChosenMethodologyRequest;
import com.elit.outildedecision.collaboration.predictibility.dto.PredictibilityScoreResponse;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.predictibility.service.PredictibilityAnalysisService;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the predictibility analysis feature.
 */
@RestController
@RequestMapping("/")
@Tag(name = "Collaboration - Predictability", description = "Analyse de prédictibilité d'un projet")
public class PredictibilityAnalysisController {

  /**
   * The predictibility analysis service.
   */
  private final PredictibilityAnalysisService predictibilityAnalysisService;

  /**
   * Constructor injecting the predictibility analysis service.
   *
   * @param predictibilityAnalysisService the predictibility analysis service
   */
  public PredictibilityAnalysisController(
      final PredictibilityAnalysisService predictibilityAnalysisService) {
    this.predictibilityAnalysisService = predictibilityAnalysisService;
  }

  /**
   * Creates a new predictibility analysis for a project, or returns the existing one.
   *
   * <p>Open to any contributor (owner included via {@code isContributer}): the methodology vote is
   * parallel, so a contributor must be able to initialise the analysis without waiting for the
   * owner. The operation is idempotent (returns the existing analysis when already created).
   *
   * @param projectId the project id
   * @return the created (or existing) analysis id
   * @throws ProjectNotFoundException if the project does not exist
   */
  @PostMapping("/predictibility-analysis/{projectId}")
  @PreAuthorize("isContributer(#projectId)")
  ResponseEntity<Long> createPredictibilityAnalysis(@PathVariable final Long projectId)
      throws ProjectNotFoundException {
    return ResponseEntity.ok(predictibilityAnalysisService.createPredictibilityForm(projectId));
  }

  /**
   * Stores the final chosen methodology.
   *
   * <p>The path follows the front contract: {@code projectId} first, then {@code analyseId}
   * (the legacy back-end inverted both ids, see plan anomaly §4.1).
   *
   * @param request the body carrying the chosen methodology
   * @param projectId the project id
   * @param analyseId the predictibility analysis id
   * @return a confirmation message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   */
  @PostMapping("/{projectId}/predictibility/{analyseId}/chosenMethodology")
  @PreAuthorize("isProjectOwner(#projectId)")
  ResponseEntity<String> addFinalChosenMethodology(@Valid @RequestBody final ChosenMethodologyRequest request,
      @PathVariable final Long projectId, @PathVariable final Long analyseId)
      throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    return ResponseEntity.ok(predictibilityAnalysisService
        .addFinalChosenMethodology(request, projectId, analyseId));
  }

  /**
   * Returns the predictibility analysis score.
   *
   * @param projectId the project id
   * @param analyseId the predictibility analysis id
   * @return the predictibility score
   * @throws ProjectNotFoundException if the analysis does not belong to the project
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   */
  @GetMapping("/{projectId}/predictibility/{analyseId}/score")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId) or isObserver(#projectId)")
  public ResponseEntity<PredictibilityScoreResponse> getPredictabilityAnalysisScore(
      @PathVariable final Long projectId, @PathVariable final Long analyseId)
      throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    return ResponseEntity.ok(
        predictibilityAnalysisService.calculatePredictibilityAnalysisScore(projectId, analyseId));
  }

  /**
   * Marks the predictibility analysis as finished.
   *
   * @param projectId the project id
   * @param analyseId the predictibility analysis id
   * @return a confirmation message
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   * @throws PredictibilityAnalysisAlreadyUpdated if the analysis is already finished
   * @throws ProjectNotFoundException if the analysis does not belong to the project
   */
  @PutMapping("/{projectId}/predictibility/isFinished/{analyseId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> putIsFinishedPredictibilityAnalysis(
      @PathVariable final Long projectId, @PathVariable final Long analyseId)
      throws PredictibilityAnalysisNotFoundException, PredictibilityAnalysisAlreadyUpdated,
      ProjectNotFoundException {
    return ResponseEntity.ok(
        predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(projectId, analyseId));
  }

  /**
   * Increases the iteration number of the predictibility analysis.
   *
   * @param projectId the project id
   * @param analyseId the predictibility analysis id
   * @return a confirmation message
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   * @throws ProjectNotFoundException if the analysis does not belong to the project
   */
  @PutMapping("/{projectId}/predictibility/iteration/{analyseId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> increasePredictibilityAnalyseIterationNumber(
      @PathVariable final Long projectId, @PathVariable final Long analyseId)
      throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    return ResponseEntity.ok(
        predictibilityAnalysisService.increasePredictAnalysisIterationNum(projectId, analyseId));
  }
}
