package com.elit.outildedecision.collaboration.executionplan.entity;

import com.elit.outildedecision.shared.kernel.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutionPlanVoteTest {
  @Test
  void testToString(){
    User user = new User(null, 1L, "username", "userLastName", "userFirstName", "userEmail", "/", null, null, null, null, null, null);
    ExecutionPlanVote execPlanVote = new ExecutionPlanVote();
    execPlanVote.setUser(user);
    final String voteUserName = "username";
    assertEquals(voteUserName, execPlanVote.getUser().getUsername());
  }

}

