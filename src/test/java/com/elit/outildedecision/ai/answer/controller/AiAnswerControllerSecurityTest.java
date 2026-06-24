package com.elit.outildedecision.ai.answer.controller;

import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.shared.user.service.implementation.UserServiceImpl;
import com.elit.outildedecision.shared.util.ProjectPermession;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AiAnswerControllerSecurityTest {

  private static final Long PROJECT_ID = 1L;

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AiAnswerService aiAnswerService;

  @MockitoBean
  private ProjectService projectService;

  @MockitoBean
  private UserServiceImpl userService;

  @BeforeEach
  void setup() {
    when(userService.getProjectPermissions("owner", PROJECT_ID)).thenReturn(List.of(ProjectPermession.CDP.name()));
    when(userService.getProjectPermissions("contributor", PROJECT_ID)).thenReturn(List.of(ProjectPermession.CONTRIBUTER.toString()));
    when(userService.getProjectPermissions("outsider", PROJECT_ID)).thenReturn(Collections.emptyList());
  }

  @Test
  @WithMockUser(username = "owner")
  void getAnswers_shouldReturnOk_forOwner() throws Exception {
    when(aiAnswerService.getAnswersByProject(PROJECT_ID)).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/answers/{projectId}", PROJECT_ID))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "contributor")
  void getAnswers_shouldReturnOk_forContributer() throws Exception {
    when(aiAnswerService.getAnswersByProject(PROJECT_ID)).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/answers/{projectId}", PROJECT_ID))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "outsider")
  void getAnswers_shouldReturnForbidden_forUserOutsideProject() throws Exception {
    mockMvc.perform(get("/answers/{projectId}", PROJECT_ID))
      .andExpect(status().isForbidden());

    verify(aiAnswerService, never()).getAnswersByProject(any());
  }

  @Test
  @WithMockUser(username = "contributor")
  void saveAnswers_shouldStayForbidden_forContributer() throws Exception {
    mockMvc.perform(post("/answers/{projectId}", PROJECT_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"data\":{\"q1\":\"a1\"}}"))
      .andExpect(status().isForbidden());

    verify(aiAnswerService, never()).saveAnswers(eq(PROJECT_ID), any());
  }

}


