
package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.StepWeight;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.exception.StepWeightNotFoundException;
import java.util.Set;

/**
 * This interface defines the contract for the Step Weight Service, which provides methods to create, retrieve,
 * update, and delete step weights associated with form steps.
 */
public interface StepWeightService {

  /**
   * Creates step weights for the specified form step and criteria.
   *
   * @param criterias the Set of Criteria objects to create step weights for
   * @param formStepToPersistId the ID of the form step to associate the step weights with
   * @return a Set of StepWeight objects representing the newly created step weights
   * @throws StepFormNotFoundException if the specified form step does not exist
   */
  Set<StepWeight> createWeights(Set<Criteria> criterias, Long formStepToPersistId)
    throws StepFormNotFoundException;

  /**
   * Updates the specified step weight.
   *
   * @param stepWeight the StepWeight object to update
   * @return the updated StepWeight object
   * @throws StepWeightNotFoundException if the specified step weight does not exist
   */
  StepWeight updateWeights(StepWeight stepWeight) throws StepWeightNotFoundException;
}
