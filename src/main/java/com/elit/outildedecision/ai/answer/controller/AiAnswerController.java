/**
 * This package contains the REST controllers for managing AI-answers.
 */
package com.elit.outildedecision.ai.answer.controller;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.answer.dto.AnswerResponse;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.mapper.AnswerMapper;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.shared.common.dto.SimpleMessageDto;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.shared.util.FormProgressStatus;
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
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Ai answer controller class. */
@Slf4j
@RestController
@RequestMapping("/answers")
@Tag(name = "AI - Answers", description = "Gestion des réponses générées par l'IA")
public class AiAnswerController {

  private final AiAnswerService aiAnswerService;
  private final ProjectService projectService;
  private final AnswerMapper answerMapper;


  /**
   * Constructs an AiAnswerController with the specified collaborators.
   *
   * @param aiAnswerService the service used to manage AI answers
   * @param projectService the service used to manage a project
   * @param answerMapper the mapper converting AI answer entities to their DTO
   */
  public AiAnswerController(final AiAnswerService aiAnswerService, final ProjectService projectService,
                           final AnswerMapper answerMapper) {

    this.aiAnswerService = aiAnswerService;
    this.projectService = projectService;
    this.answerMapper = answerMapper;
  }

  /**
   * Endpoint to save AI-generated answers for a specific project.
   * Only accessible to the project owner.
   *
   * @param projectId ID of the project
   * @param answerRequest request payload containing the AI answers
   * @return ResponseEntity with a success message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws AiAnswerNotFoundException if the AI answer data is invalid or missing
   */
  @PostMapping("/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<SimpleMessageDto> saveAnswers(@PathVariable final Long projectId, @RequestBody final AnswerRequest answerRequest) throws ProjectNotFoundException, AiAnswerNotFoundException {
    log.info("Received request to save AI answers for project ID: {}", projectId);

    Map<String, String> data = answerRequest.getData();
    log.debug("Data received: {}", data);
    aiAnswerService.saveAnswers(projectId, data);
    log.info("AI answers successfully saved for project ID: {}", projectId);
    projectService.updateFormTypeStatus(projectId, FormProgressStatus.IA);
    log.info("Form progress status updated for project ID: {}", projectId);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new SimpleMessageDto("AI answers saved successfully", HttpStatus.CREATED.value(), LocalDateTime.now()));

  }

  /**
   * Endpoint to retrieve AI-generated answers for a specific project.
   * Only accessible to the project owner.
   *
   * @param projectId ID of the project
   * @return ResponseEntity containing the AI answers DTO
   * @throws AiAnswerNotFoundException if no answers are found for the project
   */
  @GetMapping("/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId) or isContributer(#projectId)")
  public ResponseEntity<AnswerResponse> getAnswers(@PathVariable final Long projectId) throws AiAnswerNotFoundException {
    log.info("Received request to fetch AI answers for project ID: {}", projectId);

    List<AiAnswer> answers = aiAnswerService.getAnswersByProject(projectId);
    AnswerResponse response = answerMapper.toResponse(answers);
    log.info("AI answers successfully returned for project ID: {}", projectId);
    return ResponseEntity.ok(response);

  }
}
