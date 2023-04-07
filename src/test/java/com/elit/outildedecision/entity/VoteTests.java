package com.elit.outildedecision.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
 class VoteTests {
    @Test
    void testToString(){
        Vote vote = new Vote();
        vote.setPredictibilityValue(1);
        final String voteString = "predictibilityValue:" + 1;
        assertEquals(voteString, vote.toString());
    }
}
