package com.elit.outildedecision.collaboration.executionplan.calculate;

import com.elit.outildedecision.collaboration.executionplan.dto.MethodModel;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanVote;
import com.elit.outildedecision.shared.methodology.entity.Method;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure, stateless calculator of the vote score of an execution plan. From the methods and votes
 * already loaded in memory it counts, in a single pass, the votes of each method, the total number
 * of counted votes and the method(s) gathering the most votes — without any repository access.
 */
@Component
public class ExecutionPlanScoreCalculator {

  /**
   * Computes the vote score of an execution plan: the votes of each method, the total number of
   * counted votes and the method(s) gathering the most votes.
   *
   * @param methods the scorable methods of the execution plan
   * @param votes   the votes cast on the execution plan
   * @return the per-method votes (in {@code methods} order), the total counted votes and the
   *         most-voted method names
   */
  public ExecutionPlanVoteScore computeVoteScore(final List<Method> methods, final List<ExecutionPlanVote> votes) {
    Map<String, Integer> votesByMethodName = countVotesByMethodName(votes);

    List<MethodModel> methodScores = new ArrayList<>();
    int totalVotes = 0;
    for (Method method : methods) {
      int methodVotes = votesByMethodName.getOrDefault(method.getName(), 0);
      methodScores.add(new MethodModel(method.getName(), methodVotes));
      totalVotes += methodVotes;
    }

    int maxVotes = methodScores.stream()
        .map(MethodModel::getVotes)
        .mapToInt(Integer::intValue)
        .max()
        .orElseThrow(() -> new RuntimeException("Pas de valeur max trouvée pour les votes."));

    List<String> chosenMethods = methodScores.stream()
        .filter(methodScore -> methodScore.getVotes() == maxVotes)
        .map(MethodModel::getName)
        .toList();

    return new ExecutionPlanVoteScore(methodScores, totalVotes, chosenMethods);
  }

  /**
   * Counts, in a single pass, the number of votes cast for each method name.
   *
   * @param votes the votes to count
   * @return the vote count indexed by method name
   */
  private Map<String, Integer> countVotesByMethodName(final List<ExecutionPlanVote> votes) {
    Map<String, Integer> votesByMethodName = new HashMap<>();
    for (ExecutionPlanVote vote : votes) {
      votesByMethodName.merge(vote.getMethod().getName(), 1, Integer::sum);
    }
    return votesByMethodName;
  }
}
