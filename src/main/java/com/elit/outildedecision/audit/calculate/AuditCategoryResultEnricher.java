package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.audit.service.CategoryCommentService;
import com.elit.outildedecision.util.MethodType;
import org.springframework.stereotype.Component;


/**
 * Component responsible for enriching {@link AuditCategoryResult} objects
 * with additional metadata such as the evaluation level and associated comment.
 * This class encapsulates the logic for determining the {@link LevelScore}
 * based on the score percentage and retrieving the corresponding {@link CategoryComment}
 * from the database via {@link CategoryCommentService}.
*/
@Component
public class AuditCategoryResultEnricher {

  private final CategoryCommentService categoryCommentService;

  /**
   * Constructs an instance of {@code AuditCategoryResultEnricher}.
   *
   * @param categoryCommentService the service used to retrieve category comments
   *                                based on category and level
   */
  public AuditCategoryResultEnricher(final CategoryCommentService categoryCommentService) {
    this.categoryCommentService = categoryCommentService;
  }


  /**
   * Enriches the given {@link AuditCategoryResult} by .
   *Determining the {@link LevelScore} based on the score percentage
   * Fetching the corresponding {@link CategoryComment}
   * Setting the level, comment ID, and comment text into the DTO
   *
   * @param auditCategoryResult the category result to enrich
   * @param methodType the methodType
   * @throws IllegalArgumentException if no comment is found for the given category and level
   */
  public void enrichAuditCategoryResult(final MethodType methodType, final AuditCategoryResult auditCategoryResult) {
    LevelScore levelScore = resolveLevelFromScore(auditCategoryResult.getScorePercentageCategory());

    CategoryComment categoryComment = categoryCommentService.findByMethodAndCategoryAndLevel(methodType, auditCategoryResult.getCategory(), levelScore);
    auditCategoryResult.setLevel(levelScore);
    auditCategoryResult.setCategoryComment(categoryComment);
  }


  /**
   * Resolves the {@link LevelScore} based on the given score percentage.
   *
   * @param percentage the score percentage to evaluate
   * @return the corresponding {@link LevelScore}
   */
  private LevelScore resolveLevelFromScore(final double percentage) {
    return LevelScore.fromPercentage(percentage);
  }
}
