package com.elit.outildedecision.audit.service;

import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;

import java.util.List;

/**
 * Service interface for managing category comments.
 */
public interface CategoryCommentService {

  /**
   * finds a catagory comment by its associated categoty and evaluation level and method type .
   * @param methodType the method type
   * @param category the question category
   * @param level he evaluation level (BON, MOYEN, MAUVAIS)
   * @return category comment returned from method type , category and level
   */
  CategoryComment findByMethodAndCategoryAndLevel(MethodType methodType, QuestionCategory category, LevelScore level);

  /**
   * Retrieves all category comments.
   * @return a list of all CategoryComment instances
   */
  List<CategoryComment> findAll();
}
