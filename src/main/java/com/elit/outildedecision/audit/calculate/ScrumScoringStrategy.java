package com.elit.outildedecision.audit.calculate;



import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.entity.QuestionResponseScore;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.EnumMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Component responsible for compute the score of the AuditScore with Scrum strategy.
 */
@Component
public final class ScrumScoringStrategy implements MethodsScoringStrategy {

  private final AuditCategoryResultEnricher auditCategoryResultEnricher;

  /**
   * Constructor for ScrumScoringStrategy .
   * @param auditCategoryResultEnricher a service that use for enrich  {@link AuditCategoryResult} objects
   */
  public ScrumScoringStrategy(final AuditCategoryResultEnricher auditCategoryResultEnricher) {
    this.auditCategoryResultEnricher = auditCategoryResultEnricher;
  }

  @Override
  public Set<MethodType> supportedTypes() {
    return EnumSet.of(MethodType.SCRUM, MethodType.SCALED_AGILITY, MethodType.VMODEL);
  }

  @Override
  public AuditScore calculateScoring(final Audit audit) {
    Objects.requireNonNull(audit, "audit is null");

    final List<Question> questions = Optional.ofNullable(audit.getQuestions())
      .orElseGet(Collections::emptyList);

    List<QuestionResponseScore> questionResponseScores = this.calculateScoringByQuestion(questions);
    return this.calculateScoringByCategory(questionResponseScores, audit);
  }

  /**
   * compute the score by question.
   * @param auditQuestions all questions
   * @return a list of questionResponseScore with score
   */
  private List<QuestionResponseScore> calculateScoringByQuestion(final List<Question> auditQuestions) {
    Objects.requireNonNull(auditQuestions, "auditQuestions is null");

    return auditQuestions.stream()
      .filter(Objects::nonNull)
      .map(question -> {

        final int weight = safeWeight(question);
        final int effectiveValue = effectiveValue(question);
        final int maxPossible = maxPossibleScore(weight);
        final double percentage = (maxPossible == 0) ? 0d : effectiveValue * (double) weight * 100d / (double) maxPossible;

        return QuestionResponseScore.builder()
          .answerLabel(question.getAuditAnswerLabel())
          .answerValue(question.getAuditAnswerValue())
          .score(round(percentage, 1))
          .question(question)
          .build();
      }).collect(Collectors.toList());
  }

  /**
   * compute score by category.
   * @param questionResponseScores all questionResponseScores
   * @param audit audit
   * @return an auditScore with score cumputed
   */
  private AuditScore calculateScoringByCategory(final List<QuestionResponseScore> questionResponseScores, final Audit audit) {
    Objects.requireNonNull(audit, "audit must not be null");
    Objects.requireNonNull(questionResponseScores, "questionResponseScores must not be null");

    // regroupement par catégorie
    final Map<QuestionCategory, List<QuestionResponseScore>> scoresByCategory = questionResponseScores.stream()
      .filter(questionResponseScore -> Objects.nonNull(questionResponseScore.getQuestion()))
      .collect(Collectors.groupingBy(
        questionResponseScore -> questionResponseScore.getQuestion().getCategory(),
        () -> new EnumMap<>(QuestionCategory.class),
        Collectors.toList()
      ));

    final List<AuditCategoryResult> categoryScores = new ArrayList<>();
    int totalWeightedScores = 0;
    int totalMaxPossibleScore = 0;

    for (Map.Entry<QuestionCategory, List<QuestionResponseScore>> entry : scoresByCategory.entrySet()) {
      final QuestionCategory category = entry.getKey();
      final List<QuestionResponseScore> scores  = entry.getValue();


      final int categoryWeighted = scores.stream()
        .map(QuestionResponseScore::getQuestion)
        .filter(Objects::nonNull)
        .mapToInt(q -> effectiveValue(q) * safeWeight(q))
        .sum();

      final int categoryMax = scores.stream()
        .map(QuestionResponseScore::getQuestion)
        .filter(Objects::nonNull)
        .mapToInt(q -> maxPossibleScore(safeWeight(q)))
        .sum();

      final double percentageCategory = percent(categoryWeighted, categoryMax);

      final AuditCategoryResult categoryResult = AuditCategoryResult.builder()
        .category(category)
        .scorePercentageCategory(round(percentageCategory, 1))
        .build();


      auditCategoryResultEnricher.enrichAuditCategoryResult(audit.getMethod(), categoryResult);
      categoryScores.add(categoryResult);

      totalWeightedScores += categoryWeighted;
      totalMaxPossibleScore += categoryMax;
    }

    final double scoreGlobalPercentage = round(percent(totalWeightedScores, totalMaxPossibleScore), 1);

    final AuditScore auditScore = AuditScore.builder()
      .audit(audit)
      .globalScorePercentage(scoreGlobalPercentage)
      .auditCategoryResults(categoryScores)
      .questionResponses(questionResponseScores)
      .submissionDate(new Timestamp(System.currentTimeMillis()))
      .user(audit.getUser())
      .build();

    questionResponseScores.forEach(questionResponseScore -> questionResponseScore.setAuditScore(auditScore));
    categoryScores.forEach(categoryResult -> categoryResult.setAuditScore(auditScore));

    auditScore.setQuestionResponses(questionResponseScores);
    auditScore.setQuestionResponses(questionResponseScores);
    return auditScore;

  }

  /**
   * compute the percent.
   * @param part the part
   * @param total the total
   * @return the percent compute with the part and the total
   */
  private static double percent(final int part, final int total) {
    return total <= 0 ? 0.0 : part * 100.0 / total;
  }

  /**
   * compute the round.
   * @param value the value
   * @param decimals the decimal
   * @return the round
   */
  private static double round(final double value, final int decimals) {
    if (decimals < 0) {
      return value;
    } else {
      double factor = Math.pow(10, decimals);
      return Math.round(value * factor) / factor;
    }
  }
private static int safeWeight(final Question q) {
  final Integer w = q.getWeight();
  return (w == null) ? 0 : Math.max(0, w);
}

private static int safeAnswerValue(final Question q) {
  final Integer v = q.getAuditAnswerValue();
  return (v == null) ? 0 : v;
}

private static int effectiveValue(final Question q) {
  return q.getScoringType().computeEffectiveValue(safeAnswerValue(q));
}

private static int maxPossibleScore(final int weight) {
  return 2 * Math.max(0, weight);
}

}
