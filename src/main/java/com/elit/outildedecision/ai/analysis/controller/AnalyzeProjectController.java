package com.elit.outildedecision.ai.analysis.controller;

import com.elit.outildedecision.ai.analysis.service.AnalyzeProjectService;
import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.common.dto.SimpleMessageDto;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.exception.ResultAlreadyExistsException;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/projects/{projectId}/ai-analysis")
public class AnalyzeProjectController {
  private final AnalyzeProjectService analyzeProjectService;

  /**
   * Launch AI project analysis.
   *
   * @param projectId   project id.
   * @param aiAnswerDTO AI answer.
   * @return status
   */
  @PostMapping
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<SimpleMessageDto> analyse(@PathVariable final long projectId, @RequestBody final AiAnswerDTO aiAnswerDTO) {
    log.info("Received request to analyse project ID {} with AI.", projectId);

    String analysisState;
    try {
      analysisState = analyzeProjectService.analyzeProject(aiAnswerDTO, projectId);
    } catch (UnsupportedModelTypeException e) {
      return ResponseEntity.ok(new SimpleMessageDto("Project analyzed with AI. No methodology evaluations were completed due to the nature of the project : " + e.getMessage(), HttpStatus.OK.value(), LocalDateTime.now()));
    } catch (ResultAlreadyExistsException | ProjectNotFoundException | ResultAiNotFoundException e) {
      return ResponseEntity.badRequest().body(new SimpleMessageDto(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()));
    }
    return ResponseEntity.ok(new SimpleMessageDto(analysisState, HttpStatus.OK.value(), LocalDateTime.now()));
  }
}
