package com.elit.outildedecision.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class MethodologyTypesTests {
    @Test
    void testToString(){
        MethodologyTypes mt = MethodologyTypes.AGILE;
        final String mtString = "agile";
        assertEquals(mt.toString(), mtString);
    }
}
