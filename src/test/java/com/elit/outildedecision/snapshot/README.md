# Snapshot non-regression harness (migration plan §10)

This harness freezes the **observable JSON behaviour** of the contract endpoints
(`docs/ENDPOINTS_BACKEND.md`) so the journey-based migration can be carried out without
regressing the API. It boots the real application (`@SpringBootTest` + MockMvc with the
real security filter chain) on the `test` profile: **H2 in-memory, no network, no `.env`**.

## Layout

```
snapshot/
├── SnapshotTestSupport.java   base: login via POST /login, bearer calls, record/compare
├── SnapshotComparators.java   closed list of volatile-field customizations (§10.5)
├── MailMocks.java             deterministic Azure EmailClient stub (no network, no sleep)
├── OpenApiContractTest.java   freezes api/elit-openapi-contract.json (hard gate) + api/elit-openapi.json (doc)
├── AuthJourneySnapshotTest.java        ENDPOINTS_BACKEND §1 (register, login, refresh, reset)
├── UserJourneySnapshotTest.java        §2 (users, profile, avatar)
├── ProjectJourneySnapshotTest.java     §3 (projects + admin)
├── AttachmentJourneySnapshotTest.java  §4 (multipart + binary)
├── ReportJourneySnapshotTest.java      §5 (multipart + binary)
├── CollaborationJourneySnapshotTest.java §6 (survey tail + anomaly §4.2)
├── AiJourneySnapshotTest.java          §7 (answers, kiviat, feedbacks, methodology-evaluations — IA mocked)
├── AuditJourneySnapshotTest.java       §8 (pilot)
└── README.md

src/test/resources/snapshots/
└── <journey>/<METHOD>_<flattened-path>[_<case>].json
```

## Record vs. verify

- **Verify (default):** snapshots under `src/test/resources/snapshots/` are compared in
  STRICT mode. They are the contract.
- **Record:** `mvn test -Dsnapshots.record=true` (re)writes the snapshot files. Use it only
  for the initial capture or an **acted** contract change. Each captured file is **reviewed
  by hand** before commit. A snapshot is never edited just to make a test pass.

  > Windows PowerShell: quote the property — `.\mvnw.cmd test "-Dsnapshots.record=true"`.

## Volatile fields

Only the fields declared in `SnapshotComparators` are tolerated (JWTs, server-generated
timestamps, identity-generated keys created by the test). Adding one requires a written
justification in review. Everything else is compared strictly.

## Seeding

Each journey class seeds its own deterministic data **via JPA** inside a rolled-back
`@Transactional` test, so classes are independent of execution order. The legacy dev SQL
under `src/main/resources/liquibase/dev/sql` is **not reused**: it has drifted from the
current `ddl-auto` schema (e.g. `audit.sql` columns no longer match the `Audit` entity).

## Status (Étape 0) — done

- Build socle, `test` profile boots without `.env`/network, ArchUnit guard-rails, harness
  infrastructure, and **all eight journey classes** (auth, user, project, attachment, report,
  collaboration, IA, audit). 50 journey tests green; full `mvn clean verify` green at cold start
  (666 tests, checkstyle 0, PMD ok, JaCoCo ok).
- Mocks: mail (Azure `EmailClient`, via `MailMocks` — the polling loop resolves SUCCEEDED on the
  first poll, no `Thread.sleep`) and the AI prediction clients (`PredictRfApiClient`,
  `AiPredictionApiClientService`) are `@MockitoBean`s, so no test touches the network.
- Binary endpoints (avatar, attachments preview/download, report preview/download) are frozen by
  status + Content-Type + size, not a JSON snapshot (§10.6). Multipart uses `MockMultipartFile`.
- Each journey carries ≥ 1 global-handler error snapshot. The §4.2 anomaly
  (`GET /brainstorming/abstract/{prevId}`) is documented in `CollaborationJourneySnapshotTest`
  with a failing-status assertion so a later fix is a conscious contract change.
- Filesystem note: journey classes that write/move/delete real files clean the build-local upload
  dir in `@BeforeEach` (the DB rolls back per test, but on-disk files do not).

## OpenAPI contract freeze (Étape 0bis — active)

springdoc was migrated to `springdoc-openapi-starter-webmvc-ui:2.8.x`, which auto-configures
on Spring Boot 3.5. `/v3/api-docs` now serves the live document (Swagger UI at
`/api/swagger-ui.html`, JSON at `/api/v3/api-docs`), so `OpenApiContractTest` is **active**.
It freezes the surface in **two tiers**, because the front consumes the API *indirectly*
(hand-written Axios URLs + hand-written payload types, no code generation from the contract):

- **Hard gate — `api/elit-openapi-contract.json`.** A normalised *projection* of the live
  document reduced to what can actually break this front: `paths` (method+path+parameters,
  with **no schema names**, so a URL/verb/param change is perfectly isolated) and `schemas`
  (name -> compact per-property type tokens, e.g. `"createdAt": "string:date-time"`,
  `"contributors": "ContributorResponse[]"`, enums as `string{A,B,C}`, map values as
  `map<...>`, required fields under `[required]`). It drops `operationId`/tags/descriptions/
  examples entirely and sorts everything (`TreeMap`), so it is immune to springdoc's
  non-deterministic property ordering. A removed/renamed/retyped property or a path change
  **fails the build**; a schema rename still shows but as a few localised lines instead of
  being buried in 170 KB of churn.
- **Doc artifact — `api/elit-openapi.json`.** The full springdoc document for Swagger/Postman.
  Rewritten in record mode; in verify mode only checked to exist and parse, so its churn
  (incl. springdoc's non-deterministic ordering) **never fails CI on its own**.

The projection is the **reference for every later migration step's OpenAPI diff**. Regenerate
both files only on an acted contract change:
`mvn test -Dtest=OpenApiContractTest -Dsnapshots.record=true`.

**Token-cheap review procedure** (the projection exists so you do *not* read the 170 KB doc):
1. `git diff --stat api/` first.
2. Read only `api/elit-openapi-contract.json`'s diff — `paths` hunks (URL/verb/param) and
   `schemas` property-token hunks are the real signal.
3. The full `api/elit-openapi.json` diff is generated noise; do **not** read it in full.
