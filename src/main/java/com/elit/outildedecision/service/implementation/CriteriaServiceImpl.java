
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.repository.CriteriaRepository;
import com.elit.outildedecision.service.CriteriaService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
/**
 * this class is the implementation of the CriteriaService interface.
 * it's used to implement the business logic of the Criteria entity
 */
@Service
public class CriteriaServiceImpl implements CriteriaService {
  /**
   * CriteriaRepository is the repository of the Criteria entity.
   */
  private final CriteriaRepository criteriaRepository;
  /**
   * CriteriaServiceImpl is the constructor of the class.
   * it's used to inject the CriteriaRepository
   * @param criteriaRepository is the repository of the Criteria entity
   */
  public CriteriaServiceImpl(final CriteriaRepository criteriaRepository) {
    this.criteriaRepository = criteriaRepository;
  }

  /**
   * createCriterias is used to create a set of criterias.
   * @param criterias is the set of criterias to create
   * @param formQuestion is the formQuestion to which the criterias are linked
   * @return the set of created criterias
   */
  @Override
  public Set<Criteria> createCriterias(final Set<Criteria> criterias,
                                       final FormQuestion formQuestion) {
    Set<Criteria> persistedCriterias = new HashSet<>();
    for (Criteria criteria : criterias) {
      Criteria criteriaToPersist =
          Criteria.builder()
              .criteriaName(criteria.getCriteriaName())
              .formQuestion(formQuestion)
              .criteriaValue(0)
              .build();
      persistedCriterias.add(criteriaRepository.save(criteriaToPersist));
    }
    return persistedCriterias;
  }
  /**
   * updateCriteria is used to update a criteria.
   *
   * @param criteria is the criteria to update
   * @return the updated criteria
   */
  @Override
  public Criteria updateCriteria(final Criteria criteria) {
    Criteria criteriaFromDB = criteriaRepository.findById(criteria.getCriteriaId()).get();
    if (Objects.nonNull(criteria.getCriteriaValue())) {
      criteriaFromDB.setCriteriaValue(criteria.getCriteriaValue());
    }
    return criteriaRepository.save(criteriaFromDB);
  }
}
