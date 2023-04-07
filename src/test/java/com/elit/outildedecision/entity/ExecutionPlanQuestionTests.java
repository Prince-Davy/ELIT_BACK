package com.elit.outildedecision.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class ExecutionPlanQuestionTests {
    @Test
     void testToString() {
        ExecutionPlanQuestion epq = new ExecutionPlanQuestion(1L, "title", null, null);
        final String epqToString = "ExecutionPlanQuestion{" + "executionPlanQuestionId=" + 1 + '}';
        assertEquals(epq.toString(), epqToString);
    }
}
