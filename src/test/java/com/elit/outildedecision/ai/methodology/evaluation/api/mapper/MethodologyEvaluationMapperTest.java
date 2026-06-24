package com.elit.outildedecision.ai.methodology.evaluation.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.ExplanatoryFactorDto;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MethodologyEvaluationMapperTest {

  private final MethodologyEvaluationMapper mapper = new MethodologyEvaluationMapper();

  @Test
  void shouldCopyMethodologyAndScoreAndKeepOnlyAnswersScoredByEvaluation() {
    // given
    MethodologyEvaluation evaluation = evaluation("Scrum", 0.9,
        Map.of("Q1", 3.0, "Q2", 1.0));
    List<AiAnswer> aiAnswers = List.of(
        answer("Q1", "Answer 1"),
        answer("Q2", "Answer 2"),
        answer("Q3", "Unscored answer"));

    // when
    MethodologyEvaluationDto dto = mapper.toDto(evaluation, aiAnswers);

    // then
    assertThat(dto.getMethodology()).isEqualTo("Scrum");
    assertThat(dto.getScore()).isEqualTo(0.9);
    assertThat(dto.getExplanatoryFactorDto())
        .extracting(ExplanatoryFactorDto::getQuestion)
        .containsExactlyInAnyOrder("Q1", "Q2");
    assertThat(dto.getExplanatoryFactorDto())
        .extracting(ExplanatoryFactorDto::getQuestion,
            ExplanatoryFactorDto::getAnswer,
            ExplanatoryFactorDto::getQuestionScore)
        .containsExactlyInAnyOrder(
            tuple("Q1", "Answer 1", 3.0),
            tuple("Q2", "Answer 2", 1.0));
  }

  @Test
  void shouldReturnEmptyFactorsWhenNoAnswer() {
    // given
    MethodologyEvaluation evaluation = evaluation("Scrum", 0.9, Map.of("Q1", 3.0));

    // when
    MethodologyEvaluationDto dto = mapper.toDto(evaluation, List.of());

    // then
    assertThat(dto.getExplanatoryFactorDto()).isEmpty();
  }

  @Test
  void shouldReturnEmptyFactorsWhenEvaluationHasNoQuestionScore() {
    // given
    MethodologyEvaluation evaluation = evaluation("Scrum", 0.9, Map.of());

    // when
    MethodologyEvaluationDto dto = mapper.toDto(evaluation, List.of(answer("Q1", "Answer 1")));

    // then
    assertThat(dto.getExplanatoryFactorDto()).isEmpty();
  }

  private MethodologyEvaluation evaluation(final String methodology, final double score,
                                           final Map<String, Double> questionScores) {
    return MethodologyEvaluation.builder()
        .methodology(methodology)
        .score(score)
        .questionScores(questionScores)
        .build();
  }

  private AiAnswer answer(final String question, final String answer) {
    return AiAnswer.builder()
        .question(question)
        .answer(answer)
        .build();
  }
}
