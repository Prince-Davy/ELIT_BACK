package com.elit.outildedecision.snapshot;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.repository.AuditRepository;
import com.elit.outildedecision.audit.repository.QuestionRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectPropertyKey;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;

import com.elit.outildedecision.audit.entity.ScoringType;
import com.elit.outildedecision.shared.util.MethodType;
import com.elit.outildedecision.shared.util.QuestionCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pilot journey of the snapshot harness (plan §10.8 step 3): the audit parcours, chosen
 * because it is the simplest end-to-end. Validates that the harness can boot the real
 * application, seed deterministic data via JPA (the legacy dev SQL has drifted from the
 * current schema and is unusable as-is), authenticate through {@code POST /login}, and
 * freeze the JSON contract of the audit endpoints (ENDPOINTS_BACKEND §8).
 *
 * <p>Seeding is transactional and rolled back per test, so the class is independent of
 * execution order. The audit parcours touches neither the AI client nor mail, so no
 * external collaborator needs mocking here.
 */
@Transactional
class AuditJourneySnapshotTest extends SnapshotTestSupport {

  private static final String OWNER_EMAIL = "user@user.com";
  private static final String OWNER_PASSWORD = "Azerty1234!?";

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private ProjectUserPermissionRepository permissionRepository;
  @Autowired
  private AuditRepository auditRepository;
  @Autowired
  private QuestionRepository questionRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @PersistenceContext
  private EntityManager entityManager;

  private Long projectId;
  private Long auditId;

  @BeforeEach
  void seed() {
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

    Project project = projectRepository.save(Project.builder()
        .name("Audit Pilot")
        .description("Deterministic seed for the audit snapshot pilot")
        .chefId(owner.getUserId())
        .build());
    projectId = project.getProjectId();

    permissionRepository.save(ProjectUserPermission.builder()
        .id(new ProjectPropertyKey(project.getProjectId(), owner.getUserId()))
        .permission("CDP")
        .userFunction("Chef de projet")
        .project(project)
        .user(owner)
        .build());

    Audit audit = Audit.builder()
        .method(MethodType.SCRUM)
        .project(project)
        .user(owner)
        .build();
    audit.getQuestions().add(Question.builder()
        .questionRef("Q1")
        .category(QuestionCategory.ORGANISATIONS)
        .weight(3)
        .auditAnswerLabel("Often")
        .auditAnswerValue(2)
        .scoringType(ScoringType.DIRECT)
        .audit(audit)
        .build());
    auditId = auditRepository.save(audit).getAuditId();

    // Flush + clear so the @PreAuthorize navigation (user → permissions → project →
    // audits) reloads from the DB and sees the seeded audit, as it would in production.
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void hasAuditForProjectShouldReturnTrue() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/audit/project/{projectId}/has-audit", projectId), token))
        .andReturn();

    assertSnapshot(result, "audit/GET_audit_project_has-audit.json");
  }

  @Test
  void getAuditsByProjectShouldReturnSeededAudit() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/audit/project/{projectId}", projectId), token))
        .andReturn();

    // Volatile fields: identity-generated keys (H2 IDENTITY counters do not reset on
    // rollback) and the server @CreationTimestamp. Everything else is compared STRICT.
    assertSnapshot(result, "audit/GET_audit_project.json",
        SnapshotComparators.tolerating(
            "[0].auditId",
            "[0].projectId",
            "[0].userId",
            "[0].auditLaunchDate",
            "[0].questions[0].id"));
  }

  @Test
  void getAuditScoreShouldReturn404WhenNotCalculated() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/audit/{auditId}/auditScore", auditId), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(404);
  }

  @Test
  void getAuditsByUnknownProjectShouldExposeGlobalHandlerError() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/audit/project/{projectId}", 999999L), token))
        .andReturn();

    // Error contract of the global handler for a missing project (frozen by status here;
    // the body is captured as a snapshot once a representative error payload is confirmed).
    assertThat(result.getResponse().getStatus())
        .isIn(403, 404, 406);
  }
}
