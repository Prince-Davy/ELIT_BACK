package com.elit.outildedecision.ai.answer.entity;

import com.elit.outildedecision.shared.kernel.entity.Project;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiAnswerTest {
  @Test
  void shouldCreateAiAnswerWithAllFields() {
    Project project = new Project();
    AiAnswer answer = AiAnswer.builder()
      .question("Q1")
      .category("Category")
      .answer("A1")
      .project(project)
      .build();


    assertThat(answer.getQuestion()).isEqualTo("Q1");
    assertThat(answer.getCategory()).isEqualTo("Category");
    assertThat(answer.getAnswer()).isEqualTo("A1");
    assertThat(answer.getProject()).isEqualTo(project);
  }

  @Test
  void shouldSetAndGetFields() {
    AiAnswer answer = new AiAnswer();
    Project project = new Project();

    answer.setQuestion("Q1");
    answer.setCategory("Category");
    answer.setAnswer("A1");
    answer.setProject(project);

    assertThat(answer.getQuestion()).isEqualTo("Q1");
    assertThat(answer.getCategory()).isEqualTo("Category");
    assertThat(answer.getAnswer()).isEqualTo("A1");
    assertThat(answer.getProject()).isEqualTo(project);
  }
}
