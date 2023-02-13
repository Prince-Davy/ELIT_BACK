package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.exception.QuestionFormatNotValidException;

import java.util.List;
import java.util.Optional;

/**
 * interface used to lead the surveyQuestion service class
 */
public interface SurveyQuestionService {

    /**
     * create a new question for the existing survey
     * @return created question for checking
     */
    public SurveyQuestion createQuestion(SurveyQuestion question) throws QuestionFormatNotValidException;

    /**
     * allow to modify an existing question
     * @param question with modified data
     * @return modified item for checking
     */
    public SurveyQuestion modifyQuestion(Long questionId, SurveyQuestion question) throws QuestionFormatNotValidException;


    /**
     * delete an existing question from survey
     *
     * @param questionId for question to be deleted
     * @return deleted data for checking
     */
    public Optional<SurveyQuestion> deleteQuestion(Long questionId); // to be deleted from list if used

    /**
     * display a selected question
     *
     * @param questionId of question needed to be returned
     * @return the selected question
     */
    public Optional<SurveyQuestion> selectQuestion(Long questionId);

    /**
     * display a list of existing questions
     * @return a list of questions saved in database
     */
    public List<SurveyQuestion> getQuestionsList();

}
