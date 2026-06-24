package com.elit.outildedecision.collaboration.survey.service;

import com.elit.outildedecision.collaboration.survey.dto.SurveyQuestionRequest;
import com.elit.outildedecision.collaboration.survey.dto.SurveyQuestionResponse;
import com.elit.outildedecision.collaboration.survey.entity.SurveyQuestion;
import com.elit.outildedecision.collaboration.survey.exception.QuestionFormatNotValidException;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * This interface defines the contract for the Survey Question Service, which provides methods to create, modify,
 * and retrieve survey questions.
 */
public interface SurveyQuestionService {

  /**
   * Creates a new survey question.
   *
   * @param question a SurveyQuestionRequest object representing the question to create
   * @return the newly created SurveyQuestion object
   * @throws QuestionFormatNotValidException if the specified question format is not valid
   */
  SurveyQuestion createQuestion(SurveyQuestionRequest question)
    throws QuestionFormatNotValidException;

  /**
   * Modifies an existing survey question.
   *
   * @param questionId the ID of the question to modify
   * @param question a SurveyQuestionRequest object representing the modified question
   * @return the updated SurveyQuestion object
   * @throws QuestionFormatNotValidException if the specified question format is not valid
   * @throws QuestionsNotFoundException if the specified question does not exist
   */
  SurveyQuestion modifyQuestion(Long questionId, SurveyQuestionRequest question)
    throws QuestionFormatNotValidException, QuestionsNotFoundException;

  /**
   * Deletes the survey question with the specified ID.
   *
   * @param questionId the ID of the question to delete
   * @return an Optional containing the deleted SurveyQuestion object, or an empty Optional if the specified question
   *         does not exist
   * @throws QuestionsNotFoundException if the specified question does not exist
   */
  Optional<SurveyQuestion> deleteQuestion(Long questionId) throws QuestionsNotFoundException;

  /**
   * Retrieves the survey question with the specified ID.
   *
   * @param questionId the ID of the question to retrieve
   * @return the SurveyQuestion object with the specified ID
   * @throws QuestionsNotFoundException if the specified question does not exist
   */
  SurveyQuestion selectQuestion(Long questionId) throws QuestionsNotFoundException;

  /**
   * Retrieves a list of all survey questions.
   *
   * @return a List of SurveyQuestionResponse objects representing all survey questions
   */
  List<SurveyQuestionResponse> getQuestionsList();
}
