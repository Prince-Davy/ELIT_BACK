package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.repository.CriteriaRepository;
import com.elit.outildedecision.service.CriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class CriteriaServiceImpl implements CriteriaService {

  @Autowired CriteriaRepository criteriaRepository;

  /**
   * @param criterias
   * @param formQuestion
   * @return Set<Criteria>
   */
  @Override
  public Set<Criteria> createCriterias(Set<Criteria> criterias, FormQuestion formQuestion) {
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

  @Override
  public Criteria updateCriteria(Criteria criteria) {
    Criteria criteriaFromDB = criteriaRepository.findById(criteria.getCriteriaId()).get();
    if (Objects.nonNull(criteria.getCriteriaValue())) {
      criteriaFromDB.setCriteriaValue(criteria.getCriteriaValue());
    }
    return criteriaRepository.save(criteriaFromDB);
  }
}
