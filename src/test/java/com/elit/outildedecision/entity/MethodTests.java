package com.elit.outildedecision.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class MethodTests {
    @Test
    void testToString(){
        Method method = new Method(1L, "name", "description", "disadvantages", "advantages", null);
        final String mtostring="Method{"
        + "id="
        + 1
        + ", name='"
        + "name"
        + '\''
        + ", description='"
        + "description"
        + '\''
        + ", disadvantages='"
        + "disadvantages"
        + '\''
        + ", advantages='"
        + "advantages"
        + '\''
        + '}';
        assertEquals(mtostring, method.toString());
    }
}
