package com.elit.outildedecision.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.azure.communication.email.EmailClient;
import com.elit.outildedecision.shared.attachment.entity.Attachment;
import com.elit.outildedecision.shared.attachment.entity.AttachmentContent;
import com.elit.outildedecision.shared.attachment.repository.AttachmentContentRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectPropertyKey;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.attachment.repository.AttachmentRepository;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Snapshot journey for the attachment endpoints (ENDPOINTS_BACKEND §4):
 * {@code /user/attachments/upload/{projectId}}, {@code /download/{projectId}/{attachmentId}},
 * {@code /preview/{projectId}/{attachmentId}}, {@code /edit/{id}}, {@code DELETE /{id}/{projectId}}.
 *
 * <p>Binary endpoints (download, preview) are asserted by status + Content-Type + size > 0,
 * never a JSON snapshot (§10.6). A real file is written under {@code target/} so the binary
 * streams have deterministic content. Multipart upload uses a fixed {@link MockMultipartFile};
 * the plain-text upload/rename/delete responses are frozen by exact string equality. One error
 * payload (attachment not found → 404) is snapshotted for the global handler contract.
 */
@Transactional
class AttachmentJourneySnapshotTest extends SnapshotTestSupport {

  private static final String OWNER_EMAIL = "user@user.com";
  private static final String OWNER_PASSWORD = "Azerty1234!?";
  private static final Path UPLOAD_DIR = Path.of("target", "test-uploads", "files");

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private ProjectUserPermissionRepository permissionRepository;
  @Autowired
  private AttachmentRepository attachmentRepository;
  @Autowired
  private AttachmentContentRepository attachmentContentRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @PersistenceContext
  private EntityManager entityManager;

  @MockitoBean
  private EmailClient emailClient;

  private Long projectId;
  private Long attachmentId;

  @BeforeEach
  void seed() throws Exception {
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

    Project project = projectRepository.save(Project.builder()
        .name("Attachment Pilot")
        .description("Deterministic seed for the attachment snapshot journey")
        .chefId(owner.getUserId())
        .folderPath("")
        .build());
    projectId = project.getProjectId();

    permissionRepository.save(ProjectUserPermission.builder()
        .id(new ProjectPropertyKey(project.getProjectId(), owner.getUserId()))
        .permission("CDP")
        .userFunction("Chef de projet")
        .project(project)
        .user(owner)
        .build());

    // The logical path is kept for compatibility; the bytes live in attachment_content.
    Path stored = UPLOAD_DIR.resolve("contract.txt");

    Attachment attachment = attachmentRepository.save(Attachment.builder()
        .fileName("contract.txt")
        .filePath(stored.toString())
        .contentType("text/plain")
        .project(project)
        .creator(owner)
        .build());
    attachmentId = attachment.getAttachmentId();
    attachmentContentRepository.save(
        new AttachmentContent(attachmentId, "fixed-attachment-content".getBytes(StandardCharsets.UTF_8)));

    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void downloadAttachmentShouldStreamBinaryContent() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/user/attachments/download/{projectId}/{attachmentId}", projectId, attachmentId), token))
        .andReturn();

    // Binary contract (§10.6): status + Content-Type + non-empty body, no JSON snapshot.
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentType()).isEqualTo("text/plain");
    assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty();
    assertThat(result.getResponse().getHeader("Content-Disposition")).contains("attachment");
  }

  @Test
  void previewAttachmentShouldStreamInlineBinaryContent() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/user/attachments/preview/{projectId}/{attachmentId}", projectId, attachmentId), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentType()).isEqualTo("text/plain");
    assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty();
    assertThat(result.getResponse().getHeader("Content-Disposition")).contains("inline");
  }

  @Test
  void uploadAttachmentShouldStoreFileAndReturnSuccessMessage() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);
    MockMultipartFile file = new MockMultipartFile(
        "files", "upload.txt", MediaType.TEXT_PLAIN_VALUE,
        "fixed-upload-content".getBytes(StandardCharsets.UTF_8));

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .multipart("/user/attachments/upload/{projectId}", projectId)
            .file(file), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Fichiers enregistrés avec succés");
  }

  @Test
  void renameAttachmentShouldReturnSuccessMessage() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);
    String body = "{\"newAttachmentName\":\"renamed.txt\"}";

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .put("/user/attachments/edit/{attachmentId}", attachmentId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Votre fichier a été mis à jour");
  }

  @Test
  void deleteAttachmentShouldReturnSuccessMessage() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .delete("/user/attachments/{attachmentId}/{projectId}", attachmentId, projectId), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Fichier supprimé avec succès.");
  }

  @Test
  void downloadUnknownAttachmentShouldReturnNotFoundError() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/user/attachments/download/{projectId}/{attachmentId}", projectId, 999999L), token))
        .andReturn();

    // Representative global-handler error payload for this journey: AttachmentNotFound → 404.
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
    assertSnapshot(result, "attachment/GET_attachments_download_notFound.json");
  }
}
