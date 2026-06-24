package com.elit.outildedecision.ai.kiviat.calculate;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.kiviat.dto.KiviatMatrixQuestion;
import com.elit.outildedecision.ai.kiviat.dto.KiviatMatrixResponse;
import com.elit.outildedecision.ai.kiviat.dto.KiviatResponseId;
import com.elit.outildedecision.ai.kiviat.dto.KiviatScores;
import com.elit.outildedecision.ai.kiviat.dto.KiviatTheme;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KiviatScoreCalculatorTest {

  private final KiviatScoreCalculator calculator = new KiviatScoreCalculator();

  @Test
  void shouldAverageNotesAndRoundToTwoDecimalsWhenThemeHasSeveralAnswers() {
    // given: two questions on the same theme, answered with notes 1 and 2 (avg 1.5)
    List<KiviatMatrixQuestion> questions = List.of(
        question("1", KiviatTheme.FlexibiliteGestion, response(KiviatResponseId.low, 1)),
        question("2", KiviatTheme.FlexibiliteGestion, response(KiviatResponseId.high, 2)));
    List<AiAnswer> answers = List.of(answer("1", "low"), answer("2", "high"));

    // when
    KiviatScores scores = calculator.computeProjectScores(questions, answers);

    // then
    assertThat(scores.id()).isNull();
    assertThat(scores.flexibiliteGestion()).isEqualTo(1.5);
  }

  @Test
  void shouldScoreZeroWhenThemeHasNoMatchingAnswer() {
    // given: a question on one theme only, leaving every other theme empty
    List<KiviatMatrixQuestion> questions = List.of(
        question("1", KiviatTheme.Coordination, response(KiviatResponseId.high, 4)));
    List<AiAnswer> answers = List.of(answer("1", "high"));

    // when
    KiviatScores scores = calculator.computeProjectScores(questions, answers);

    // then
    assertThat(scores.coordination()).isEqualTo(4.0);
    assertThat(scores.flexibiliteGestion()).isZero();
    assertThat(scores.flexibiliteSolution()).isZero();
    assertThat(scores.confianceEtDelegation()).isZero();
    assertThat(scores.maitrisePerimetre()).isZero();
  }

  @Test
  void shouldIgnoreAnswersThatMatchNoQuestionOrResponse() {
    // given: one answer targets an unknown question, another an unknown response id
    List<KiviatMatrixQuestion> questions = List.of(
        question("1", KiviatTheme.MaitrisePerimetre, response(KiviatResponseId.low, 1)));
    List<AiAnswer> answers = List.of(
        answer("1", "low"),
        answer("99", "low"),
        answer("1", "high"));

    // when
    KiviatScores scores = calculator.computeProjectScores(questions, answers);

    // then: only the single valid answer (note 1) is kept
    assertThat(scores.maitrisePerimetre()).isEqualTo(1.0);
  }

  @Test
  void shouldScoreEveryAxisZeroWhenNoQuestion() {
    // given
    List<KiviatMatrixQuestion> questions = List.of();
    List<AiAnswer> answers = List.of(answer("1", "low"));

    // when
    KiviatScores scores = calculator.computeProjectScores(questions, answers);

    // then
    assertThat(scores.flexibiliteGestion()).isZero();
    assertThat(scores.flexibiliteSolution()).isZero();
    assertThat(scores.confianceEtDelegation()).isZero();
    assertThat(scores.maitrisePerimetre()).isZero();
    assertThat(scores.coordination()).isZero();
  }

  private KiviatMatrixQuestion question(final String questionNumber, final KiviatTheme theme,
                                        final KiviatMatrixResponse response) {
    return new KiviatMatrixQuestion(questionNumber, "question " + questionNumber, theme, List.of(response));
  }

  private KiviatMatrixResponse response(final KiviatResponseId id, final int note) {
    return new KiviatMatrixResponse(id, id.name(), note);
  }

  private AiAnswer answer(final String question, final String answer) {
    return AiAnswer.builder().question(question).answer(answer).build();
  }
}
