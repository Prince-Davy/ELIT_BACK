/**
 * This package contains the REST controllers for managing AI-answers.
 */
package com.elit.outildedecision.ai.answer.controller;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.common.dto.SimpleMessageDto;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.service.ProjectService;
import com.elit.outildedecision.util.FormProgressStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Slf4j
@RestController
@RequestMapping("/answers")
public class AiAnswerController {

  private final AiAnswerService aiAnswerService;
  private final ProjectService projectService;


  /**
   * Constructs an AiAnswerController with the specified AiAnswerService.
   *
   * @param aiAnswerService the service used to manage AI answers
   * @param projectService the service used to manage a project
   */
  public AiAnswerController(final AiAnswerService aiAnswerService, final ProjectService projectService) {

    this.aiAnswerService = aiAnswerService;
    this.projectService = projectService;
  }

  /**
   * Endpoint to save AI-generated answers for a specific project.
   * Only accessible to the project owner.
   *
   * @param projectId ID of the project
   * @param answerIaDTO DTO containing the AI answers
   * @return ResponseEntity with a success message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws AiAnswerNotFoundException if the AI answer data is invalid or missing
   */
  @PostMapping("/{projectId}")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Answers saved successfully"),
    @ApiResponse(responseCode = "400", description = "Project not found"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<SimpleMessageDto> saveAnswers(@PathVariable final Long projectId, @RequestBody final AiAnswerDTO answerIaDTO) throws ProjectNotFoundException, AiAnswerNotFoundException {
    log.info("Received request to save AI answers for project ID: {}", projectId);

    Map<String, String> data = answerIaDTO.getData();
    log.debug("Data received: {}", data);
    aiAnswerService.saveAnswers(projectId, data);
    log.info("AI answers successfully saved for project ID: {}", projectId);
    projectService.updateFormTypeStatus(projectId, FormProgressStatus.IA);
    log.info("Form progress status updated for project ID: {}", projectId);
    return ResponseEntity.ok(new SimpleMessageDto("AI answers saved successfully", HttpStatus.OK.value(), LocalDateTime.now()));

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
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Answers retrieved successfully"),
    @ApiResponse(responseCode = "404", description = "Answers not found"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PreAuthorize("isProjectOwner(#projectId) or isContributer(#projectId)")
  public ResponseEntity<AiAnswerDTO> getAnswers(@PathVariable final Long projectId) throws AiAnswerNotFoundException {
    log.info("Received request to fetch AI answers for project ID: {}", projectId);

    List<AiAnswer> answers = aiAnswerService.getAnswersByProject(projectId);
    log.debug("Answers retrieved: {}", answers);
    AiAnswerDTO dto = AiAnswerDTO.fromEntityList(answers);
    log.info("AI answers successfully returned for project ID: {}", projectId);
    return ResponseEntity.ok(dto);

  }
}
