package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.exception.StepWeightNotFoundException;
import org.springframework.http.ResponseEntity;
import java.util.Set;

public interface FormStepService {
  FormStep findStepByNameAndFormId(Long formId, String stepName) throws StepFormNotFoundException;

  ResponseEntity<String> createFormStep(
      Long formId, String stepName, Set<Criteria> criterias, Set<FormQuestion> formQuestions)
      throws FormStepAlreadyExistsException, StepFormNotFoundException;

  ResponseEntity updateFormStep(FormStep step) throws StepFormNotFoundException, QuestionsNotFoundException, StepWeightNotFoundException;
}
