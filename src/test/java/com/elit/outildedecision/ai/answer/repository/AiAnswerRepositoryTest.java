package com.elit.outildedecision.ai.answer.repository;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class AiAnswerRepositoryTest {

  @Autowired
  private AiAnswerRepository aiAnswerRepository;

  @Autowired
  private ProjectRepository projectRepository;

  private Project project;

  @BeforeEach
  void setUp() {
    project = new Project();
    project.setName("Test Project");
    project = projectRepository.save(project);

    AiAnswer answer1 = AiAnswer.builder()
      .question("Q1")
      .category("Category1")
      .answer("A1")
      .project(project)
      .build();

    AiAnswer answer2 = AiAnswer.builder()
      .question("Q2")
      .category("Category2")
      .answer("A2")
      .project(project)
      .build();

    aiAnswerRepository.save(answer1);
    aiAnswerRepository.save(answer2);
  }

  @Test
  public void testFindByProject_ProjectId() {
    List<AiAnswer> answers = aiAnswerRepository.findByProject_ProjectId(project.getProjectId());
    assertEquals(2, answers.size());
    assertTrue(answers.stream().anyMatch(a -> a.getQuestion().equals("Q1")));
    assertTrue(answers.stream().anyMatch(a -> a.getQuestion().equals("Q2")));
  }

  @Test
  public void testDeleteByProject_ProjectId() {
    aiAnswerRepository.deleteByProject_ProjectId(project.getProjectId());
    List<AiAnswer> answers = aiAnswerRepository.findByProject_ProjectId(project.getProjectId());
    assertTrue(answers.isEmpty());
  }
}
