package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.entity.CategoryComment;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.model.AuditCategoryResultDTO;
import com.elit.outildedecision.audit.service.CategoryCommentService;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuditCategoryResultEnricherTest {


  @Mock
  private CategoryCommentService categoryCommentService;

  private AuditCategoryResultEnricher enricher;

  private static final double GOOD_SCORE = 85.0;
  private static final String GOOD_TEXT = "Bonne Organisation";
  @BeforeEach
  void setUp() {
    enricher = new AuditCategoryResultEnricher(categoryCommentService);
  }

  @Test
  void enrichAuditCategoryResult_shouldSetLevelAndComment() {

    AuditCategoryResultDTO dto = AuditCategoryResultDTO.builder()
      .scorePercentageCategory(GOOD_SCORE)
      .category(QuestionCategory.ORGANISATIONS.getDisplayName())
      .build();

    CategoryComment comment = CategoryComment.builder()
      .id(1L)
      .text(GOOD_TEXT)
      .method(MethodType.SCRUM)
      .category(QuestionCategory.ORGANISATIONS)
      .build();

    when(categoryCommentService.findByMethodAndCategoryAndLevel(MethodType.SCRUM,QuestionCategory.ORGANISATIONS, LevelScore.BON))
      .thenReturn(comment);

    enricher.enrichAuditCategoryResult(MethodType.SCRUM,dto);

    assertEquals(LevelScore.BON, dto.getLevel());
    assertEquals(1L, dto.getCategoryCommentId());
    assertEquals(GOOD_TEXT, dto.getText());
  }

  @Test
  void enrichAuditCategoryResult_shouldThrowExceptionIfCommentNotFound() {
    AuditCategoryResultDTO dto = AuditCategoryResultDTO.builder()
      .scorePercentageCategory(GOOD_SCORE)
      .category(QuestionCategory.ORGANISATIONS.getDisplayName())
      .build();

    when(categoryCommentService.findByMethodAndCategoryAndLevel(any(), any(), any()))
      .thenThrow(new IllegalArgumentException("Comment not found"));

    assertThrows(IllegalArgumentException.class, () -> enricher.enrichAuditCategoryResult(MethodType.SCRUM,dto));
  }
}

