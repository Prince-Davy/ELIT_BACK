package com.elit.outildedecision.collaboration.predictibility.calculate;

import com.elit.outildedecision.collaboration.predictibility.dto.MethodologyScoreModel;
import com.elit.outildedecision.collaboration.vote.entity.Vote;
import com.elit.outildedecision.shared.util.MethodologyType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class MethodologyScoreCalculatorTest {

  private final MethodologyScoreCalculator calculator = new MethodologyScoreCalculator();

  @Test
  void shouldComputeScoresAndSelectHighestVotedMethodologyIgnoringUnconfirmedVotes() {
    // given
    List<Vote> votes = List.of(
        confirmedVote("AGILE", 3),
        confirmedVote("AGILE", 2),
        confirmedVote("CLASSIC", 4),
        unconfirmedVote("AGILE", 100));

    // when
    MethodologyScoreResult result = calculator.computeScores(votes);

    // then: scores are returned in MethodologyType declaration order (AGILE, HYBRID, CLASSIC)
    assertThat(result.methodologyScores())
        .extracting(MethodologyScoreModel::getName, MethodologyScoreModel::getValue,
            MethodologyScoreModel::getValueVoteSum, MethodologyScoreModel::getNbVotes)
        .containsExactly(
            tuple("AGILE", 5 * 0.2f / 2, 5, 2),
            tuple("HYBRID", 0f, 0, 0),
            tuple("CLASSIC", 4 * 0.2f / 1, 4, 1));
    assertThat(result.chosenMethodology()).isEqualTo(MethodologyType.AGILE);
    assertThat(result.tie()).isFalse();
  }

  @Test
  void shouldSelectFirstMethodologyInDeclarationOrderWhenNoConfirmedVote() {
    // given / when
    MethodologyScoreResult result = calculator.computeScores(List.of());

    // then: every score is zero, the tie is resolved on the first declared methodology
    assertThat(result.methodologyScores())
        .extracting(MethodologyScoreModel::getValueVoteSum, MethodologyScoreModel::getValue)
        .containsExactly(tuple(0, 0f), tuple(0, 0f), tuple(0, 0f));
    assertThat(result.chosenMethodology()).isEqualTo(MethodologyType.AGILE);
    // a zero maximum (no confirmed vote) is never reported as a tie
    assertThat(result.tie()).isFalse();
  }

  @Test
  void shouldReportTieWhenTwoMethodologiesShareTheHighestVoteSum() {
    // given: AGILE and CLASSIC both reach a cumulated 3, HYBRID stays at 0
    List<Vote> votes = List.of(
        confirmedVote("AGILE", 3),
        confirmedVote("CLASSIC", 3));

    // when
    MethodologyScoreResult result = calculator.computeScores(votes);

    // then: tie flagged, chosen methodology resolved on declaration order (AGILE first)
    assertThat(result.tie()).isTrue();
    assertThat(result.chosenMethodology()).isEqualTo(MethodologyType.AGILE);
  }

  private Vote confirmedVote(final String methodology, final int predictibilityValue) {
    return buildVote(methodology, predictibilityValue, true);
  }

  private Vote unconfirmedVote(final String methodology, final int predictibilityValue) {
    return buildVote(methodology, predictibilityValue, false);
  }

  private Vote buildVote(final String methodology, final int predictibilityValue, final boolean confirmed) {
    Vote vote = new Vote();
    vote.setChosenPMM(methodology);
    vote.setPredictibilityValue(predictibilityValue);
    vote.setIsConfirmed(confirmed);
    return vote;
  }
}
