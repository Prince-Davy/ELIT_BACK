package com.elit.outildedecision.ai.methodology.evaluation.calculate;

import com.elit.outildedecision.ai.methodology.evaluation.api.dto.ExplanatoryFactorDto;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Pure, stateless selector trimming the methodology evaluations to their most relevant subset. It
 * keeps the top evaluations and, within each, the highest-scoring explanatory factors, working only
 * on data already mapped in memory without any repository access, so the surrounding service keeps the
 * load/map responsibility and this logic stays unit-testable in isolation.
 */
@Component
public class MethodologyEvaluationSelector {

  /**
   * Maximum number of methodology evaluations kept (evaluations are already sorted by decreasing
   * score upstream by the AI backend).
   */
  private static final int MAX_EVALUATIONS = 3;

  /**
   * Keeps the top {@value #MAX_EVALUATIONS} evaluations and, within each, the {@code maxFactors}
   * explanatory factors with the highest question score, sorted by decreasing score.
   *
   * @param evaluations the evaluations to trim, already mapped and ordered by decreasing score
   * @param maxFactors  the maximum number of explanatory factors kept per evaluation
   * @return the trimmed evaluations
   */
  public List<MethodologyEvaluationDto> selectTopEvaluations(final List<MethodologyEvaluationDto> evaluations,
                                                             final int maxFactors) {
    return evaluations.stream()
        .map(evaluation -> selectTopFactors(evaluation, maxFactors))
        .limit(MAX_EVALUATIONS)
        .toList();
  }

  /**
   * Rebuilds an evaluation keeping only its highest-scoring explanatory factors.
   *
   * @param evaluation the evaluation to trim
   * @param maxFactors the maximum number of explanatory factors kept
   * @return the evaluation with its top factors
   */
  private MethodologyEvaluationDto selectTopFactors(final MethodologyEvaluationDto evaluation, final int maxFactors) {
    final List<ExplanatoryFactorDto> topFactors = evaluation.getExplanatoryFactorDto().stream()
        .sorted(Comparator.comparing(ExplanatoryFactorDto::getQuestionScore).reversed())
        .limit(maxFactors)
        .toList();

    return MethodologyEvaluationDto.builder()
        .methodology(evaluation.getMethodology())
        .score(evaluation.getScore())
        .explanatoryFactorDto(topFactors)
        .build();
  }
}
