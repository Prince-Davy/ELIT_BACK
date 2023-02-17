package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;

import java.util.Set;

public interface CriteriaService {
  Set<Criteria> createCriterias(Set<Criteria> criterias, FormQuestion formQuestion);

  Criteria updateCriteria(Criteria criterias);
}
