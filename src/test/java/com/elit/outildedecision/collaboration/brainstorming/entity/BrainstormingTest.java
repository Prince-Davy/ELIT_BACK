package com.elit.outildedecision.collaboration.brainstorming.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrainstormingTest {

  @Test
  void shouldRenderIdTextAndCheckedFlagInToString() {
    // given
    Brainstorming brainstorming = new Brainstorming();
    brainstorming.setBrainstormingId(1L);
    brainstorming.setBrainstormingText("brainstormingText");
    brainstorming.setIsChecked(true);

    // when
    String result = brainstorming.toString();

    // then
    assertThat(result)
        .isEqualTo("Brainstorming{brainstormingId=1, brainstormingText='brainstormingText', isChecked='true'}");
  }
}
