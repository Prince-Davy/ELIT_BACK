package com.elit.outildedecision.collaboration.multicriteria.calculate;

import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepScoreResponse;
import com.elit.outildedecision.collaboration.multicriteria.entity.Criteria;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormQuestion;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.StepWeight;
import com.elit.outildedecision.collaboration.multicriteria.exception.EscoreException;
import com.elit.outildedecision.collaboration.multicriteria.dto.CriteriaScoreResponse;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class MultiCriteriaScoreCalculatorTest {

  private static final int MAX_ALLOWED_EVALUATION = 4;

  private final MultiCriteriaScoreCalculator calculator = new MultiCriteriaScoreCalculator();

  @Test
  void shouldComputeWeightedEscoreAndCriteriaScoresWhenStepIsComplete() throws Exception {
    // given
    FormStep formStep = stepWithTwoCriteria(0.5f, 0.5f, 2, 4, 4, 2);

    // when
    MultiCriteriaStepScoreResponse result = calculator.computeStepScore(formStep, MAX_ALLOWED_EVALUATION);

    // then
    assertThat(result.getStepName()).isEqualTo("step");
    assertThat(result.getEScore()).isEqualTo(0.75f);
    assertThat(result.getCriteriaScores())
        .extracting(CriteriaScoreResponse::getCriteriaName, CriteriaScoreResponse::getCriteriaScore)
        .containsExactlyInAnyOrder(
            tuple("A", 3.0f),
            tuple("B", 3.0f));
  }

  @Test
  void shouldThrowEscoreExceptionWhenDenominatorIsZero() {
    // given: zero weights make the denominator zero
    FormStep formStep = stepWithTwoCriteria(0f, 0f, 2, 4, 4, 2);

    // when / then
    assertThatThrownBy(() -> calculator.computeStepScore(formStep, MAX_ALLOWED_EVALUATION))
        .isInstanceOf(EscoreException.class);
  }

  @Test
  void shouldThrowQuestionsNotFoundExceptionWhenStepHasNoQuestion() {
    // given
    FormStep formStep = new FormStep();
    formStep.setStepName("step");
    formStep.setFormQuestions(new HashSet<>());
    formStep.setStepWeights(new HashSet<>());

    // when / then
    assertThatThrownBy(() -> calculator.computeStepScore(formStep, MAX_ALLOWED_EVALUATION))
        .isInstanceOf(QuestionsNotFoundException.class);
  }

  @Test
  void shouldThrowQuestionsNotFoundExceptionWhenWeightIsMissing() {
    // given: a question with criterion "A" but no matching weight
    FormStep formStep = new FormStep();
    formStep.setStepName("step");
    formStep.setStepWeights(new HashSet<>());
    FormQuestion question = new FormQuestion();
    question.setQuestionId(1L);
    question.setCriterias(new HashSet<>(Set.of(criteria(11L, "A", 2))));
    formStep.setFormQuestions(new HashSet<>(Set.of(question)));

    // when / then
    assertThatThrownBy(() -> calculator.computeStepScore(formStep, MAX_ALLOWED_EVALUATION))
        .isInstanceOf(QuestionsNotFoundException.class);
  }

  private FormStep stepWithTwoCriteria(final float weightA, final float weightB,
                                       final int q1ValueA, final int q1ValueB,
                                       final int q2ValueA, final int q2ValueB) {
    FormStep formStep = new FormStep();
    formStep.setStepName("step");

    FormQuestion question1 = new FormQuestion();
    question1.setQuestionId(1L);
    question1.setCriterias(new HashSet<>(Set.of(criteria(11L, "A", q1ValueA), criteria(12L, "B", q1ValueB))));
    FormQuestion question2 = new FormQuestion();
    question2.setQuestionId(2L);
    question2.setCriterias(new HashSet<>(Set.of(criteria(21L, "A", q2ValueA), criteria(22L, "B", q2ValueB))));
    formStep.setFormQuestions(new HashSet<>(Set.of(question1, question2)));

    formStep.setStepWeights(new HashSet<>(Set.of(weight(101L, "A", weightA), weight(102L, "B", weightB))));
    return formStep;
  }

  private Criteria criteria(final Long id, final String name, final int value) {
    Criteria criteria = new Criteria();
    criteria.setCriteriaId(id);
    criteria.setCriteriaName(name);
    criteria.setCriteriaValue(value);
    return criteria;
  }

  private StepWeight weight(final Long id, final String name, final float value) {
    StepWeight weight = new StepWeight();
    weight.setWeightId(id);
    weight.setCriteriaName(name);
    weight.setWeightValue(value);
    return weight;
  }
}
