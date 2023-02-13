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
 * interface used to lead the surveyAnswer service class
 */
public interface SurveyAnswerService {

    /**
     * recover answer sent by the user
     *
     * @param userAnswer objet that contain all information about the answer given
     *                   (user identification, question, and response)
     * @return question and its answer
     */
    ResponseEntity<String> replyToQuestion(List<SurveyAnswerRequestModel> userAnswer) throws UserNotFoundException, QuestionsNotFoundException, ResponseNotFoundException, ResponseNotValidException;

    /**
     * display response given by one user on survey
     * @param userId represents user that responding to the survey
     * @return user answers
     */
    List<SurveyAnswerResponseModel> getUserAnswer(Long userId) throws UserNotFoundException, ResponseNotFoundException;

    /**
     * display all results for all users on the survey
     *
     * @return satisfactory survey results
     */
    List<SurveyAnswerResponseModel> getSurveyReport(int pageNumber);

}
