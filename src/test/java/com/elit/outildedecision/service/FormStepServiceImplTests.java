package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.service.implementation.FormStepServiceImpl;
import com.elit.outildedecision.util.ModelSamples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
class FormStepServiceImplTests {

  @Mock
  FormStepRepo formStepRepo;

  @Mock
  MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;

  @Mock
  StepWeightService stepWeightService;

  @Mock
  FormQuestionService formQuestionService;

  @InjectMocks
  FormStepServiceImpl formStepService;

  Long formId; String stepName;
  FormStep formStep;
  Set<Criteria> criterias;
  Set<FormQuestion> formQuestions;

  @BeforeEach
  void setup() {
    formId = 1l;
    stepName = "step1";
    formStep = ModelSamples.FormStepSamples.getFormStep();
    formQuestions = new HashSet<>();
    criterias = new HashSet<>();
  }

  @Test
  void shouldFindStepByNameAndFormId_success() throws StepFormNotFoundException {
    when(formStepRepo.findByFormIdAndStepName(formId, stepName)).thenReturn(formStep);
    assertEquals(formStep,formStepService.findStepByNameAndFormId(formId, stepName));
  }

  @Test
  void shouldFindStepByNameAndFormId_throwException() {
    when(formStepRepo.findByFormIdAndStepName(formId, stepName)).thenReturn(null);
    Assertions.assertThrows(StepFormNotFoundException.class, () -> formStepService.findStepByNameAndFormId(formId, stepName));
  }

  @Test
   void shouldCreateFormStep_Success() throws Exception {
    MultiCriteriaAnalysis form = new MultiCriteriaAnalysis();
    FormStep formStep = new FormStep();
    given(multiCriteriaAnalysisRepository.findById(formId)).willReturn(Optional.of(form));
    given(formStepRepo.findByFormIdAndStepName(formId, stepName)).willReturn(null);
    given(formStepRepo.save(any())).willReturn(formStep);
    given(formStepRepo.save(formStep)).willReturn(formStep);
    given(stepWeightService.createWeights(anySet(), anyLong())).willReturn(null);
    given(formQuestionService.createFormQuestions(anySet(), anySet(), anyLong())).willReturn(null);

    ResponseEntity<String> response = formStepService.createFormStep(formId, stepName, criterias, formQuestions);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("FormStep Created Successfully", response.getBody());
    verify(multiCriteriaAnalysisRepository).findById(formId);
    verify(formStepRepo).findByFormIdAndStepName(formId, stepName);
    verify(formStepRepo).save(any(FormStep.class));
    verify(stepWeightService).createWeights(criterias, formStep.getStepId());
    verify(formQuestionService).createFormQuestions(formQuestions, criterias, formStep.getStepId());
  }

  @Test
   void shouldCreateFormStep_ThrowFormStepAlreadyExistsException() throws Exception {
    MultiCriteriaAnalysis form = new MultiCriteriaAnalysis();
    FormStep formStep = new FormStep();
    given(multiCriteriaAnalysisRepository.findById(formId)).willReturn(Optional.of(form));
    given(formStepRepo.findByFormIdAndStepName(formId, stepName)).willReturn(formStep);
    Assertions.assertThrows(FormStepAlreadyExistsException.class, () -> formStepService.createFormStep(formId, stepName, criterias, formQuestions));

  }
}
