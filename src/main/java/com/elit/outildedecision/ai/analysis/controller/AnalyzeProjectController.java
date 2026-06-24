package com.elit.outildedecision.ai.analysis.controller;

import com.elit.outildedecision.ai.analysis.service.AnalyzeProjectService;
import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.shared.common.dto.SimpleMessageDto;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAlreadyExistsException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/** Analyze project controller class. */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/projects/{projectId}/ai-analysis")
@Tag(name = "AI - Analysis", description = "Analyse IA d'un projet")
public class AnalyzeProjectController {
  private final AnalyzeProjectService analyzeProjectService;

  /**
   * Launch AI project analysis.
   *
   * @param projectId     project id.
   * @param answerRequest AI answers.
   * @return status
   */
  @PostMapping
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<SimpleMessageDto> analyse(@PathVariable final long projectId, @RequestBody final AnswerRequest answerRequest)
    throws ResultAiNotFoundException, ProjectNotFoundException, ResultAlreadyExistsException {
    log.info("Received request to analyse project ID {} with AI.", projectId);

    String analysisState = analyzeProjectService.analyzeProject(answerRequest, projectId);
    return ResponseEntity.ok(new SimpleMessageDto(analysisState, HttpStatus.OK.value(), LocalDateTime.now()));
  }
}
