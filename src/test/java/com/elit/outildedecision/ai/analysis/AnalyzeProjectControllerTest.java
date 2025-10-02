package com.elit.outildedecision.ai.analysis;

import com.elit.outildedecision.ai.analysis.service.AnalyzeProjectServiceImpl;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.ProjectPermession;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class AnalyzeProjectControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AnalyzeProjectServiceImpl analyzeProjectServiceImpl;

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


}
