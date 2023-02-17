package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface FormStepService {
  FormStep findStepByNameAndFormId(Long formId, String stepName) throws StepFormNotFoundException;

  ResponseEntity<String> createFormStep(
      Long formId, String stepName, Set<Criteria> criterias, Set<FormQuestion> formQuestions)
      throws FormStepAlreadyExistsException, StepFormNotFoundException;

  //    FormStep updateFormStep(Long formId, String stepName, Set<StepWeight> stepWeight,
  // Set<FormQuestion> formQuestions) throws StepFormNotFoundException;

  ResponseEntity updateFormStep(FormStep step) throws Exception;
}
