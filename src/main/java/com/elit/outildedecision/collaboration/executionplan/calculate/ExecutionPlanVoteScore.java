package com.elit.outildedecision.collaboration.executionplan.calculate;

import com.elit.outildedecision.collaboration.executionplan.dto.MethodModel;

import java.util.List;

/**
 * Result of an execution plan vote score computation.
 *
 * @param methodScores  the votes of each method, in method order
 * @param totalVotes    the total number of counted votes
 * @param chosenMethods the name(s) of the method(s) gathering the most votes
 */
public record ExecutionPlanVoteScore(List<MethodModel> methodScores, int totalVotes,
                                     List<String> chosenMethods) {
}
