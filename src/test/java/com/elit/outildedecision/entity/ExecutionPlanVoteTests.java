
package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutionPlanVoteTests {
  @Test
  void testToString(){
    User user = new User(null, 1L, "username", "userLastName", "userFirstName", "userEmail", "/", null, null, null, null, null, null);
    ExecutionPlanVote execPlanVote = new ExecutionPlanVote();
    execPlanVote.setUser(user);
    final String voteUserName = "username";
    assertEquals(voteUserName, execPlanVote.getUser().getUsername());
  }

}

