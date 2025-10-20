package com.elit.outildedecision.audit.service.impl;

import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.repository.CategoryCommentRepository;
import com.elit.outildedecision.audit.service.CategoryCommentService;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing category comments.
 */
@Service
public class CategoryCommentServiceImpl implements CategoryCommentService {

  private final CategoryCommentRepository categoryCommentRepository;

  /**
   * Constructor for CategoryCommentServiceImpl.
   * Initializes the service with the given category comment repository.
   *
   * @param categoryCommentRepository the repository used to access category comments
   */
  public CategoryCommentServiceImpl(final CategoryCommentRepository categoryCommentRepository) {
    this.categoryCommentRepository = categoryCommentRepository;
  }

  /**
   * finds a catagory comment by its associated categoty and evaluation level and method type .
   * @param methodType the method type
   * @param category the question category
   * @param level he evaluation level (BON, MOYEN, MAUVAIS)
   * @return category comment returned from method type , category and level
   */
  @Override
  public CategoryComment findByMethodAndCategoryAndLevel(final MethodType methodType, final QuestionCategory category, final LevelScore level) {
    return categoryCommentRepository.findByMethodAndCategoryAndLevel(methodType, category, level)
      .orElseThrow(() -> new IllegalArgumentException("Comment not found for category " + category + " and level " + level + " and type : " + methodType));
  }

  /**
   * Retrieves all category comments.
   *
   * @return a list of all CategoryComment instances
   */
  @Override
  public List<CategoryComment> findAll() {
    return categoryCommentRepository.findAll();
  }

  /**
   * return the list of category comment by method type .
   * @param methodType the method type
   * @return the list of categoryComment find by method type
   */
  @Override
  public List<CategoryComment> findAllByMethod(final MethodType methodType) {
    return categoryCommentRepository.findAllByMethod(methodType);
  }
}
