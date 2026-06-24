package com.elit.outildedecision.collaboration.executionplan.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class ExecutionPlanAnswerTest {

    @Test
     void testToString(){
        ExecutionPlan ep = new ExecutionPlan();
        ExecutionPlanQuestion epq= new ExecutionPlanQuestion();
        ExecutionPlanAnswer epa = new ExecutionPlanAnswer(1L, null, ep, epq);
        final String epaToString="executionPlanAnswerId="
        + 1
        + "executionPlanQuestion="
        + epq;
        assertEquals(epaToString, epa.toString());
    }
}
