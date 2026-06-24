package com.elit.outildedecision.collaboration.predictibility.calculate;

import com.elit.outildedecision.collaboration.predictibility.dto.MethodologyScoreModel;
import com.elit.outildedecision.shared.util.MethodologyType;

import java.util.List;

/**
 * Result of a methodology score computation: the per-methodology scores, the methodology
 * gathering the highest cumulated votes, and whether that maximum is shared by several
 * methodologies (a tie that the project owner must break manually).
 *
 * @param methodologyScores  the score of each methodology, in declaration order
 * @param chosenMethodology  the methodology gathering the highest cumulated votes
 * @param tie                {@code true} when at least two methodologies share the highest non-zero
 *                           cumulated votes
 */
public record MethodologyScoreResult(List<MethodologyScoreModel> methodologyScores,
                                     MethodologyType chosenMethodology,
                                     boolean tie) {
}
