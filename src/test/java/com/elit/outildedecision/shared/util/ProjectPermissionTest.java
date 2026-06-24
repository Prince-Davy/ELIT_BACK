package com.elit.outildedecision.shared.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class ProjectPermissionTest {
    @Test
    void testToString(){
        ProjectPermession pp = ProjectPermession.OBSERVER;
        final String ppString = "Observateur";
        assertEquals(ppString, pp.toString());
    }
}
