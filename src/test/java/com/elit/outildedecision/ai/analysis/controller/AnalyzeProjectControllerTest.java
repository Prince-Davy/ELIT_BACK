package com.elit.outildedecision.ai.analysis.controller;

import com.elit.outildedecision.ai.analysis.service.AnalyzeProjectService;
import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.ProjectPermession;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnalyzeProjectControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AnalyzeProjectService analyzeProjectService;

  @MockitoBean
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
  void testAnalyzeProjectOK() {
    // Arrange
    AiAnswerDTO aiAnswerDTO = new AiAnswerDTO();
    long projectId = 1L;
    String analysisState = "OK";
    when(analyzeProjectService.analyzeProject(aiAnswerDTO, projectId))
      .thenReturn("OK");

    String jsonBody = new ObjectMapper().writeValueAsString(aiAnswerDTO);

    // Act & Assert
    mockMvc.perform(post("/projects/1/ai-analysis")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBody))
      .andExpectAll(
        status().isOk(),
        content().contentType("application/json")
      );
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "testuser", roles = {"USER"})
  void testAnalyzeProject_ResultAlreadyExistsException() {
    long projectId = 1L;
    AiAnswerDTO dto = new AiAnswerDTO();

    when(analyzeProjectService.analyzeProject(any(AiAnswerDTO.class), eq(projectId)))
      .thenThrow(new ResultAlreadyExistsException("Result already exists"));

    String jsonBody = new ObjectMapper().writeValueAsString(dto);

    mockMvc.perform(post("/projects/1/ai-analysis")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBody))
      .andExpectAll(
        status().isUnauthorized(),
        content().contentType(MediaType.APPLICATION_JSON),
        jsonPath("$.message").value("Result already exists"),
        jsonPath("$.status").value(401)
      );
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "testuser", roles = {"USER"})
  void testAnalyzeProject_ProjectNotFoundException() {
    long projectId = 1L;
    AiAnswerDTO dto = new AiAnswerDTO();

    when(analyzeProjectService.analyzeProject(any(AiAnswerDTO.class), eq(projectId)))
      .thenThrow(new ProjectNotFoundException("Project not found"));

    String jsonBody = new ObjectMapper().writeValueAsString(dto);

    mockMvc.perform(post("/projects/1/ai-analysis")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBody))
      .andExpectAll(
        status().isNotFound(),
        content().contentType(MediaType.APPLICATION_JSON),
        jsonPath("$.message").value("Project not found"),
        jsonPath("$.status").value(404)
      );
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "testuser", roles = {"USER"})
  void testAnalyzeProject_ResultAiNotFoundException() {
    long projectId = 1L;
    AiAnswerDTO dto = new AiAnswerDTO();

    when(analyzeProjectService.analyzeProject(any(AiAnswerDTO.class), eq(projectId)))
      .thenThrow(new ResultAiNotFoundException("AI result not found"));

    String jsonBody = new ObjectMapper().writeValueAsString(dto);

    mockMvc.perform(post("/projects/1/ai-analysis")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBody))
      .andExpectAll(
        status().isNotFound(),
        content().contentType(MediaType.APPLICATION_JSON),
        jsonPath("$.message").value("AI result not found"),
        jsonPath("$.status").value(404)
      );
  }
}
