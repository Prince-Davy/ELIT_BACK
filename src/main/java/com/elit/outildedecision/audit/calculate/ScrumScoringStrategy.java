package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.QuestionResponseModel;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public final class ScrumScoringStrategy implements MethodsScoringStrategy {

  @Override
  public MethodType supportedType() {
    return MethodType.SCRUM;
  }

  @Override
  public void calculateScoring(final AuditResponseModel auditResponseModel) {
    Objects.requireNonNull(auditResponseModel, "AuditResponseModel is null");
    List<QuestionResponseModel> questionResponseModels = auditResponseModel.getQuestions();

    this.calculateScoringByQuestion(questionResponseModels);
    this.calculateScoringByCategory(questionResponseModels, auditResponseModel);
  }

  /**
   * compute the score by question.
   *
   * @param questionResponseModels all questions
   */
  private void calculateScoringByQuestion(final List<QuestionResponseModel> questionResponseModels) {
    Objects.requireNonNull(questionResponseModels, "questionResponseModels is null");

    questionResponseModels.stream()
      .filter(Objects::nonNull)
      .forEach(q -> {
        int answerValue = q.getAuditAnswerValue() != null ? q.getAuditAnswerValue() : 0;
        int weight = q.getWeight() != null ? Math.max(0, q.getWeight()) : 0;

        int effectiveValue = q.getScoringType().computeEffectiveValue(answerValue);
        int weightedScore = effectiveValue * weight;
        int maxPossibleScore = 2 * weight;
        double scorePercentage = maxPossibleScore == 0 ? 0.0 : (double) weightedScore / maxPossibleScore * 100;

        q.setCurrentScore(effectiveValue);
        q.setWeightedScore(weightedScore);
        q.setMaxPossibleScore(maxPossibleScore);
        q.setScorePercentage(scorePercentage);
      });
  }

  /**
   * compute score by category.
   * @param questions all questions
   * @param auditResponseModel all responses of questions
   */
  private void calculateScoringByCategory(final List<QuestionResponseModel> questions, final AuditResponseModel auditResponseModel) {
    Objects.requireNonNull(auditResponseModel, "AuditResponseModel (model) ne doit pas être null");

    final List<QuestionResponseModel> safeQuestions =
      (questions == null) ? Collections.emptyList() : questions;

    Map<QuestionCategory, List<QuestionResponseModel>> questionsByCategory = safeQuestions.stream()
      .collect(Collectors.groupingBy(
        QuestionResponseModel::getCategory,
        LinkedHashMap::new,
        Collectors.toList()
      ));

    Map<QuestionCategory, Double> categoryScores = new LinkedHashMap<>();
    int totalWeightedScores = 0;
    int totalMaxPossibleScore = 0;

    for (Map.Entry<QuestionCategory, List<QuestionResponseModel>> entry : questionsByCategory.entrySet()) {
      final QuestionCategory category = entry.getKey();
      final List<QuestionResponseModel> questionResponseModels = entry.getValue();

      int categoryScore  = 0;
      int categoryScoreMax = 0;

      for (QuestionResponseModel questionResponseModel : questionResponseModels) {
        categoryScore += questionResponseModel.getWeightedScore();
        categoryScoreMax += questionResponseModel.getMaxPossibleScore();
      }

      double pourcentageCategorie = percent(categoryScore, categoryScoreMax);
      categoryScores.put(category, round(pourcentageCategorie, 1));

      totalWeightedScores += categoryScore;
      totalMaxPossibleScore += categoryScoreMax;
    }

    double scoreGlobal = percent(totalWeightedScores, totalMaxPossibleScore);
    auditResponseModel.setCategoryScores(categoryScores);
    auditResponseModel.setGlobalScorePercentage(round(scoreGlobal, 1));
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

}
