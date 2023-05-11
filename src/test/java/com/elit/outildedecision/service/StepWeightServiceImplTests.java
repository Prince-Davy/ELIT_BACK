package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.StepWeight;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.exception.StepWeightNotFoundException;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.repository.StepWeightRepo;
import com.elit.outildedecision.service.implementation.StepWeightServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StepWeightServiceImplTests {
  @Mock
  StepWeightRepo stepWeightRepo;
  @Mock
  FormStepRepo formStepRepo;
  @InjectMocks
  StepWeightServiceImpl stepWeightService;
  Long formId = 1L;
  FormStep formStep;
  Set<Criteria> criterias;
  StepWeight stepWeight;
  Criteria criteria;

  @BeforeEach
  void setup(){
    formId = 1L;
    criterias = new HashSet<>();
    formStep=new FormStep();
    formStep.setStepId(formId);
    criteria =
      Criteria.builder()
        .criteriaId(formId)
        .criteriaName("criteria")
        .criteriaValue(1)
        .build();
    stepWeight =
      StepWeight.builder()
        .weightId(formId)
        .criteriaName(criteria.getCriteriaName())
        .weightValue(1)
        .formStep(formStep)
        .build();
  }
  @Test
  void shouldCreateWeights_Exception() throws Exception {
    StepFormNotFoundException e = Assertions.assertThrows(StepFormNotFoundException.class,()->stepWeightService.createWeights(null,null));
    Assertions.assertEquals("This Step Doesn't Exist !", e.getMessage());
  }

  @Test
  void shouldCreateWeights_Success() throws Exception {
    Set<StepWeight> persistedWeights = new HashSet<>();
    Criteria criteria = new Criteria();
    criterias.add(criteria);
    persistedWeights.add(null);
    when(formStepRepo.findById(formId)).thenReturn(Optional.ofNullable(formStep));
    Assertions.assertEquals(stepWeightService.createWeights(criterias,formId),persistedWeights);
  }

  @Test
  void shouldUpdateWeights_ThrowStepWeightNotFound() throws Exception {
    StepWeight step = new StepWeight();
    StepWeightNotFoundException p = Assertions.assertThrows(StepWeightNotFoundException.class, ()-> stepWeightService.updateWeights(step));
    Assertions.assertEquals("Le poid du critère " + step.getCriteriaName() + " n'existe pas !", p.getMessage());
  }
  @Test
  void shouldUpdateWeights_Success() throws Exception {
    when(stepWeightRepo.findById(formId)).thenReturn(Optional.of(stepWeight));
    when(stepWeightRepo.save(stepWeight)).thenReturn(stepWeight);
    Assertions.assertEquals(stepWeight,stepWeightService.updateWeights(stepWeight));
  }
}
