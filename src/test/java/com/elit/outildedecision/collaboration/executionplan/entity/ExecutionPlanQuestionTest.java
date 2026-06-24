package com.elit.outildedecision.collaboration.executionplan.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class ExecutionPlanQuestionTest {
    @Test
     void testToString() {
        ExecutionPlanQuestion epq = new ExecutionPlanQuestion(1L, "title", null, null);
        final String epqToString = "ExecutionPlanQuestion{" + "executionPlanQuestionId=" + 1 + '}';
        assertEquals(epq.toString(), epqToString);
    }
}
