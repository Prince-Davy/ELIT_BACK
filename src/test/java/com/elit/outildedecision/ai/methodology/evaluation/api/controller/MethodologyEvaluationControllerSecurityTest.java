package com.elit.outildedecision.ai.methodology.evaluation.api.controller;

import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationService;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.ProjectPermession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MethodologyEvaluationControllerSecurityTest {

  private static final Long PROJECT_ID = 1L;

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MethodologyEvaluationService methodologyEvaluationService;

  @MockitoBean
  private UserServiceImpl userService;

  @BeforeEach
  void setup() {
    when(userService.findByEmail("owner")).thenReturn(userWithPermission(ProjectPermession.CDP.name()));
    when(userService.findByEmail("contributor")).thenReturn(userWithPermission(ProjectPermession.CONTRIBUTER.toString()));
    when(userService.findByEmail("outsider")).thenReturn(userWithoutProjectMembership());
  }

  @Test
  @WithMockUser(username = "owner")
  void getMethodologyEvaluations_shouldReturnOk_forOwner() throws Exception {
    when(methodologyEvaluationService.getMethodologyEvaluations(PROJECT_ID, 5)).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/projects/{projectId}/methodology-evaluations", PROJECT_ID))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "contributor")
  void getMethodologyEvaluations_shouldReturnOk_forContributer() throws Exception {
    when(methodologyEvaluationService.getMethodologyEvaluations(PROJECT_ID, 5)).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/projects/{projectId}/methodology-evaluations", PROJECT_ID))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "outsider")
  void getMethodologyEvaluations_shouldReturnForbidden_forUserOutsideProject() throws Exception {
    mockMvc.perform(get("/projects/{projectId}/methodology-evaluations", PROJECT_ID))
      .andExpect(status().isForbidden());

    verify(methodologyEvaluationService, never()).getMethodologyEvaluations(eq(PROJECT_ID), anyInt());
  }

  private User userWithPermission(final String permissionLabel) {
    User user = new User();
    user.setUserEmail("test@example.com");

    Project project = new Project();
    project.setProjectId(PROJECT_ID);

    ProjectUserPermission permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(permissionLabel);

    user.setUserProjectPermissions(List.of(permission));
    return user;
  }

  private User userWithoutProjectMembership() {
    User user = new User();
    user.setUserEmail("outsider@example.com");
    user.setUserProjectPermissions(Collections.emptyList());
    return user;
  }
}



