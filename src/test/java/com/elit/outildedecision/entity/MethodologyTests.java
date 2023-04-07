package com.elit.outildedecision.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class MethodologyTests {
    @Test
    void testToString(){
        Methodology methodology = new Methodology();
        methodology.setAdvantages("advantages");
        methodology.setDisadvantages("disadvantages");
        methodology.setDescription("description");
        methodology.setName("name");
        methodology.setMethodologyId(1L);
        final String MethoString = "Methodology{"
        + "methodologyId="
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
        assertEquals(methodology.toString(), MethoString);
    }

}
