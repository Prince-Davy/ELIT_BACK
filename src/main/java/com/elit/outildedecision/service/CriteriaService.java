
package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import java.util.Set;

/**
 * This interface represents a service that handles criteria-related operations.
 */
public interface CriteriaService {

  /**
   * Creates the given set of criteria for the given form question.
   *
   * @param criterias the set of criteria to create
   * @param formQuestion the form question to create the criteria for
   * @return a set of Criteria representing the created criteria
   */
  Set<Criteria> createCriterias(Set<Criteria> criterias, FormQuestion formQuestion);

  /**
   * Updates the given criteria.
   *
   * @param criteria the criteria to update
   * @return Criteria
   */
  Criteria updateCriteria(Criteria criteria);
}
