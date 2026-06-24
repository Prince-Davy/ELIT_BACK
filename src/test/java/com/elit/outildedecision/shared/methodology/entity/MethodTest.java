package com.elit.outildedecision.shared.methodology.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class MethodTest {
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
