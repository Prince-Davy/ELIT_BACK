package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.ResponseNotValidException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.SurveyAnswerRequestModel;
import com.elit.outildedecision.model.SurveyAnswerResponseModel;
import com.elit.outildedecision.service.SurveyAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * class used to root API calls for survey responses management. Contains methods to answer to the
 * survey, to get user answers and to get the complete survey answers.
 */
@RestController
@Validated
@RequestMapping("/api/survey/answer")
public class SurveyAnswerController {
  //    @Autowired
  private final SurveyAnswerService answerService;

  @Autowired
  public SurveyAnswerController(SurveyAnswerService answerService) {
    this.answerService = answerService;
  }

  /**
   * method called to answer to a survey
   *
   * @param userAnswerList represents a list of questions that user will respond on
   * @return a list of questions with their answers.
   */
  @PostMapping
  public ResponseEntity<String> replyToQuestion(
      @RequestBody List<@Valid SurveyAnswerRequestModel> userAnswerList)
      throws UserNotFoundException, QuestionsNotFoundException, ResponseNotFoundException,
          ResponseNotValidException {
    return answerService.replyToQuestion(userAnswerList);
  }

  /**
   * method called to display answers for one user
   *
   * @param userId given in parameter and represents user for whom is asked to display survey
   *     answers
   * @return a list of user answers
   */
  @GetMapping("/{userId}")
  public List<SurveyAnswerResponseModel> userAnswer(@PathVariable Long userId)
      throws UserNotFoundException, ResponseNotFoundException {
    return answerService.getUserAnswer(userId);
  }

  /**
   * method called to display all result of survey
   *
   * @return a list of survey result
   */
  @GetMapping("/page/{pageNumber}")
  public List<SurveyAnswerResponseModel> getSurveyReport(@PathVariable int pageNumber) {
    return answerService.getSurveyReport(pageNumber);
  }
}
