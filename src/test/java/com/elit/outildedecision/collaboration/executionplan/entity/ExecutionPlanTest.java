package com.elit.outildedecision.collaboration.executionplan.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

 class ExecutionPlanTest {

    @Test
     void testToString(){
        final Date date = Mockito.mock(Date.class);
        ExecutionPlan ep =
        new ExecutionPlan(1L,
        1,
        date,
        date,
        null, null,
        null);
        final String epString ="ExecutionPlan{"
        + "executionPlanId="
        + 1
        + ", createdAt="
        + date
        + ", updatedAt="
        + date
        + '}';

        assertEquals(ep.toString(), epString);
    }
}
