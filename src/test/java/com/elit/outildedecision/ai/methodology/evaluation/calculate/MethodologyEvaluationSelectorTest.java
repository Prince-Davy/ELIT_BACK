package com.elit.outildedecision.ai.methodology.evaluation.calculate;

import com.elit.outildedecision.ai.methodology.evaluation.api.dto.ExplanatoryFactorDto;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MethodologyEvaluationSelectorTest {

  private final MethodologyEvaluationSelector selector = new MethodologyEvaluationSelector();

  @Test
  void shouldKeepHighestScoringFactorsSortedWhenLimitingByMaxFactors() {
    // given
    MethodologyEvaluationDto evaluation = evaluation("Scrum", 0.9,
        factor("low", 1.0), factor("high", 3.0), factor("mid", 2.0));

    // when: keep only the top 2 factors
    List<MethodologyEvaluationDto> result = selector.selectTopEvaluations(List.of(evaluation), 2);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getExplanatoryFactorDto())
        .extracting(ExplanatoryFactorDto::getQuestionScore)
        .containsExactly(3.0, 2.0);
  }

  @Test
  void shouldKeepOnlyTopThreeEvaluations() {
    // given: four evaluations already ordered by decreasing score upstream
    List<MethodologyEvaluationDto> evaluations = List.of(
        evaluation("A", 0.9, factor("q", 1.0)),
        evaluation("B", 0.8, factor("q", 1.0)),
        evaluation("C", 0.7, factor("q", 1.0)),
        evaluation("D", 0.6, factor("q", 1.0)));

    // when
    List<MethodologyEvaluationDto> result = selector.selectTopEvaluations(evaluations, 5);

    // then
    assertThat(result)
        .extracting(MethodologyEvaluationDto::getMethodology)
        .containsExactly("A", "B", "C");
  }

  @Test
  void shouldReturnEmptyWhenNoEvaluation() {
    // when
    List<MethodologyEvaluationDto> result = selector.selectTopEvaluations(List.of(), 3);

    // then
    assertThat(result).isEmpty();
  }

  private MethodologyEvaluationDto evaluation(final String methodology, final double score,
                                             final ExplanatoryFactorDto... factors) {
    return MethodologyEvaluationDto.builder()
        .methodology(methodology)
        .score(score)
        .explanatoryFactorDto(List.of(factors))
        .build();
  }

  private ExplanatoryFactorDto factor(final String question, final double questionScore) {
    return ExplanatoryFactorDto.builder()
        .question(question)
        .answer("answer")
        .questionScore(questionScore)
        .build();
  }
}
