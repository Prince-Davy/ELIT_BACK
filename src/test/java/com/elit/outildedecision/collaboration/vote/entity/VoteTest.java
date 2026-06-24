package com.elit.outildedecision.collaboration.vote.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
 class VoteTest {
    @Test
    void testToString(){
        Vote vote = new Vote();
        vote.setPredictibilityValue(1);
        final String voteString = "predictibilityValue:" + 1;
        assertEquals(voteString, vote.toString());
    }
}
