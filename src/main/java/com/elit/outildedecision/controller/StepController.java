
package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.exception.StepWeightNotFoundException;
import com.elit.outildedecision.model.MultiCriteriaStepModel;
import com.elit.outildedecision.service.FormStepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Set;

/**
 * Multi criteria Step Controller.
 **/
@RestController
@RequestMapping("/steps")
public class StepController {
  /**
   * FormStepService.
   */
  private final FormStepService formStepService;
  /**
   * Constructor of StepController.
   * @param formStepService FormStepService
   */
  public StepController(final FormStepService formStepService) {
    this.formStepService = formStepService;
  }

  /**
   * Get a step by its name and form id.
   * @param formId the form id
   * @param stepName the step name
   * @throws StepFormNotFoundException when the step is not found in the database
   * @return FormStep
   */
  @GetMapping("/{formId}/{stepName}")
  public FormStep getFormStep(@PathVariable final Long formId,
                              @PathVariable final String stepName)
      throws StepFormNotFoundException {
    return formStepService.findStepByNameAndFormId(formId, stepName);
  }
  /**
   * Create a step.
   *
   * @param formStep FormStep object
   * @throws FormStepAlreadyExistsException when the step already exists in the database
   * @throws StepFormNotFoundException when the form is not found in the database
   * @throws QuestionsNotFoundException when the questions are not found in the form step sent in the request
   * @return FormStep
   */
  @PostMapping(consumes = {"application/json"})
  public ResponseEntity<String> createFormStep(@RequestBody final FormStep formStep)
    throws FormStepAlreadyExistsException, StepFormNotFoundException, QuestionsNotFoundException {
    Set<Criteria> criterias;
    criterias = formStep.getFormQuestions().stream()
      .findFirst()
      .orElseThrow(() -> new QuestionsNotFoundException("Aucune question n'a été trouvée"))
      .getCriterias();
    return formStepService.createFormStep(
        formStep.getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId(),
        formStep.getStepName(),
        criterias,
        formStep.getFormQuestions());
  }
  /**
   * Update a step.
   *
   * @param step FormStep object
   * @throws StepFormNotFoundException when the step is not found in the database
   * @throws QuestionsNotFoundException when the questions are not found in the database
   * @throws StepWeightNotFoundException when the step weight is not found in the database
   * @return FormStep
   */
  @PutMapping
  public ResponseEntity<String> updateFormStep(@Valid @RequestBody final MultiCriteriaStepModel step)
    throws StepFormNotFoundException, QuestionsNotFoundException, StepWeightNotFoundException {
    return formStepService.updateFormStep(step);
  }
}
