package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.service.FormStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/steps")
public class StepController {
  @Autowired FormStepService formStepService;

  /**
   * @return FormStep
   * @throws StepFormNotFoundException
   */
  @GetMapping("/{formId}/{stepName}")
  public FormStep getFormStep(@PathVariable Long formId, @PathVariable String stepName)
      throws StepFormNotFoundException {
    return formStepService.findStepByNameAndFormId(formId, stepName);
  }

  @PostMapping(consumes = {"application/json"})
  public ResponseEntity<String> createFormStep(@RequestBody FormStep formStep)
      throws FormStepAlreadyExistsException, StepFormNotFoundException {
    Set<Criteria> criterias = new HashSet<>();
    criterias = formStep.getFormQuestions().stream().findFirst().orElse(null).getCriterias();
    return formStepService.createFormStep(
        formStep.getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId(),
        formStep.getStepName(),
        criterias,
        formStep.getFormQuestions());
  }

  @PutMapping
  public ResponseEntity updateFormStep(@Valid @RequestBody FormStep step) throws Exception {
    return formStepService.updateFormStep(step);
  }
}
