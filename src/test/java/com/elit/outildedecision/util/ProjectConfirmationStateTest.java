package com.elit.outildedecision.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class ProjectConfirmationStateTest {
    @Test
    void testToString(){
        ProjectConfirmationState pcs = ProjectConfirmationState.CONFIRMED;
        final String pcsString = "CONFIRMED";
        assertEquals(pcsString, pcs.toString());
    }
}
