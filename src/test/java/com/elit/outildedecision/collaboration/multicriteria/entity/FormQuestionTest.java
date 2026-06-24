package com.elit.outildedecision.collaboration.multicriteria.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

 class FormQuestionTest {

    @Test
     void testToString(){
        FormQuestion fq = new FormQuestion(null, 1L, "questionText", "answerText", null, false, null,null);
        final String fqToString = "FormQuestion{"
        + "questionId="
        + 1
        + ", questionText='"
        + "questionText"
        + '\''
        + ", answerText='"
        + "answerText"
        + '\''
        + '}';
        assertEquals(fq.toString(), fqToString);
    }

}
