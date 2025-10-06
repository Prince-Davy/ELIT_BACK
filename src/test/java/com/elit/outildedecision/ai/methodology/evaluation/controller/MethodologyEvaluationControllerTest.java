package com.elit.outildedecision.ai.methodology.evaluation.controller;

import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationService;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.ProjectPermession;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MethodologyEvaluationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MethodologyEvaluationService methodologyEvaluationService;

  @MockBean
  private UserServiceImpl userService;

  @BeforeEach
  void setup() {
    User mockUser = new User();
    mockUser.setUserEmail("testuser@example.com");

    Project project = Project.builder().projectId(1L).build();
    ProjectUserPermission permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setPermission(ProjectPermession.CDP.name());

    mockUser.setUserProjectPermissions(List.of(permission));

    when(userService.findByEmail("testuser")).thenReturn(mockUser);
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "testuser", roles = {"USER"})
  void shouldReturnEvaluations() {
    // Arrange
    MethodologyEvaluationDto evaluation = MethodologyEvaluationDto.builder()
      .methodology("A")
      .score(1.0)
      .build();

    List<MethodologyEvaluationDto> evaluations = List.of(evaluation);

    when(methodologyEvaluationService.getMethodologyEvaluations(1L, 5))
      .thenReturn(evaluations);

    // Act & Assert
    mockMvc.perform(get("/projects/1/methodology-evaluations")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].methodology").value("A"))
      .andExpect(jsonPath("$[0].score").value(1.0));
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "testuser", roles = {"USER"})
  void testUnsupportedModelTypeException() {
    // Arrange
    List<MethodologyEvaluationDto> evaluations = List.of();

    when(methodologyEvaluationService.getMethodologyEvaluations(1L, 5))
      .thenThrow(UnsupportedModelTypeException.class);

    // Act & Assert
    mockMvc.perform(get("/projects/1/methodology-evaluations")
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.length()").value(0));
  }
}
