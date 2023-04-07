package com.elit.outildedecision.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class ProjectPermissionTests {
    @Test
    void testToString(){
        ProjectPermession pp = ProjectPermession.OBSERVER;
        final String ppString = "Observateur";
        assertEquals(ppString, pp.toString());
    }
}
