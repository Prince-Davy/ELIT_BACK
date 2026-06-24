package com.elit.outildedecision.collaboration.multicriteria.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class FormStepTest {
    @Test
     void testToString(){
        FormStep fs = new FormStep();
        fs.setStepId(1L);
        fs.setStepName("stepName");
        final String fString ="FormStep{" + "stepId=" + 1 + ", stepName='" + "stepName" + '\'' + '}';
        assertEquals(fString, fs.toString());
    }
}
