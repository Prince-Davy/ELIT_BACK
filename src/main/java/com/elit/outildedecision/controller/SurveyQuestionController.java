package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.exception.QuestionFormatNotValidException;
import com.elit.outildedecision.service.SurveyQuestionService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * class used to root API calls for survey questions management. Contains methods to call to
 * display, create, delete or modify survey questions.
 */
@RestController
@RequestMapping("/api/survey/question")
public class SurveyQuestionController {
  /**
   * SurveyQuestionServiceImpl.
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
   * method called to add a new question to the survey.
   *
   * @param question represents the new question to add on survey
   * @return added question. return needed for API testing
   */
  @PostMapping("")
  public SurveyQuestion createQuestion(@RequestBody(required = false) final SurveyQuestion question)
      throws QuestionFormatNotValidException {
    return questionService.createQuestion(question);
  }
  /**
   * method called to modify an existing question.
   *
   * @param question given in parameter and contains modification to apply.
   * @param questionId given in parameter and represents the id of the existing question to modify
   * @return updated question. return needed for API testing
   */
  @PutMapping("/{questionId}")
  public SurveyQuestion modifyQuestion(
      @PathVariable final Long questionId, @RequestBody(required = false) final SurveyQuestion question)
      throws QuestionFormatNotValidException {
    return questionService.modifyQuestion(questionId, question);
  }
  /**
   * method called to remove a question on survey.
   *
   * @param questionId given in parameter and represents the id of the existing question to delete
   * @return deleted question. return needed for API testing
   */
  @DeleteMapping("/{questionId}")
  public Optional<SurveyQuestion> deleteQuestion(@PathVariable final Long questionId) {
    return questionService.deleteQuestion(questionId);
  }
  /**
   * method called to select and display an existing question.
   *
   * @param questionId given in parameter and represents the desired question to display
   * @return selected question.
   */
  @GetMapping("/{questionId}")
  public Optional<SurveyQuestion> selectQuestion(@PathVariable final Long questionId) {
    return questionService.selectQuestion(questionId);
  }
  /**
   * method called to display all questions that belong to the survey.
   *
   * @return a list of all existing questions on survey
   */
  @GetMapping("")
  public List<SurveyQuestion> getQuestionsList() {
    return questionService.getQuestionsList();
  }
}
