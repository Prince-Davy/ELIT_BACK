package com.elit.outildedecision.collaboration.multicriteria.calculate;

import com.elit.outildedecision.collaboration.multicriteria.dto.CriteriaScoreResponse;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepScoreResponse;
import com.elit.outildedecision.collaboration.multicriteria.entity.Criteria;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormQuestion;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.StepWeight;
import com.elit.outildedecision.collaboration.multicriteria.exception.EscoreException;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pure, stateless calculator for the multi-criteria score of a single form step. It computes the
 * eScore and the per-criteria scores from data already loaded in memory (questions, criteria and
 * weights of the step), without any repository access, so the surrounding service keeps the
 * load/persist responsibility and this logic stays unit-testable in isolation.
 */
@Component
public class MultiCriteriaScoreCalculator {

  /**
   * Computes the score of a form step: its eScore and the score of each evaluation criterion.
   *
   * <p>The set of criteria taken into account is the one of the question carrying the most criteria
   * (some questions deliberately skip a criterion); for each of these criteria the values are summed
   * over every question of the step. The eScore is the weighted ratio
   * {@code Σ(weight·sum) / Σ(weight·maxEvaluation·count)}.</p>
   *
   * @param formStep            the form step to score, with its questions, criteria and weights loaded
   * @param maxAllowedEvaluation the maximum value a single criterion evaluation can reach
   * @return the computed step score (name, eScore and per-criteria scores)
   * @throws QuestionsNotFoundException if the step has no question, or a weight is missing
   * @throws EscoreException            if the eScore denominator is zero
   */
  public MultiCriteriaStepScoreResponse computeStepScore(final FormStep formStep, final int maxAllowedEvaluation)
      throws QuestionsNotFoundException, EscoreException {
    Set<Criteria> referenceCriterias = extractReferenceCriterias(formStep);
    Map<String, CriteriaAggregate> aggregatesByName = aggregateCriteriaValues(formStep);

    float numerator = 0f;
    float denominator = 0f;
    List<CriteriaScoreResponse> criteriaScores = new ArrayList<>();
    for (Criteria criteria : referenceCriterias) {
      String criteriaName = criteria.getCriteriaName();
      CriteriaAggregate aggregate = aggregatesByName.get(criteriaName);
      float weight = findWeight(formStep, criteriaName);
      float criteriaMax = (float) maxAllowedEvaluation * aggregate.occurrences;
      numerator += weight * aggregate.valueSum;
      denominator += weight * criteriaMax;
      criteriaScores.add(
          new CriteriaScoreResponse(criteriaName, (float) aggregate.valueSum / aggregate.occurrences));
    }

    if (denominator == 0) {
      throw new EscoreException("Le dénominateur du e-score ne peux être 0");
    }

    return MultiCriteriaStepScoreResponse.builder()
        .stepName(formStep.getStepName())
        .eScore(numerator / denominator)
        .criteriaScores(criteriaScores)
        .build();
  }

  /**
   * Returns the criteria of the question holding the most criteria, used as the reference set of
   * criteria names (and iteration order) for the step.
   *
   * @param formStep the form step
   * @return the reference set of criteria
   * @throws QuestionsNotFoundException if the step has no question
   */
  private Set<Criteria> extractReferenceCriterias(final FormStep formStep) throws QuestionsNotFoundException {
    return formStep.getFormQuestions()
        .stream()
        .sorted((q1, q2) -> Integer.compare(q2.getCriterias().size(), q1.getCriterias().size()))
        .findAny()
        .orElseThrow(() -> new QuestionsNotFoundException("Erreur lors de la récupération des questions"))
        .getCriterias();
  }

  /**
   * Aggregates, in a single in-memory pass, the value sum and occurrence count of each criterion
   * name across all questions of the step.
   *
   * @param formStep the form step
   * @return the aggregates indexed by criterion name
   */
  private Map<String, CriteriaAggregate> aggregateCriteriaValues(final FormStep formStep) {
    Map<String, CriteriaAggregate> aggregates = new HashMap<>();
    for (FormQuestion question : formStep.getFormQuestions()) {
      for (Criteria criteria : question.getCriterias()) {
        aggregates.computeIfAbsent(criteria.getCriteriaName(), name -> new CriteriaAggregate())
            .add(criteria.getCriteriaValue());
      }
    }
    return aggregates;
  }

  /**
   * Finds the weight of a criterion within the step.
   *
   * @param formStep     the form step holding the weights
   * @param criteriaName the criterion name
   * @return the criterion weight value
   * @throws QuestionsNotFoundException if no weight matches the criterion name
   */
  private float findWeight(final FormStep formStep, final String criteriaName) throws QuestionsNotFoundException {
    return formStep.getStepWeights()
        .stream()
        .filter(stepWeight -> stepWeight.getCriteriaName().equals(criteriaName))
        .map(StepWeight::getWeightValue)
        .findFirst()
        .orElseThrow(() -> new QuestionsNotFoundException("Pondération introuvable pour le critère " + criteriaName));
  }

  /**
   * Mutable accumulator of the value sum and occurrence count of a criterion name.
   */
  private static final class CriteriaAggregate {

    /**
     * Sum of the criterion values across the step.
     */
    private int valueSum;

    /**
     * Number of occurrences of the criterion across the step.
     */
    private int occurrences;

    /**
     * Adds a single criterion value to the aggregate.
     *
     * @param value the criterion value to add
     */
    private void add(final int value) {
      this.valueSum += value;
      this.occurrences++;
    }
  }
}
