package com.elit.outildedecision.collaboration.predictibility.calculate;

import com.elit.outildedecision.collaboration.predictibility.dto.MethodologyScoreModel;
import com.elit.outildedecision.collaboration.vote.entity.Vote;
import com.elit.outildedecision.shared.util.MethodologyType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure, stateless calculator of the methodology scores of a predictibility analysis. From the
 * confirmed votes already loaded in memory it computes, for each {@link MethodologyType}, the
 * cumulated vote sum, the number of voters and the resulting weighted score, then selects the
 * methodology gathering the highest cumulated votes — without any repository access.
 */
@Component
public class MethodologyScoreCalculator {

  /**
   * Weight applied to the cumulated votes when computing a methodology score.
   */
  private static final float SUM_CONSTANT = 0.2f;

  /**
   * Computes the methodology scores and the selected methodology from the given votes.
   *
   * @param votes the votes of the predictibility analysis (confirmed votes are taken into account)
   * @return the per-methodology scores (in {@link MethodologyType} declaration order) and the
   *         methodology gathering the highest cumulated votes
   */
  public MethodologyScoreResult computeScores(final List<Vote> votes) {
    Map<String, VoteAggregate> aggregatesByMethodology = aggregateConfirmedVotes(votes);

    List<MethodologyScoreModel> methodologyScores = new ArrayList<>();
    MethodologyType[] methodologyTypes = MethodologyType.values();
    for (MethodologyType methodologyType : methodologyTypes) {
      VoteAggregate aggregate = aggregatesByMethodology.getOrDefault(methodologyType.name(), VoteAggregate.EMPTY);
      float result = aggregate.voteSum * SUM_CONSTANT / aggregate.voterCount;
      methodologyScores.add(MethodologyScoreModel.builder()
          .name(methodologyType.name())
          .value(Float.isNaN(result) ? 0 : result)
          .valueVoteSum(aggregate.voteSum)
          .nbVotes((int) aggregate.voterCount)
          .build());
    }

    int maxVoteSum = maxVoteSum(methodologyScores);
    MethodologyType chosenMethodology = selectChosenMethodology(methodologyTypes, methodologyScores, maxVoteSum);
    boolean tie = isTie(methodologyScores, maxVoteSum);
    return new MethodologyScoreResult(methodologyScores, chosenMethodology, tie);
  }

  /**
   * Returns the highest cumulated votes among the methodology scores.
   *
   * @param methodologyScores the methodology scores
   * @return the maximum cumulated votes
   */
  private int maxVoteSum(final List<MethodologyScoreModel> methodologyScores) {
    return methodologyScores.stream()
        .map(MethodologyScoreModel::getValueVoteSum)
        .mapToInt(Integer::intValue)
        .max()
        .orElseThrow(() -> new IllegalStateException("Pas de valeur max trouvée pour les méthodologies."));
  }

  /**
   * Determines whether the highest cumulated votes are shared by several methodologies, which the
   * project owner must then break manually. A maximum of zero (no confirmed vote yet) is never a
   * tie.
   *
   * @param methodologyScores the methodology scores
   * @param maxVoteSum        the highest cumulated votes
   * @return {@code true} when at least two methodologies reach the same non-zero maximum
   */
  private boolean isTie(final List<MethodologyScoreModel> methodologyScores, final int maxVoteSum) {
    if (maxVoteSum <= 0) {
      return false;
    }
    long countAtMax = methodologyScores.stream()
        .filter(score -> score.getValueVoteSum() == maxVoteSum)
        .count();
    return countAtMax > 1;
  }

  /**
   * Aggregates, in a single pass, the cumulated vote value and the number of voters of each
   * methodology over the confirmed votes.
   *
   * @param votes the votes to aggregate
   * @return the aggregates indexed by methodology name
   */
  private Map<String, VoteAggregate> aggregateConfirmedVotes(final List<Vote> votes) {
    Map<String, VoteAggregate> aggregates = new HashMap<>();
    for (Vote vote : votes) {
      if (Boolean.TRUE.equals(vote.getIsConfirmed())) {
        aggregates.computeIfAbsent(vote.getChosenPMM(), methodology -> new VoteAggregate())
            .add(vote.getPredictibilityValue());
      }
    }
    return aggregates;
  }

  /**
   * Selects the methodology gathering the highest cumulated votes, keeping the first one in
   * declaration order in case of a tie.
   *
   * @param methodologyTypes  the methodologies in declaration order
   * @param methodologyScores the scores parallel to {@code methodologyTypes}
   * @param maxVoteSum        the highest cumulated votes among the scores
   * @return the chosen methodology
   */
  private MethodologyType selectChosenMethodology(final MethodologyType[] methodologyTypes,
                                                  final List<MethodologyScoreModel> methodologyScores,
                                                  final int maxVoteSum) {
    for (int i = 0; i < methodologyTypes.length; i++) {
      if (methodologyScores.get(i).getValueVoteSum() == maxVoteSum) {
        return methodologyTypes[i];
      }
    }
    throw new IllegalStateException("Aucune méthodologie trouvée.");
  }

  /**
   * Mutable accumulator of the cumulated vote value and the number of voters of a methodology.
   */
  private static final class VoteAggregate {

    /**
     * Shared empty aggregate for methodologies without any confirmed vote.
     */
    private static final VoteAggregate EMPTY = new VoteAggregate();

    /**
     * Number of confirmed voters for the methodology.
     */
    private long voterCount;

    /**
     * Cumulated predictibility value of the confirmed votes for the methodology.
     */
    private int voteSum;

    /**
     * Adds a single confirmed vote value to the aggregate.
     *
     * @param predictibilityValue the predictibility value of the vote
     */
    private void add(final int predictibilityValue) {
      this.voterCount++;
      this.voteSum += predictibilityValue;
    }
  }
}
