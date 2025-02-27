package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.AnswerIaNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.model.RecommandationKiviatModel;
import com.elit.outildedecision.service.KiviatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * KiviatController.
 */
@RestController
@RequestMapping("/kiviat")
public class KiviatController {

  private final KiviatService kiviatService;

  /**
   * KiviatController.
   *
   * @param kiviatService kiviatService.
   */
  public KiviatController(final KiviatService kiviatService) {
    this.kiviatService = kiviatService;
  }

  /**
   *
   * @param projectId the project id
   * @return the value for kiviat graph project
   */
  @GetMapping("/methodology/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<RecommandationKiviatModel> getKiviatMethodologyValues(@PathVariable final Long projectId)
    throws ResultAiNotFoundException {
    return kiviatService.getKiviatMethodologyValues(projectId);
  }

  /**
   *
   * @param projectId the project id
   * @return the value for kiviat graph project
   */
  @GetMapping("/project/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<RecommandationKiviatModel> getKiviatProjectValues(@PathVariable final Long projectId)
    throws AnswerIaNotFoundException {
    return kiviatService.getKiviatProjetValues(projectId);
  }
}
