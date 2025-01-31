package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.exception.AnswerIaNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.model.RecommandationItemModel;
import com.elit.outildedecision.model.RecommandationKiviatModel;
import com.elit.outildedecision.model.ResultAiModel;
import com.elit.outildedecision.service.ResultIaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * IaResultController.
 */
@RestController
@RequestMapping("/result/ai")
public class IaResultController {

  private final ResultIaService resultIaService;

  /**
   * IaResultController.
   *
   * @param resultIaService resultIaService.
   */
  public IaResultController(final ResultIaService resultIaService) {
    this.resultIaService = resultIaService;
  }

  /**
   * Send data to Ai and get result to save it on BDD.
   *
   * @param projectId project id
   * @param answers   list of answers IA
   * @return ResponseEntity<Object>
   */
  @PostMapping("/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Object> saveAiResponse(@PathVariable final long projectId, @RequestBody final List<AnswerIa> answers) {
    return resultIaService.generateAndSaveResult(projectId, answers);
  }

  /**
   * Get result of analyse AI for project.
   *
   * @param projectId ptoject id
   * @return ResultAiModel
   * @throws ProjectNotFoundException  if project not found
   * @throws ResponseNotFoundException if exception
   */
  @GetMapping("/{projectId}")
  @PreAuthorize("hasAnyAuthority('USER')")
  public ResultAiModel getAnalyseIAResult(@PathVariable final long projectId) throws ProjectNotFoundException, ResponseNotFoundException {
    return resultIaService.getResultIa(projectId);
  }

  /**
   * Delete a result analysis AI.
   *
   * @param ignoredProjectId project ID
   * @param resultIaId       result IA ID
   * @return ResponseEntity<String>
   * @throws ResponseNotFoundException if exception
   */
  @DeleteMapping("/{ignoredProjectId}/{resultIaId}")
  @PreAuthorize("isProjectOwner(#ignoredProjectId)")
  public ResponseEntity<String> deleteAnalysisIaResult(@PathVariable final long ignoredProjectId, @PathVariable final Long resultIaId) throws ResponseNotFoundException {
    // ignoredProjectId est utilisé dans l'annotation @PreAuthorize pour verifier les authorizations
    return resultIaService.deleteResultAnalysisAI(resultIaId);
  }

  /**
   * Gets the mapping associated to project id, given the ai has already been called.
   *
   * @param projectId the project id
   * @return the mapping given the responses
   */
  @GetMapping("/mapping/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<List<RecommandationItemModel>> getAnalysisIaResultMapping(@PathVariable final String projectId)
    throws AnswerIaNotFoundException, ResultAiNotFoundException {
    return resultIaService.getMapping(projectId);
  }

  /**
   *
   * @param projectId the project id
   * @return the value for kiviat graph project
   */
  @GetMapping("/kiviatProject/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<RecommandationKiviatModel> getKiviatProjectValues(@PathVariable final String projectId)
    throws ResultAiNotFoundException {
    return resultIaService.getKiviatProjectValues(projectId);
  }
}
