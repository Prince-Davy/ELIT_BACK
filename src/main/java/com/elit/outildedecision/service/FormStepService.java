
package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.exception.StepWeightNotFoundException;
import com.elit.outildedecision.model.MultiCriteriaStepModel;
import org.springframework.http.ResponseEntity;
import java.util.Set;
/**
 * This interface defines the contract for the Form Step Service, which provides methods to create, retrieve,
 * and update form steps and associated criteria and questions.
 */
public interface FormStepService {

  /**
   * Finds the form step with the specified name and form ID.
   *
   * @param formId the ID of the form containing the step to find
   * @param stepName the name of the step to find
   * @return a FormStep object representing the matching step
   * @throws StepFormNotFoundException if the specified step and form combination does not exist
   */
  FormStep findStepByNameAndFormId(Long formId, String stepName) throws StepFormNotFoundException;

  /**
   * Creates a new form step with the specified name, criteria, and questions.
   *
   * @param formId the ID of the form for which to create the step
   * @param stepName the name of the new step
   * @param criterias a set of Criteria objects representing the criteria to associate with the new step
   * @param formQuestions a set of FormQuestion objects representing the questions to associate with the new step
   * @return a ResponseEntity indicating the success or failure of the create operation
   * @throws FormStepAlreadyExistsException if a step with the specified name already exists for the specified form
   * @throws StepFormNotFoundException if the specified form does not exist
   */
  ResponseEntity<String> createFormStep(
    Long formId, String stepName, Set<Criteria> criterias, Set<FormQuestion> formQuestions)
    throws FormStepAlreadyExistsException, StepFormNotFoundException;

  /**
   * Updates an existing form step.
   *
   * @param step a FormStep object representing the step to update
   * @return a ResponseEntity indicating the success or failure of the update operation
   * @throws StepFormNotFoundException if the specified step and form combination does not exist
   * @throws QuestionsNotFoundException if any of the questions associated with the step do not exist
   * @throws StepWeightNotFoundException if the weight property of the step cannot be found
   */
  ResponseEntity<String> updateFormStep(MultiCriteriaStepModel step)
    throws StepFormNotFoundException, QuestionsNotFoundException, StepWeightNotFoundException;
}
