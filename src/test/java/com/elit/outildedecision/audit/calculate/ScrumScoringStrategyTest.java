package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.QuestionResponseModel;
import com.elit.outildedecision.audit.model.ScoringType;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class ScrumScoringStrategyTest {

  private ScrumScoringStrategy strategy;

  private final static double HUNDRED_PERCENT= 100.0;
  private final static int YES= 2;
  private final static int NO= 0;
  private final static QuestionCategory OUTILS_DOCUMENTATION_CATEGORY = QuestionCategory.OUTILS_ET_DOCUMENTATION;

  @Before
  public void setup() {
    strategy = new ScrumScoringStrategy();
  }

  @Test
  public void shouldCalculateCorrectlyScoresForDirectAndReversedQuestions(){
    List<QuestionResponseModel> questions = List.of(
      QuestionResponseModel.builder()
        .questionRef("Q27")
        .categoryDisplayName(OUTILS_DOCUMENTATION_CATEGORY.getDisplayName())
        .category(OUTILS_DOCUMENTATION_CATEGORY)
        .weight(3)
        .auditAnswerValue(YES)
      .scoringType(ScoringType.DIRECT)
        .build(),
      QuestionResponseModel.builder()
        .questionRef("Q28")
        .categoryDisplayName(OUTILS_DOCUMENTATION_CATEGORY.getDisplayName())
        .category(OUTILS_DOCUMENTATION_CATEGORY)
        .weight(3)
        .auditAnswerValue(YES)
        .scoringType(ScoringType.DIRECT)
        .build(),
      QuestionResponseModel.builder()
        .questionRef("Q29")
        .categoryDisplayName(OUTILS_DOCUMENTATION_CATEGORY.toString())
        .category(OUTILS_DOCUMENTATION_CATEGORY)
        .weight(2)
        .auditAnswerValue(NO)
        .scoringType(ScoringType.REVERSED)
        .build()
    );

    AuditResponseModel auditResponseModel = AuditResponseModel.builder().method(MethodType.SCRUM).questions(questions).build();
    strategy.calculateScoring(auditResponseModel);

    Assertions.assertEquals(HUNDRED_PERCENT,auditResponseModel.getGlobalScorePercentage());
    Assertions.assertEquals(1,auditResponseModel.getCategoryScores().size());
    Assertions.assertEquals(HUNDRED_PERCENT,auditResponseModel.getCategoryScores().get(OUTILS_DOCUMENTATION_CATEGORY));
  }


  @Test
  public void shouldSupportScrumMethodType() {
    Assertions.assertEquals(MethodType.SCRUM, strategy.supportedType());
  }



}
