package com.elit.outildedecision.ai.answer.entity;

import com.elit.outildedecision.entity.Project;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AiAnswerTest {
  @Test
  public void shouldCreateAiAnswerWithAllFields() {
    Project project = new Project();
    AiAnswer answer = AiAnswer.builder()
      .question("Q1")
      .category("Category")
      .answer("A1")
      .project(project)
      .build();


    assertEquals("Q1", answer.getQuestion());
    assertEquals("Category", answer.getCategory());
    assertEquals("A1", answer.getAnswer());
    assertEquals(project, answer.getProject());
  }

  @Test
  public void shouldSetAndGetFields() {
    AiAnswer answer = new AiAnswer();
    Project project = new Project();

    answer.setQuestion("Q1");
    answer.setCategory("Category");
    answer.setAnswer("A1");
    answer.setProject(project);

    assertEquals("Q1", answer.getQuestion());
    assertEquals("Category", answer.getCategory());
    assertEquals("A1", answer.getAnswer());
    assertEquals(project, answer.getProject());
  }
}
