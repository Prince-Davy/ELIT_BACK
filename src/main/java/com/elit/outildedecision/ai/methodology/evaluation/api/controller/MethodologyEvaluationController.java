package com.elit.outildedecision.ai.methodology.evaluation.api.controller;

import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/projects/{projectId}/methodology-evaluations")
public class MethodologyEvaluationController {

  private final MethodologyEvaluationService methodologyEvaluationService;

  /**
   * Get methodologies evaluatued by AI.
   *
   * @param projectId project ID
   * @return methodology evaluations.
   */
  @GetMapping
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<List<MethodologyEvaluationDto>> getMethodologyEvaluations(@PathVariable final Long projectId) {
    log.info("Received request to fetch Methodology evalutations for project ID: {}", projectId);

    try {
      List<MethodologyEvaluationDto> evaluations = methodologyEvaluationService.getMethodologyEvaluations(projectId, 5);
      return ResponseEntity.ok(evaluations);
    } catch (UnsupportedModelTypeException e) {
      log.info("Model type not supported for project {}, returning empty list. {}", projectId, e.getMessage());
      return ResponseEntity.ok(Collections.emptyList());
    }

  }

}
