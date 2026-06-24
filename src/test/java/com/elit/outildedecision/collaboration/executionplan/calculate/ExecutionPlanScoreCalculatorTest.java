package com.elit.outildedecision.collaboration.executionplan.calculate;

import com.elit.outildedecision.collaboration.executionplan.dto.MethodModel;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanVote;
import com.elit.outildedecision.shared.methodology.entity.Method;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class ExecutionPlanScoreCalculatorTest {

  private final ExecutionPlanScoreCalculator calculator = new ExecutionPlanScoreCalculator();

  @Test
  void shouldCountVotesPerMethodAndSelectMostVotedIgnoringUnknownMethods() {
    // given
    List<Method> methods = List.of(method("Scrum"), method("Kanban"));
    List<ExecutionPlanVote> votes = List.of(
        voteFor("Scrum"), voteFor("Scrum"), voteFor("Kanban"), voteFor("Other"));

    // when
    ExecutionPlanVoteScore result = calculator.computeVoteScore(methods, votes);

    // then: votes are counted in method order, the "Other" vote is not counted in the total
    assertThat(result.methodScores())
        .extracting(MethodModel::getName, MethodModel::getVotes)
        .containsExactly(tuple("Scrum", 2), tuple("Kanban", 1));
    assertThat(result.totalVotes()).isEqualTo(3);
    assertThat(result.chosenMethods()).containsExactly("Scrum");
  }

  @Test
  void shouldReturnEveryTiedMethodWhenVotesAreEqual() {
    // given
    List<Method> methods = List.of(method("A"), method("B"));
    List<ExecutionPlanVote> votes = List.of(voteFor("A"), voteFor("B"));

    // when
    ExecutionPlanVoteScore result = calculator.computeVoteScore(methods, votes);

    // then
    assertThat(result.chosenMethods()).containsExactly("A", "B");
    assertThat(result.totalVotes()).isEqualTo(2);
  }

  @Test
  void shouldThrowWhenNoMethodToScore() {
    // when / then
    assertThatThrownBy(() -> calculator.computeVoteScore(List.of(), List.of()))
        .isInstanceOf(RuntimeException.class);
  }

  private Method method(final String name) {
    Method method = new Method();
    method.setName(name);
    return method;
  }

  private ExecutionPlanVote voteFor(final String methodName) {
    ExecutionPlanVote vote = new ExecutionPlanVote();
    vote.setMethod(method(methodName));
    return vote;
  }
}
