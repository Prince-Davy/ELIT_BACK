package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.StepWeight;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.repository.StepWeightRepo;
import com.elit.outildedecision.service.StepWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class StepWeightServiceImpl implements StepWeightService {

  @Autowired StepWeightRepo stepWeightRepo;
  @Autowired FormStepRepo formStepRepo;

  /**
   * @param criterias
   * @param formStepToPersistId
   * @return Set<StepWeight>
   * @throws StepFormNotFoundException
   */
  @Override
  public Set<StepWeight> createWeights(Set<Criteria> criterias, Long formStepToPersistId)
      throws StepFormNotFoundException {
    FormStep formStepFromDB =
        formStepRepo
            .findById(formStepToPersistId)
            .orElseThrow(() -> new StepFormNotFoundException("This Step Doesn't Exist !"));
    Set<StepWeight> persistedWeights = new HashSet<>();
    float weightValue = (float) 1 / criterias.size();
    for (Criteria criteria : criterias) {
      StepWeight stepWeight =
          StepWeight.builder()
              .criteriaName(criteria.getCriteriaName())
              .weightValue(weightValue)
              .formStep(formStepFromDB)
              .build();
      StepWeight persistedWeight = stepWeightRepo.save(stepWeight);
      persistedWeights.add(persistedWeight);
    }
    return persistedWeights;
  }

  @Override
  public StepWeight updateWeights(StepWeight stepWeight) throws Exception {
    Optional<StepWeight> stepWeightFromDB = stepWeightRepo.findById(stepWeight.getWeightId());
    if (!stepWeightFromDB.isPresent()) {
      throw new Exception("Le poid du critère " + stepWeight.getCriteriaName() + "n'existe pas !");
    }
    StepWeight existingStepWeight = stepWeightFromDB.get();
    if (Objects.nonNull(stepWeight.getWeightValue())) {
      existingStepWeight.setWeightValue(stepWeight.getWeightValue());
    }
    return stepWeightRepo.save(existingStepWeight);
  }
}
