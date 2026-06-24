package com.elit.outildedecision.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Freezes the OpenAPI surface in two tiers, because the ELIT front consumes the API
 * <em>indirectly</em> (hand-written Axios URLs + hand-written payload types, no code generation
 * from the contract). Only two things can break that front: a path/verb/parameter change, and the
 * runtime <em>shape</em> of a field it reads. Schema names, {@code operationId}s, tags and
 * descriptions cannot, yet they dominate the diff during the migration's renamings.
 *
 * <ul>
 *   <li><b>Hard gate</b> ({@link #liveApiContractShouldMatchCommittedReference}): a normalised
 *       <em>contract projection</em> ({@code api/elit-openapi-contract.json}) reduced to
 *       {@code paths} (method+path+parameters, no schema names, so URL/verb changes are perfectly
 *       isolated) and {@code schemas} (name -> compact per-property type tokens). It drops
 *       {@code operationId}/tags/descriptions/examples entirely. A removed, renamed or retyped
 *       property, or a path/verb/param change, fails the build; a schema rename still shows but as a
 *       few localised lines instead of being buried in the full document.</li>
 *   <li><b>Doc artifact</b> ({@link #liveOpenApiDocumentShouldRemainAvailable}): the full springdoc
 *       document ({@code api/elit-openapi.json}) for Swagger/Postman. Rewritten in record mode; in
 *       verify mode only checked to exist and parse, so its churn no longer fails CI.</li>
 * </ul>
 *
 * <p>Reference regeneration (after an acted change): run the suite with
 * {@code -Dsnapshots.record=true}; both files are rewritten and reviewed by hand before commit.
 * Review the small {@code -contract.json} diff first; the full document is generated noise.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiContractTest {

  private static final Path DOCUMENT = Path.of("api", "elit-openapi.json");
  private static final Path CONTRACT = Path.of("api", "elit-openapi-contract.json");
  private static final boolean RECORD = Boolean.getBoolean("snapshots.record");
  private static final Set<String> HTTP_METHODS =
      Set.of("get", "put", "post", "delete", "patch", "options", "head", "trace");

  private final ObjectMapper objectMapper = new ObjectMapper()
      .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
      .enable(SerializationFeature.INDENT_OUTPUT);

  @Autowired
  private MockMvc mockMvc;

  /**
   * The guard-rail every later migration step relies on. Compares the front-relevant projection of
   * the live document against the committed reference, dropping rename-only noise to a few lines.
   */
  @Test
  void liveApiContractShouldMatchCommittedReference() throws Exception {
    JsonNode live = fetchLiveDocument();
    String projection = objectMapper.writeValueAsString(buildContract(live));

    if (RECORD) {
      Files.writeString(CONTRACT, projection, StandardCharsets.UTF_8);
      return;
    }

    assertThat(CONTRACT)
        .as("OpenAPI contract projection must be committed (capture with -Dsnapshots.record=true)")
        .exists();

    String reference = Files.readString(CONTRACT, StandardCharsets.UTF_8);
    JSONAssert.assertEquals(reference, projection, JSONCompareMode.STRICT);
  }

  /**
   * Keeps the full springdoc document available as a generated artifact (Swagger/Postman). Not a
   * strict gate: schema renames refresh it on record but never fail the build on their own.
   */
  @Test
  void liveOpenApiDocumentShouldRemainAvailable() throws Exception {
    JsonNode live = fetchLiveDocument();

    if (RECORD) {
      Files.writeString(DOCUMENT, objectMapper.writeValueAsString(live), StandardCharsets.UTF_8);
      return;
    }

    assertThat(DOCUMENT)
        .as("OpenAPI document must be committed (capture with -Dsnapshots.record=true)")
        .exists();
    objectMapper.readTree(Files.readString(DOCUMENT, StandardCharsets.UTF_8));
  }

  private JsonNode fetchLiveDocument() throws Exception {
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs")).andReturn();

    assertThat(result.getResponse().getStatus())
        .as("springdoc must serve the OpenAPI document at /v3/api-docs")
        .isEqualTo(200);

    return objectMapper.readTree(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /** Projects the document down to {@code paths} (method+path+params) and {@code schemas}. */
  private Map<String, Object> buildContract(JsonNode root) {
    Map<String, Object> paths = new TreeMap<>();
    root.path("paths").fields().forEachRemaining(pathEntry -> {
      String path = pathEntry.getKey();
      pathEntry.getValue().fields().forEachRemaining(opEntry -> {
        if (HTTP_METHODS.contains(opEntry.getKey())) {
          paths.put(opEntry.getKey().toUpperCase(Locale.ROOT) + " " + path,
              buildOperation(opEntry.getValue()));
        }
      });
    });

    Map<String, Object> schemas = new TreeMap<>();
    root.path("components").path("schemas").fields().forEachRemaining(entry ->
        schemas.put(entry.getKey(), buildSchema(entry.getValue())));

    Map<String, Object> contract = new TreeMap<>();
    contract.put("paths", paths);
    contract.put("schemas", schemas);
    return contract;
  }

  private Map<String, Object> buildOperation(JsonNode op) {
    Map<String, Object> result = new TreeMap<>();
    result.put("parameters", buildParameters(op.path("parameters")));
    result.put("request", op.has("requestBody")
        ? mediaTokens(op.path("requestBody").path("content")) : "<none>");
    Map<String, Object> responses = new TreeMap<>();
    op.path("responses").fields().forEachRemaining(entry -> {
      JsonNode content = entry.getValue().path("content");
      responses.put(entry.getKey(),
          content.isMissingNode() ? "<no-content>" : mediaTokens(content));
    });
    result.put("responses", responses);
    return result;
  }

  private List<Map<String, Object>> buildParameters(JsonNode parameters) {
    List<Map<String, Object>> result = new ArrayList<>();
    if (parameters.isArray()) {
      for (JsonNode parameter : parameters) {
        Map<String, Object> entry = new TreeMap<>();
        entry.put("in", parameter.path("in").asText());
        entry.put("name", parameter.path("name").asText());
        entry.put("required", parameter.path("required").asBoolean(false));
        entry.put("type", typeToken(parameter.path("schema")));
        result.add(entry);
      }
    }
    result.sort(Comparator.comparing((Map<String, Object> entry) -> (String) entry.get("in"))
        .thenComparing(entry -> (String) entry.get("name")));
    return result;
  }

  private Map<String, Object> mediaTokens(JsonNode content) {
    Map<String, Object> result = new TreeMap<>();
    content.fields().forEachRemaining(entry ->
        result.put(entry.getKey(), typeToken(entry.getValue().path("schema"))));
    return result;
  }

  /** A named schema: object -> {property -> token}; anything else -> its own token. */
  private Object buildSchema(JsonNode schema) {
    if (!("object".equals(schema.path("type").asText(null)) || schema.has("properties"))) {
      return typeToken(schema);
    }
    Map<String, Object> result = new TreeMap<>();
    if (schema.has("properties")) {
      schema.get("properties").fields().forEachRemaining(entry ->
          result.put(entry.getKey(), typeToken(entry.getValue())));
    }
    if (schema.path("additionalProperties").isObject()) {
      result.put("[*]", typeToken(schema.get("additionalProperties")));
    }
    if (schema.has("required")) {
      List<String> required = new ArrayList<>();
      schema.get("required").forEach(node -> required.add(node.asText()));
      required.sort(Comparator.naturalOrder());
      result.put("[required]", required);
    }
    return result;
  }

  /** Compact one-string type token; {@code $ref} stays a schema name (no following, no inlining). */
  private String typeToken(JsonNode schema) {
    if (schema == null || schema.isMissingNode() || schema.isNull()) {
      return "<none>";
    }
    if (schema.has("$ref")) {
      String ref = schema.get("$ref").asText();
      return ref.substring(ref.lastIndexOf('/') + 1);
    }
    String type = schema.path("type").asText(null);
    if ("array".equals(type)) {
      return typeToken(schema.path("items")) + "[]";
    }
    if ("object".equals(type) || schema.has("properties")) {
      return schema.path("additionalProperties").isObject()
          ? "map<" + typeToken(schema.get("additionalProperties")) + ">" : "object";
    }
    StringBuilder token = new StringBuilder(type == null ? "any" : type);
    if (schema.has("format")) {
      token.append(':').append(schema.get("format").asText());
    }
    if (schema.has("enum")) {
      List<String> values = new ArrayList<>();
      schema.get("enum").forEach(node -> values.add(node.asText()));
      values.sort(Comparator.naturalOrder());
      StringJoiner joiner = new StringJoiner(",", "{", "}");
      values.forEach(joiner::add);
      token.append(joiner);
    }
    return token.toString();
  }
}
