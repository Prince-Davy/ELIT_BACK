package com.elit.outildedecision.ai.result.controller;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.result.dto.RecommendationItemResponse;
import com.elit.outildedecision.ai.result.dto.ResultResponse;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.ai.result.service.ResultIaService;
import com.elit.outildedecision.shared.common.dto.SimpleMessageDto;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IaResultController.
 */
@RestController
@RequestMapping("/result/ai")
@Slf4j
@Tag(name = "AI - Results", description = "Gestion des résultats et recommandations IA")
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
   * @param projectId   project id
   * @param answerRequest list of answers IA
   * @return ResponseEntity<SimpleMessageDto>
   */
  @PostMapping("/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<SimpleMessageDto> saveAiResponse(@PathVariable final long projectId,
                                                         @RequestBody final AnswerRequest answerRequest) throws ResultAiNotFoundException, ProjectNotFoundException, ResultAlreadyExistsException {

    String iaResponse = resultIaService.generateAndSaveResult(projectId, answerRequest);
    return ResponseEntity.ok(new SimpleMessageDto("AI prediction" + "\n" + iaResponse, HttpStatus.OK.value(), LocalDateTime.now()));
  }


  /**
   * Get result of analyse AI for project.
   *
   * @param projectId ptoject id
   * @return ResultResponse
   * @throws ProjectNotFoundException  if project not found
   * @throws ResponseNotFoundException if exception
   */
  @GetMapping("/{projectId}")
  @PreAuthorize("isContributer(#projectId) or isObserver(#projectId)")
  public ResultResponse getAnalyseIAResult(@PathVariable final long projectId) throws ProjectNotFoundException, ResponseNotFoundException {
    return resultIaService.getResultIa(projectId);
  }

  /**
   * Gets the mapping associated to project id, given the ai has already been called.
   *
   * @param projectId the project id
   * @return the mapping given the responses
   */
  @GetMapping("/mapping/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<List<RecommendationItemResponse>> getAnalysisIaResultMapping(@PathVariable final String projectId)
    throws AiAnswerNotFoundException, ResultAiNotFoundException {
    return ResponseEntity.ok(resultIaService.getMapping(projectId));
  }

  /**
   * Acknowledges the front-end "method recommendation" placeholder call (plan anomaly §4.4).
   * The front issues this POST after the AI questionnaire; there is no business payload yet, so the
   * endpoint is officialised as a safe no-op returning 204 No Content to avoid a failed async call.
   *
   * @param projectId the project id
   */
  @PostMapping("/method-recommendation/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void acknowledgeMethodRecommendation(@PathVariable final long projectId) {
    log.info("Method recommendation acknowledgement received for project ID: {}", projectId);
  }
}
