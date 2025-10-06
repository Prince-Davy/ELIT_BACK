package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.entity.CategoryComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.model.AuditCategoryResultDTO;
import com.elit.outildedecision.audit.service.CategoryCommentService;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import org.springframework.stereotype.Component;


/**
 * Component responsible for enriching {@link AuditCategoryResultDTO} objects
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
   * Enriches the given {@link AuditCategoryResultDTO} by .
   *Determining the {@link LevelScore} based on the score percentage
   * Fetching the corresponding {@link CategoryComment}
   * Setting the level, comment ID, and comment text into the DTO
   *
   * @param resultDTO the category result DTO to enrich
   * @param methodType the methodType
   * @throws IllegalArgumentException if no comment is found for the given category and level
   */
  public void enrichAuditCategoryResult(final MethodType methodType, final AuditCategoryResultDTO resultDTO) {
    LevelScore levelScore = resolveLevelFromScore(resultDTO.getScorePercentageCategory());

    CategoryComment categoryComment = categoryCommentService.findByMethodAndCategoryAndLevel(methodType, QuestionCategory.getQuestionCategory(resultDTO.getCategory()), levelScore);
    resultDTO.setLevel(levelScore);
    resultDTO.setCategoryCommentId(categoryComment.getId());
    resultDTO.setText(categoryComment.getText());
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
