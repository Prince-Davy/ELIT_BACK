package com.elit.outildedecision.audit.calculate;



import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.entity.QuestionResponseScore;
import com.elit.outildedecision.audit.entity.ScoringType;
import com.elit.outildedecision.shared.util.MethodType;
import com.elit.outildedecision.shared.util.QuestionCategory;
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
 * Component responsible for compute the score of the AuditScore with Agility strategy.
 */
@Component
public final class AgilityScoringStrategy implements MethodsScoringStrategy {

  /** {@inheritDoc} */
  @Override
  public Set<MethodType> supportedTypes() {
    return EnumSet.of(MethodType.SCRUM, MethodType.SCALED_AGILITY, MethodType.VMODEL, MethodType.WATERFALL);
  }

  /** {@inheritDoc} */
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
        final double percentage = (maxPossible == 0) ? 0d : effectiveValue * weight * 100d / maxPossible;

        return QuestionResponseScore.builder()
          .answerLabel(question.getAuditAnswerLabel())
          .answerValue(question.getAuditAnswerValue())
          .score(round(percentage))
          .question(question)
          .build();
      }).toList();
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
        .scorePercentageCategory(round(percentageCategory))
        .build();
      categoryScores.add(categoryResult);

      totalWeightedScores += categoryWeighted;
      totalMaxPossibleScore += categoryMax;
    }

    final double scoreGlobalPercentage = round(percent(totalWeightedScores, totalMaxPossibleScore));

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
    return auditScore;

  }

  /**
   * compute the percent.
   * @param part the part
   * @param total the total
   * @return the percent compute with the part and the total
   */
  private static double percent(final int part, final int total) {
    if (total <= 0L) {
      return 0.0;
    } else {
      return part * 100.0 / total;
    }
  }

  /**
   * compute the round.
   *
   * @param value the value
   * @return the round
   */
  private static double round(final double value) {
    return Math.round(value);
  }

  /**
   * Returns the weight of the question, ensuring it is non-negative and not null.
   *
   * @param q the question object
   * @return the weight value, or 0 if null or negative
   */
  private static int safeWeight(final Question q) {
    final Integer w = q.getWeight();
    return (w == null) ? 0 : Math.max(0, w);
  }

  /**
   * Returns the audit answer value of the question, defaulting to 0 if null.
   *
   * @param q the question object
   * @return the audit answer value, or 0 if null
   */
  private static int safeAnswerValue(final Question q) {
    final Integer v = q.getAuditAnswerValue();
    return (v == null) ? 0 : Math.max(0, v);
  }

  /**
   * Computes the effective value of the question's answer using its scoring type.
   *
   * @param q the question object
   * @return the effective value computed from the answer value
   */
  private static int effectiveValue(final Question q) {
    final ScoringType scoringType = q.getScoringType();
    return (scoringType == null) ? 0 : scoringType.computeEffectiveValue(safeAnswerValue(q));
  }

  /**
   * Calculates the maximum possible score for a given weight.
   * The score is twice the weight, but never negative.
   *
   * @param weight the weight value
   * @return the maximum possible score
   */
  private static int maxPossibleScore(final int weight) {
    return 2 * Math.max(0, weight);
  }

}
