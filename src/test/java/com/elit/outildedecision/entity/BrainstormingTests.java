package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

 class BrainstormingTests {
    @Test
     void testToString(){
        Brainstorming brainstorming = new Brainstorming();
        brainstorming.setBrainstormingId(1L);
        brainstorming.setBrainstormingText("brainstormingText");
        brainstorming.setIsChecked(true);
        String brainstormingString =
        "Brainstorming{"
        + "brainstormingId="
        + "1"
        + ", brainstormingText='"
        + "brainstormingText"
        + '\''
        + ", isChecked='"
        + "true"
        + '\''
        + '}';
        Assertions.assertEquals(brainstormingString, brainstorming.toString());
    }
}
