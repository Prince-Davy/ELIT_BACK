package com.elit.outildedecision.collaboration.survey.controller;

import com.elit.outildedecision.collaboration.survey.dto.SurveyQuestionResponse;
import com.elit.outildedecision.collaboration.survey.service.SurveyQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * class used to root API calls for survey questions management. Contains methods to call to
 * display, create, delete or modify survey questions.
 */
@RestController
@RequestMapping("/survey/question")
@Tag(name = "Collaboration - Survey Questions", description = "Gestion des questions du sondage")
public class SurveyQuestionController {
  /**
   * SurveyQuestionService to manage survey questions.
   */
  private final SurveyQuestionService questionService;

  /**
   * Constructor of SurveyQuestionController.
   *
   * dependency injection of SurveyQuestionService to manage survey questions.
   * @param questionService SurveyQuestionService
   */
  public SurveyQuestionController(final SurveyQuestionService questionService) {
    this.questionService = questionService;
  }

  /**
   * method called to display all questions that belong to the survey.
   *
   * @return a list of all existing questions on survey
   */
  @GetMapping("")
  @PreAuthorize("hasAnyAuthority('USER')")
  public List<SurveyQuestionResponse> getQuestionsList() {
    return questionService.getQuestionsList();
  }
}
