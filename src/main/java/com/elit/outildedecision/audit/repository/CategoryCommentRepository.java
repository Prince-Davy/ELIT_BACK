package com.elit.outildedecision.audit.repository;

import com.elit.outildedecision.audit.entity.CategoryComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link CategoryComment} entities.
 * Provides methods for querying comments based on question category and evaluation level.
 */
@Repository
public interface CategoryCommentRepository extends JpaRepository<CategoryComment, Long> {

  /**
   * finds a catagory comment by its associated categoty and evaluation level and method type.
   * @param methodType the method type
   * @param category the question category
   * @param level he evaluation level (BON, MOYEN, MAUVAIS)
   * @return the global comment returned from type , level and category
   */
  Optional<CategoryComment> findByMethodAndCategoryAndLevel(MethodType methodType, QuestionCategory category, LevelScore level);

}
