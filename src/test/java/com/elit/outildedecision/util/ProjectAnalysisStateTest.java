package com.elit.outildedecision.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.elit.outildedecision.enumeration.ProjectAnalysisState;
import org.junit.jupiter.api.Test;

 class ProjectAnalysisStateTest {
    @Test
    void testToString(){
        ProjectAnalysisState pas = ProjectAnalysisState.DONE;
        final String pasString = "DONE";
        assertEquals(pasString, pas.toString());
    }
}
