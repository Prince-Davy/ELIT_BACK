package com.elit.outildedecision.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class CriteriaTests {

    @Test
    void testToString(){
       String criterytostring = "Criteria{"
        + "criteriaId="
        + "1"
        + ", criteriaName='"
        + "criteriaTest"
        + '\''
        + ", criteriaValue="
        + 5
        + '}';
        Criteria criteria = new Criteria(1L, "criteriaTest", 5, 0, null);
        assertEquals(criteria.toString(), criterytostring);
    }
}
