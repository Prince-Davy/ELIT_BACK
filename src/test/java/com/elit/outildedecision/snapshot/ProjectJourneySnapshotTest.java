package com.elit.outildedecision.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.azure.communication.email.EmailClient;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectPropertyKey;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Snapshot journey for the project / admin endpoints (ENDPOINTS_BACKEND §3):
 * {@code /user/projects} (legacy list), {@code /user/projects/getdata/{id}},
 * {@code /user/projectlist/}, {@code /admin/projects} (list), {@code /admin/projects/{id}}.
 *
 * <p>The richer shape is captured through {@code getdata} which returns the controlled
 * {@code ProjectResponseModel} DTO; identity-generated ids and server timestamps are tolerated,
 * everything else STRICT. The legacy/admin list endpoints serialize raw {@code Project} entities
 * with lazy collections, so they are frozen on the deterministic empty-list contract (no project
 * owned by the caller / no pending project) plus a representative error payload.
 */
@Transactional
class ProjectJourneySnapshotTest extends SnapshotTestSupport {

  private static final String OWNER_EMAIL = "user@user.com";
  private static final String OWNER_PASSWORD = "Azerty1234!?";
  private static final String ADMIN_EMAIL = "admin@elite.fr";
  private static final String ADMIN_PASSWORD = "Azerty1234!?";

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private ProjectUserPermissionRepository permissionRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @PersistenceContext
  private EntityManager entityManager;

  @MockitoBean
  private EmailClient emailClient;

  private Long projectId;

  @BeforeEach
  void seed() {
    MailMocks.stubSuccessfulSend(emailClient);

    User owner = userRepository.save(User.builder()
        .username("user")
        .userFirstName("John")
        .userLastName("Doe")
        .userEmail(OWNER_EMAIL)
        .userPassword(passwordEncoder.encode(OWNER_PASSWORD))
        .isEmailVerified(true)
        .isUserActive(true)
        .isAdmin(false)
        .build());

    userRepository.save(User.builder()
        .username("admin")
        .userFirstName("Alice")
        .userLastName("Admin")
        .userEmail(ADMIN_EMAIL)
        .userPassword(passwordEncoder.encode(ADMIN_PASSWORD))
        .isEmailVerified(true)
        .isUserActive(true)
        .isAdmin(true)
        .build());

    Project project = projectRepository.save(Project.builder()
        .name("Project Pilot")
        .description("Deterministic seed for the project snapshot journey")
        .chefId(owner.getUserId())
        .confirmationState("PENDING")
        .isArchived(false)
        .build());
    projectId = project.getProjectId();

    permissionRepository.save(ProjectUserPermission.builder()
        .id(new ProjectPropertyKey(project.getProjectId(), owner.getUserId()))
        .permission("CDP")
        .userFunction("Chef de projet")
        .project(project)
        .user(owner)
        .build());

    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void getProjectDataShouldReturnProjectResponseModel() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/user/projects/getdata/{projectId}", projectId), token))
        .andReturn();

    assertSnapshot(result, "project/GET_user_projects_getdata.json",
        SnapshotComparators.tolerating(
            "projectId",
            "createdAt",
            "updatedAt",
            "chefId",
            "contributors[0].contributerId"));
  }

  @Test
  void getUserProjectsLegacyShouldReturnEmptyListForCaller() throws Exception {
    // The caller owns the seeded project through a permission; the legacy endpoint resolves
    // projects from the caller's permissions. After flush/clear the permission navigation is
    // reloaded, so a freshly-logged-in user with one project returns exactly that project.
    // To keep the raw-entity serialization deterministic we snapshot the admin (no permission)
    // whose project list is empty.
    String token = login(ADMIN_EMAIL, ADMIN_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/user/projects"), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertSnapshot(result, "project/GET_user_projects_empty.json");
  }

  @Test
  void getAdminProjectsShouldReturnSeededProjectsRawList() throws Exception {
    String token = login(ADMIN_EMAIL, ADMIN_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/admin/projects"), token))
        .andReturn();

    // /admin/projects serializes raw Project entities via findAll(). One project is seeded;
    // ids and server timestamps are tolerated, the field contract is otherwise STRICT.
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertSnapshot(result, "project/GET_admin_projects.json",
        SnapshotComparators.tolerating(
            "[0].projectId",
            "[0].chefId",
            "[0].createdAt",
            "[0].updatedAt"));
  }

  @Test
  void getProjectListShouldReturnPagedResult() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);
    String triModel = objectMapper.writeValueAsString(java.util.Map.of(
        "key", "",
        "status", "",
        "sort", "",
        "isActiveTab", false,
        "page", 0,
        "size", 10,
        "typeAnalyse", ""));

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .post("/user/projectlist/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(triModel), token))
        .andReturn();

    // The response wraps a Spring Data Page; freeze the envelope status here. The page payload
    // is non-deterministic across Spring versions, so the JSON contract is not snapshotted.
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString())
        .contains("projectsPage")
        .contains("totalProjetCreated");
  }

  @Test
  void getProjectDataForUnknownProjectShouldReturnNotFoundError() throws Exception {
    String token = login(ADMIN_EMAIL, ADMIN_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/user/projects/getdata/{projectId}", 999999L), token))
        .andReturn();

    // Representative global-handler error payload for this journey: ProjectNotFound → 404.
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
    assertSnapshot(result, "project/GET_user_projects_getdata_notFound.json");
  }
}
