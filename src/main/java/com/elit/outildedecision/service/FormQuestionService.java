package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;

import java.util.Set;

public interface FormQuestionService {

  //    List<FormQuestion> saveFormQuestion(FormQuestion[] formQuestions, Long formId);

  //    List<FormQuestion> saveFormQuestion(FormQuestion[] formQuestions, Map<String, Map<String,
  // Integer>> questionCriterias, Long formId);

  Set<FormQuestion> createFormQuestions(
      Set<FormQuestion> formQuestions, Set<Criteria> criterias, Long stepId);

  FormQuestion updateFormQuestion(FormQuestion formQuestions) throws Exception;
}
