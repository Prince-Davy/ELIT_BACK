
package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.ResponseNotValidException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.SurveyAnswerRequestModel;
import com.elit.outildedecision.model.SurveyAnswerResponseModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * This interface defines the contract for the Survey Answer Service, which provides methods to record and retrieve
 * survey answers and responses.
 */
public interface SurveyAnswerService {

  /**
   * Records the user's responses to the specified survey questions.
   *
   * @param userAnswer a List of SurveyAnswerRequestModel objects representing the user's responses to the survey
   *                    questions
   * @return a ResponseEntity indicating the success or failure of the response recording operation
   * @throws UserNotFoundException if the specified user does not exist
   * @throws QuestionsNotFoundException if the specified questions do not exist
   * @throws ResponseNotFoundException if the specified response does not exist
   * @throws ResponseNotValidException if the specified response is not valid
   */
  ResponseEntity<String> replyToQuestion(List<SurveyAnswerRequestModel> userAnswer)
    throws UserNotFoundException, QuestionsNotFoundException, ResponseNotFoundException,
    ResponseNotValidException;

  /**
   * Retrieves the user's responses to survey questions.
   *
   * @param userId the ID of the user whose survey responses to retrieve
   * @return a List of SurveyAnswerResponseModel objects representing the user's survey responses
   * @throws UserNotFoundException if the specified user does not exist
   * @throws ResponseNotFoundException if the specified response does not exist
   */
  List<SurveyAnswerResponseModel> getUserAnswer(Long userId)
    throws UserNotFoundException, ResponseNotFoundException;

  /**
   * Retrieves a paginated list of survey responses.
   *
   * @param pageNumber the page number to retrieve
   * @return a List of SurveyAnswerResponseModel objects representing the survey responses
   */
  List<SurveyAnswerResponseModel> getSurveyReport(int pageNumber);
}

