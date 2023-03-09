package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.StepWeight;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.exception.StepWeightNotFoundException;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.repository.StepWeightRepo;
import com.elit.outildedecision.service.StepWeightService;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Service implementation for managing Step Weights.
 */
@Service
public class StepWeightServiceImpl implements StepWeightService {
  /**
   * Repository for managing StepWeight entities.
   */
  private final StepWeightRepo stepWeightRepo;
  /**
   * Repository for managing FormStep entities.
   */
  private final FormStepRepo formStepRepo;
  /**
   *Constructs a new StepWeightServiceImpl with the given StepWeightRepo and FormStepRepo.
   *
   *@param stepWeightRepo the repository for managing Step Weights
   *@param formStepRepo the repository for managing Form Steps
   */
  public StepWeightServiceImpl(final StepWeightRepo stepWeightRepo,
                               final FormStepRepo formStepRepo) {
    this.stepWeightRepo = stepWeightRepo;
    this.formStepRepo = formStepRepo;
  }
  /**
   * Creates a set of Step Weights for the given set of Criteria and Form Step ID.
   *
   * @param criterias the set of Criteria for which to create Step Weights
   * @param formStepToPersistId the ID of the Form Step to which the Step Weights should be persisted
   * @return the set of persisted Step Weights
   * @throws StepFormNotFoundException if the Form Step does not exist
   */
  @Override
  public Set<StepWeight> createWeights(final Set<Criteria> criterias,
                                       final Long formStepToPersistId) throws StepFormNotFoundException {
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
  /**
   * Updates an existing Step Weight.
   *
   * @param stepWeight the Step Weight to update
   * @return the updated Step Weight
   * @throws StepWeightNotFoundException if the Step Weight does not exist
   */
  @Override
  public StepWeight updateWeights(final StepWeight stepWeight) throws StepWeightNotFoundException {
    Optional<StepWeight> stepWeightFromDB = stepWeightRepo.findById(stepWeight.getWeightId());
    if (!stepWeightFromDB.isPresent()) {
      throw new StepWeightNotFoundException("Le poid du critère " + stepWeight.getCriteriaName() + "n'existe pas !");
    }
    StepWeight existingStepWeight = stepWeightFromDB.get();
    if (Objects.nonNull(stepWeight.getWeightValue())) {
      existingStepWeight.setWeightValue(stepWeight.getWeightValue());
    }
    return stepWeightRepo.save(existingStepWeight);
  }
}
