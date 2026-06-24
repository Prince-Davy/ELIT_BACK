package com.elit.outildedecision.audit.repository;

import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.shared.util.MethodType;
import com.elit.outildedecision.shared.util.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link CategoryComment} entities.
 * Provides methods for querying comments based on question category and evaluation level.
 */
@Repository
public interface CategoryCommentRepository extends JpaRepository<CategoryComment, Long> {

  /**
   * finds a catagory comment by its associated category and evaluation level and method type.
   * @param methodType the method type
   * @param category the question category
   * @param level he evaluation level (BON, MOYEN, MAUVAIS)
   * @return the global comment returned from type , level and category
   */
  Optional<CategoryComment> findByMethodAndCategoryAndLevel(MethodType methodType, QuestionCategory category, LevelScore level);

  /**
   * find a list of categoryComment by its method type.
   * @param methodType the method type
   * @return list of category comment
   */
  List<CategoryComment> findAllByMethod(MethodType methodType);
}
