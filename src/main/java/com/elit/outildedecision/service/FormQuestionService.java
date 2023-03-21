
package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import java.util.Set;
/**
 * This interface defines the contract for the Form Question Service, which provides methods to create and
 * update form questions and associated criteria.
 */
public interface FormQuestionService {

  /**
   * Creates new form questions and associated criteria for the specified step.
   *
   * @param formQuestions a set of FormQuestion objects representing the questions to create
   * @param criterias a set of Criteria objects representing the criteria to associate with the questions
   * @param stepId the ID of the step for which to create the questions and criteria
   * @return a set of FormQuestion objects representing the newly created questions
   */
  Set<FormQuestion> createFormQuestions(
    Set<FormQuestion> formQuestions, Set<Criteria> criterias, Long stepId);

  /**
   * Updates an existing form question.
   *
   * @param formQuestion a FormQuestion object representing the question to update
   * @return the updated FormQuestion object
   * @throws QuestionsNotFoundException if the specified question does not exist
   */
  FormQuestion updateFormQuestion(FormQuestion formQuestion) throws QuestionsNotFoundException;
}
