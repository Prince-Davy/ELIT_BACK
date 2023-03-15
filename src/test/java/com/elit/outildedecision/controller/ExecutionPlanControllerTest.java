package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ExecutionPlanControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  ProjectRepository  projectRepository;

  @Test
  void createExecutionPlan() throws Exception {
  /*  Long projectId = 1L;
    Project project = new Project();
    project.setProjectId(projectId);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    ResultActions response = mockMvc.perform(post("/api/executionPlan/create/"+ projectId));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());*/
  }

  @Test
  void getQuestionsAndAnswers() {
  }

  @Test
  void updateAnswers() {
  }

  @Test
  void getMethodologies() {
  }

  @Test
  void postMethodologiesVote() {
  }

  @Test
  void getExecutionPlanScore() {
  }

  @Test
  void putIsFinishedExecutionPlan() {
  }
}
