package com.elit.outildedecision.controller;

import com.elit.outildedecision.config.View;
import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.service.AnswerIaService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;


import java.util.List;

@RestController
@RequestMapping("/answers")
public class AnswerIaController {

  private final AnswerIaService answerIaService;

  AnswerIaController(final AnswerIaService answerIaService) {
    this.answerIaService = answerIaService;
  }

  /**
   * @param projectId id
   * @return ResponseEntity.ok
   */
  @GetMapping("/{projectId}")
  @JsonView(View.AnswerIa.class)
  @PreAuthorize("hasAnyAuthority('USER')")
  public ResponseEntity<List<AnswerIa>> getAnswers(@PathVariable final long projectId) {
    final List<AnswerIa> answers = answerIaService.getAllAnswers(projectId);
    return ResponseEntity.ok(answers);
  }

  /**
   *
   * @param ignoredProjectId id est utilisé dans l'annotation @PreAuthorize pour vérifier les autorisations
   * @param answers save answers
   * @return ResponseEntity<>(HttpStatus.CREATED)
   */
  @PostMapping("/{ignoredProjectId}")
  @PreAuthorize("isProjectOwner(#ignoredProjectId)")
  public ResponseEntity<?> saveMultipleAnswers(@PathVariable final long ignoredProjectId, @RequestBody final List<AnswerIa> answers) {
    // ignoredProjectId est utilisé dans l'annotation @PreAuthorize pour vérifier les autorisations
    answerIaService.saveAll(answers);
    return new ResponseEntity<>(HttpStatus.CREATED);

  }

  /**
   * @param projectId id
   * @return ResponseEntityNoContent
   */
  @DeleteMapping("/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Void> deleteAnswersByProject(@PathVariable final long projectId) {
    answerIaService.deleteAnswersByProjectId(projectId);
    return ResponseEntity.noContent().build();
  }
}
