package com.elit.outildedecision.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.JSONComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Base class of the non-regression snapshot harness (plan §10.4).
 *
 * <p>Boots the real application with the security filter chain so the snapshots capture
 * the observable JSON behaviour of each contract endpoint, including 403/error bodies from
 * the global handler. Tests run with the {@code test} profile (H2, no network, no
 * {@code .env}). Each journey class seeds its own deterministic data so classes stay
 * independent of execution order.
 *
 * <p>Mode: by default snapshots are compared in STRICT mode against the committed files
 * under {@code src/test/resources/snapshots/}. Running with {@code -Dsnapshots.record=true}
 * (re)writes them — for the initial capture or an acted contract change only; each captured
 * file is reviewed by hand before commit. A snapshot is never edited to make a test pass.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class SnapshotTestSupport {

  private static final Path SNAPSHOT_ROOT = Path.of("src", "test", "resources", "snapshots");
  private static final boolean RECORD = Boolean.getBoolean("snapshots.record");

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @BeforeEach
  void clearAuthentication() {
    org.springframework.security.core.context.SecurityContextHolder.clearContext();
  }

  /**
   * Authenticates against the real {@code POST /login} endpoint and returns the JWT access
   * token, so subsequent calls go through {@code JwtAuthorizationFilter} as the front does.
   *
   * @param email    the account email (seed account)
   * @param password the clear-text password
   * @return the signed access token
   * @throws Exception if the login call fails
   */
  protected String login(final String email, final String password) throws Exception {
    String payload = objectMapper.writeValueAsString(java.util.Map.of(
        "userEmail", email,
        "password", password));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andReturn();

    assertThat(result.getResponse().getStatus())
        .as("login for %s must succeed", email)
        .isEqualTo(200);

    JsonNode tokens = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    return tokens.get("access-token").asText();
  }

  /**
   * Adds the bearer token to a request builder.
   *
   * @param builder     the request builder
   * @param accessToken the JWT access token
   * @return the same builder, for chaining
   */
  protected MockHttpServletRequestBuilder withBearer(
      final MockHttpServletRequestBuilder builder, final String accessToken) {
    return builder.header("Authorization", "Bearer " + accessToken);
  }

  /**
   * Compares (or records) the JSON body of a performed request against the named snapshot
   * with STRICT semantics.
   *
   * @param result       the MockMvc result whose body is asserted
   * @param relativePath the snapshot path relative to the snapshot root, e.g.
   *                     {@code "audit/GET_audit_project_has-audit.json"}
   * @throws Exception if the snapshot cannot be read or written
   */
  protected void assertSnapshot(final MvcResult result, final String relativePath) throws Exception {
    assertSnapshot(result, relativePath, JSONCompareMode.STRICT);
  }

  /**
   * Compares (or records) the JSON body against the named snapshot using a custom
   * comparator (for the closed set of volatile fields — see {@link SnapshotComparators}).
   *
   * @param result       the MockMvc result whose body is asserted
   * @param relativePath the snapshot path relative to the snapshot root
   * @param comparator   the JSONAssert comparator to apply
   * @throws Exception if the snapshot cannot be read or written
   */
  protected void assertSnapshot(
      final MvcResult result, final String relativePath, final JSONComparator comparator)
      throws Exception {
    String actual = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    if (recordIfRequested(relativePath, actual)) {
      return;
    }
    String expected = readSnapshot(relativePath);
    JSONAssert.assertEquals(expected, actual, comparator);
  }

  private boolean isJsonStructure(final String body) {
    String trimmed = body.trim();
    return trimmed.startsWith("{") || trimmed.startsWith("[");
  }

  private void assertSnapshot(
      final MvcResult result, final String relativePath, final JSONCompareMode mode)
      throws Exception {
    String actual = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    if (recordIfRequested(relativePath, actual)) {
      return;
    }
    String expected = readSnapshot(relativePath);
    if (!isJsonStructure(expected)) {
      // Scalar body (e.g. a bare boolean from /has-audit): JSONAssert only parses
      // objects/arrays, so a scalar contract is frozen by exact string equality.
      assertThat(actual.trim()).isEqualTo(expected.trim());
      return;
    }
    JSONAssert.assertEquals(expected, actual, mode);
  }

  private boolean recordIfRequested(final String relativePath, final String actual) throws Exception {
    if (!RECORD) {
      return false;
    }
    Path target = SNAPSHOT_ROOT.resolve(relativePath);
    Files.createDirectories(target.getParent());
    String pretty = objectMapper.writerWithDefaultPrettyPrinter()
        .writeValueAsString(objectMapper.readTree(actual));
    Files.writeString(target, pretty, StandardCharsets.UTF_8);
    return true;
  }

  private String readSnapshot(final String relativePath) throws Exception {
    Path target = SNAPSHOT_ROOT.resolve(relativePath);
    assertThat(target)
        .as("missing snapshot %s — capture it with -Dsnapshots.record=true", relativePath)
        .exists();
    return Files.readString(target, StandardCharsets.UTF_8);
  }
}
