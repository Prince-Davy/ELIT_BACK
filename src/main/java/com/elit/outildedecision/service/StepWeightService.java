package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.StepWeight;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.exception.StepWeightNotFoundException;

import java.util.Set;

public interface StepWeightService {
  Set<StepWeight> createWeights(Set<Criteria> criterias, Long formStepToPersistId)
      throws StepFormNotFoundException;

  StepWeight updateWeights(StepWeight stepWeight) throws StepWeightNotFoundException;
}
