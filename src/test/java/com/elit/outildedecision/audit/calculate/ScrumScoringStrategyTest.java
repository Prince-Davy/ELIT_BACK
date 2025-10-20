package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.model.ScoringType;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
public class ScrumScoringStrategyTest {

  private AgilityScoringStrategy strategy;

  private final static double HUNDRED_PERCENT= 100.0;
  private final static int YES= 2;
  private final static int NO= 0;
  private final static QuestionCategory OUTILS_DOCUMENTATION_CATEGORY = QuestionCategory.OUTILS_ET_DOCUMENTATION;


  @BeforeEach
  public void setup() {
    strategy = new AgilityScoringStrategy();
  }

  @Test
  public void shouldCalculateCorrectlyScoresForDirectAndReversedQuestions() {
    // Arrange
    Audit audit = Audit.builder()
      .auditId(1L)
      .method(MethodType.SCRUM)
      .user(User.builder().userId(1L).build())
      .auditLaunchDate(Timestamp.valueOf(LocalDateTime.now()))
      .build();

    Question directQuestion = Question.builder()
      .questionRef("Q1")
      .weight(2)
      .auditAnswerValue(YES) // 2
      .auditAnswerLabel("Oui")
      .category(OUTILS_DOCUMENTATION_CATEGORY)
      .scoringType(ScoringType.DIRECT)
      .audit(audit).build();

    Question reversedQuestion = Question.builder()
      .questionRef("Q2")
      .weight(2)
      .auditAnswerValue(NO)
      .auditAnswerLabel("Non")
      .category(OUTILS_DOCUMENTATION_CATEGORY)
      .scoringType(ScoringType.REVERSED)
      .audit(audit).build();

    audit.setQuestions(List.of(directQuestion, reversedQuestion));

    AuditScore result = strategy.calculateScoring(audit);

    assertNotNull(result);
    assertEquals(1, result.getAuditCategoryResults().size());
    assertEquals(OUTILS_DOCUMENTATION_CATEGORY, result.getAuditCategoryResults().get(0).getCategory());

    assertEquals(HUNDRED_PERCENT, result.getGlobalScorePercentage());

  }

  @Test
  public void shouldSupportScrumMethodType() {
    assertEquals(EnumSet.of(MethodType.SCRUM, MethodType.SCALED_AGILITY, MethodType.VMODEL, MethodType.WATERFALL), strategy.supportedTypes());
  }

}
