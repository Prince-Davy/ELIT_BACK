package com.elit.outildedecision.collaboration.survey.service;

import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerRequest;
import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerResponse;
import com.elit.outildedecision.collaboration.survey.exception.ResponseNotValidException;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;

import java.util.List;

/**
 * This interface defines the contract for the Survey Answer Service, which provides methods to record and retrieve
 * survey answers and responses.
 */
public interface SurveyAnswerService {

  /**
   * Records the user's responses to the specified survey questions.
   *
   * @param userAnswer a List of SurveyAnswerRequest objects representing the user's responses to the survey
   *                    questions
   * @return a confirmation message indicating that the responses have been saved
   * @throws UserNotFoundException if the specified user does not exist
   * @throws QuestionsNotFoundException if the specified questions do not exist
   * @throws ResponseNotFoundException if the specified response does not exist
   * @throws ResponseNotValidException if the specified response is not valid
   */
  String replyToQuestion(List<SurveyAnswerRequest> userAnswer)
    throws UserNotFoundException, QuestionsNotFoundException, ResponseNotFoundException,
    ResponseNotValidException;

  /**
   * Retrieves the user's responses to survey questions.
   *
   * @param userId the ID of the user whose survey responses to retrieve
   * @return a List of SurveyAnswerResponse objects representing the user's survey responses
   * @throws UserNotFoundException if the specified user does not exist
   * @throws ResponseNotFoundException if the specified response does not exist
   */
  List<SurveyAnswerResponse> getUserAnswer(Long userId)
    throws UserNotFoundException, ResponseNotFoundException;

  /**
   * Retrieves a paginated list of survey responses.
   *
   * @param pageNumber the page number to retrieve
   * @return a List of SurveyAnswerResponse objects representing the survey responses
   */
  List<SurveyAnswerResponse> getSurveyReport(int pageNumber);
}
