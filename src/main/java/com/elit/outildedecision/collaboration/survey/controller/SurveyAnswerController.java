package com.elit.outildedecision.collaboration.survey.controller;

import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerRequest;
import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerResponse;
import com.elit.outildedecision.collaboration.survey.exception.ResponseNotValidException;
import com.elit.outildedecision.collaboration.survey.service.SurveyAnswerService;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * class used to root API calls for survey responses management. Contains methods to answer to the
 * survey, to get user answers and to get the complete survey answers.
 */
@RestController
@Validated
@RequestMapping("/survey/answer")
@Tag(name = "Collaboration - Survey Answers", description = "Gestion des réponses au sondage")
public class SurveyAnswerController {
  /**
   * SurveyAnswerService to manage survey answers.
   */
  private final SurveyAnswerService answerService;

  /**
   * Constructor of SurveyAnswerController.
   * dependency injection of SurveyAnswerService to manage survey answers.
   * @param answerService SurveyAnswerService
   */
  public SurveyAnswerController(final SurveyAnswerService answerService) {
    this.answerService = answerService;
  }

  /**
   * method called to answer to a survey.
   *
   * @param userAnswerList represents a list of questions that user will respond on
   * @throws UserNotFoundException if user is not found in database
   * @throws QuestionsNotFoundException if questions are not found in database
   * @throws ResponseNotFoundException if response is not found in database
   * @throws ResponseNotValidException if the response is not valid
   * @return a confirmation message.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthority('USER')")
  public String replyToQuestion(@RequestBody final List<@Valid SurveyAnswerRequest> userAnswerList)
    throws UserNotFoundException, QuestionsNotFoundException, ResponseNotFoundException,
    ResponseNotValidException {
    return answerService.replyToQuestion(userAnswerList);
  }

  /**
   * method called to display answers for one user.
   *
   * @param userId given in parameter and represents user for whom is asked to display survey
   *     answers
   * @throws UserNotFoundException if user is not found in database
   * @throws ResponseNotFoundException if response is not found in database
   * @return a list of user answers
   */
  @GetMapping("/{userId}")
  @PreAuthorize("hasAnyAuthority('USER')")
  public List<SurveyAnswerResponse> userAnswer(@PathVariable final Long userId)
      throws UserNotFoundException, ResponseNotFoundException {
    return answerService.getUserAnswer(userId);
  }
}
