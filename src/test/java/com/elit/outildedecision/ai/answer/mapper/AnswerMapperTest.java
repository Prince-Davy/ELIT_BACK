package com.elit.outildedecision.ai.answer.mapper;

import com.elit.outildedecision.ai.answer.dto.AnswerResponse;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerMapperTest {

  private final AnswerMapper mapper = new AnswerMapperImpl();

  @Test
  void toResponse_convertitLesEntitesEnMap() {
    AiAnswer a1 = new AiAnswer();
    a1.setQuestion("Q1");
    a1.setAnswer("A1");

    AiAnswer a2 = new AiAnswer();
    a2.setQuestion("Q2");
    a2.setAnswer("A2");

    AnswerResponse response = mapper.toResponse(Arrays.asList(a1, a2));

    assertThat(response).isNotNull();
    assertThat(response.getData()).isNotNull();
    assertThat(response.getData()).hasSize(2);
    assertThat(response.getData().get("Q1")).isEqualTo("A1");
    assertThat(response.getData().get("Q2")).isEqualTo("A2");
  }

  @Test
  void toResponse_listeVide_retourneMapVide() {
    AnswerResponse response = mapper.toResponse(Collections.emptyList());

    assertThat(response).isNotNull();
    assertThat(response.getData()).isNotNull();
    assertThat(response.getData()).isEmpty();
  }

  @Test
  void toResponse_listeNull_retourneMapVide() {
    AnswerResponse response = mapper.toResponse(null);

    assertThat(response).isNotNull();
    assertThat(response.getData()).isNotNull();
    assertThat(response.getData()).isEmpty();
  }

  @Test
  void toResponse_clesDupliquees_derniereValeurConservee() {
    AiAnswer a1 = new AiAnswer();
    a1.setQuestion("Q1");
    a1.setAnswer("A1");

    AiAnswer a2 = new AiAnswer();
    a2.setQuestion("Q1");
    a2.setAnswer("A1bis");

    AnswerResponse response = mapper.toResponse(Arrays.asList(a1, a2));

    assertThat(response.getData()).hasSize(1);
    assertThat(response.getData().get("Q1")).isEqualTo("A1bis");
  }
}
