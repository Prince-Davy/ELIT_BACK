# ELIT Backend — Parcours Collaboratif

> Document fonctionnel + technique du **parcours collaboratif** (`com.elit.outildedecision.collaboration`).
> Objectif : comprendre l'**enchaînement des étapes**, les **dépendances d'ordre**, les **rôles**,
> les **cas d'erreur** et les **incohérences connues**. Les diagrammes sont en
> [Mermaid](https://mermaid.js.org/) (rendu natif GitHub / aperçu VSCode ; IntelliJ → plugin **Mermaid**).
>
> Périmètre : ce document décrit **uniquement le parcours collaboratif** tel qu'il est réellement
> traité aujourd'hui (validé de bout en bout par la collection Postman *ELIT - Parcours Collaboratif*).
> Les parcours **IA** (`ai/`) et **audit** (`audit/`) ne sont pas couverts ici.
>
> Pour le contrat d'API complet, voir [ENDPOINTS_BACKEND.md](ENDPOINTS_BACKEND.md).
> Pour les règles de calcul, voir [REGLES_CALCUL_METIER.md](REGLES_CALCUL_METIER.md).

---

## 1. Vue d'ensemble

Le parcours collaboratif accompagne une équipe projet dans le **choix d'une méthodologie de gestion
de projet** (Scrum, Kanban, XP, …) puis dans la **construction d'un plan d'exécution**. Il se déroule
en 8 sous-parcours **ordonnés** :

| # | Sous-parcours | Feature | Rôle déclencheur |
|---|---|---|---|
| 0 | Auth & cadrage projet | `shared/auth`, `shared/project` | CDP (owner) |
| 1 | Analyse multicritère | `multicriteria` | Contributeur |
| 2 | Analyse de prévisibilité | `predictibility` | CDP + Contributeur |
| 3 | Vote (prévisibilité) | `vote` | Contributeur |
| 4 | Brainstorming | `brainstorming` | Contributeur |
| 5 | Plan d'exécution | `executionplan` | CDP + Contributeur |
| 6 | Questionnaire (survey) | `survey` | Tout utilisateur |
| 7 | Rapports | `report` | CDP |

**Rôles** (libellés FR exigés par le validateur et la sécurité) :
- **CDP / owner** : créateur du projet (`isProjectOwner`).
- **Contributeur** : membre invité avec droits d'écriture (`isContributer`).
- **Observateur** : lecture seule (`isObserver`).

---

## 2. Flux nominal de bout en bout

> ⚠️ L'enchaînement n'est **pas** purement libre : plusieurs étapes ne se débloquent que si la
> précédente a été **finalisée**. Ces dépendances sont implicites (aucune n'est documentée dans le
> contrat) — voir §4.

```mermaid
flowchart TD
    subgraph S0["0 — Auth & cadrage"]
        A0["POST /login (owner, contrib, observer)"] --> A1["GET /user/users<br/>(capture contribId, observerId)"]
        A1 --> A2["POST /user/projects<br/>(capture projectId, chefId)"]
        A2 --> A3["POST /user/projects/{id}/add-contributor<br/>(Contributeur)"]
        A3 --> A4["add-contributor (Observateur)"]
    end

    subgraph S1["1 — Multicritère"]
        B0["GET /multi-criteria-analysis/{projectId}<br/>(capture mcaId, questionId)"]
        B0 -. "remplissage optionnel" .-> B1["PUT /multi-criteria-analysis/{projectId}<br/>(answerText + criteriaValue ≠ 0)"]
        B1 --> B2["GET /multi-criteria-analysis/{projectId}/score<br/>(lecture seule, idempotent)"]
        B0 --> B2
        B2 --> B3["PUT /{projectId}/multi-criteria-analysis/isFinished/{mcaId}<br/>→ isFinished = true"]
    end

    subgraph S2["2 — Prévisibilité"]
        C0["POST /predictibility-analysis/{projectId}<br/>→ predId (409 si MC non finalisée)"]
        C0 --> C1["GET /{projectId}/predictibility/{predId}/score<br/>→ tieBreakRequired (départage requis ?)"]
        C1 --> C2["POST .../chosenMethodology<br/>body { chosenMethodology: 'SCRUM' }"]
        C2 --> C3["PUT .../predictibility/isFinished/{predId}"]
    end

    subgraph S3["3 — Vote"]
        D0["POST /{projectId}/vote (VoteRequest)"] --> D1["GET /vote/{predId}"] --> D2["PUT /vote/{projectId}/{voteId}"]
    end

    subgraph S4["4 — Brainstorming"]
        E0["POST /brainstorming/{projectId}<br/>→ brainstormingId (1 / user / analyse)"] --> E1["GET /brainstorming/{projectId}/{predId}"]
        E1 --> E2["PUT /brainstorming/{projectId} (update)"]
        E2 --> E3["PUT /brainstorming/validate/{projectId}/{brainstormingId}"]
        E3 --> E4["DELETE /brainstorming/{projectId}/{brainstormingId}"]
    end

    subgraph S5["5 — Plan d'exécution"]
        F0["POST /executionPlan/create/{projectId}<br/>→ execPlanId (409 si prévisibilité non finalisée)"]
        F0 --> F1["GET .../formConstraints/{epId}<br/>(capture executionQuestionId[])"]
        F1 --> F2["PUT .../formConstraints/putAnswers/{epId}"]
        F2 --> F3["GET .../methodologies/{epId}<br/>(capture methodId)"]
        F3 --> F4["POST .../methodologies/vote<br/>body { methodId, executionPlanId }"]
        F4 --> F5["GET .../executionPlan/{epId}/score"]
        F5 --> F6["POST .../executionPlan/isFinished/{epId}"]
    end

    subgraph S6["6 — Survey"]
        G0["GET /survey/question (capture questionId)"] --> G1["POST /survey/answer"] --> G2["GET /survey/answer/{userId}"]
    end

    subgraph S7["7 — Rapports"]
        H0["POST /report/upload/{projectId}/{analyseId}/{type}/{iteration}"] --> H1["GET /report/{projectId} (capture reportId)"]
        H1 --> H2["GET /report/preview/{reportId}"] --> H3["GET /report/download/{reportId}"] --> H4["DELETE /report/{reportId}"]
    end

    S0 --> S1 --> S2 --> S3 --> S4 --> S5 --> S6 --> S7
```

---

## 3. Rôles & sécurité par endpoint

```mermaid
flowchart LR
    classDef owner fill:#fde,stroke:#a36,color:#111;
    classDef contrib fill:#def,stroke:#36a,color:#111;
    classDef obs fill:#efe,stroke:#3a6,color:#111;
    classDef any fill:#eee,stroke:#888,color:#111;

    O["CDP / owner"]:::owner
    C["Contributeur"]:::contrib
    OB["Observateur"]:::obs

    O --> oCreate["créer projet<br/>chosenMethodology · isFinished · iteration<br/>putAnswers · validate brainstorming"]:::owner
    C --> cWrite["créer/initialiser la prévisibilité<br/>remplir multicritère · voter<br/>créer/maj brainstorming<br/>voter méthodologie"]:::contrib
    OB --> obRead["lecture des scores<br/>(predictibility · brainstorming · execPlan)"]:::obs

    obRead -. "interdit en écriture<br/>(403)" .-> oCreate
```

Points notables :
- `predictibility/score`, `executionPlan/score`, `brainstorming GET` : ouverts à **ADMIN ∨ Contributeur ∨ Observateur**.
- `chosenMethodology`, `isFinished`, `putAnswers`, `validate brainstorming` : **owner uniquement**.
- **Création de prévisibilité** (`POST /predictibility-analysis/{projectId}`), vote (prévisibilité
  **et** méthodologie), remplissage multicritère, création brainstorming : **Contributeur** (owner
  inclus). Le vote méthodologie est **parallèle** : un contributeur initialise l'analyse de
  prévisibilité lui-même et vote sans attendre l'owner — l'appel est idempotent (renvoie l'analyse
  existante).

---

## 4. Dépendances d'ordre

> Le parcours a des **dépendances d'ordre** : certaines étapes ne se débloquent que si la précédente
> a été **finalisée**. Les préconditions non remplies renvoient des codes explicites (409).

```mermaid
flowchart TD
    start(["Projet créé"]) --> mc["Multicritère : (remplir) → GET score (idempotent)<br/>→ PUT isFinished"]
    mc --> q1{"Multicritère finalisée ?"}
    q1 -->|Non| x1["POST predictibility → 409 Conflict"]:::err
    q1 -->|Oui| pred["Prévisibilité créée → chosenMethodology → isFinished"]
    pred --> q2{"Prévisibilité finalisée ?"}
    q2 -->|Non| x2["POST executionPlan/create → 409 Conflict"]:::err
    q2 -->|Oui| ep["Plan d'exécution créé"]
    classDef err fill:#fdd,stroke:#c33,color:#111;
```

> Aucune limitation/incohérence connue ouverte à l'instant T sur ce parcours.

### Départage de méthodologie (`tieBreakRequired`)

Le score de prévisibilité auto-sélectionne la méthodologie gagnante (cumul de votes le plus élevé,
départage interne par ordre de déclaration de l'enum). Quand **deux méthodologies sont à égalité au
sommet** et que l'owner n'a pas encore tranché (`chosenMethodology == null`), `GET .../score` renvoie
`tieBreakRequired = true`. Le front s'appuie sur ce seul booléen pour **griser le bouton « Compléter
le formulaire » de l'étape 3** tant que l'égalité n'est pas résolue. Après `POST .../chosenMethodology`
(owner), un nouveau `GET .../score` renvoie `tieBreakRequired = false`. Sans vote confirmé, le flag
reste `false` (l'égalité n'a de sens que sur des votes réels).

Quand l'owner tranche, son choix (`chosenMethodology`) prime sur l'auto-pick : au recalcul du score,
l'association `methodology` est alignée sur le choix manuel et **non** sur la méthodologie déduite par
ordre de déclaration. Sans cela, l'étape 3 (`loadScorableMethods` dans `ExecutionPlanServiceImpl`)
calculerait le plan d'exécution sur une autre méthodologie que celle choisie en cas d'égalité.

### Historique des correctifs

| Correctif | Commit |
|---|---|
| `GET /multi-criteria-analysis/{id}` 500 → init du graphe lazy hors transaction | `4d93eff` |
| Survey réponse vide 500 → 400 (`ConstraintViolationException` mappée) | `a1e32d6` |
| Stabilisation Tier 1 : exec plan create 500 → 404/409, garde-fous null, brainstorming valide l'appartenance de la question | `770f6ec`, `3b7d36d`, `db37f54` |
| `chosenMethodology` : parsing JSON manuel → DTO `@Valid` (JSON inchangé) | `a059689` |
| `POST predictibility` : sentinelle `-1`/403 → `409 Conflict` | `a584480` |
| `GET multi-criteria score` rendu idempotent + endpoint explicite `PUT isFinished` | `7668ccc` |
| brainstorming `create` renvoie l'`id` → `update`/`validate`/`delete` outillables | `3ee114e` |
| `POST executionPlan/create` : 409 exige désormais une prévisibilité **finalisée** (et non juste existante) | _antérieur_ |
| Création de prévisibilité ouverte au Contributeur (`isProjectOwner` → `isContributer`) : vote méthodologie parallèle, plus d'attente du créateur (loader infini côté contributeur) | _antérieur_ |
| `tieBreakRequired` exposé sur `GET .../score` (égalité de méthodologie non départagée → bouton étape 3 grisé côté front) | _ce commit_ |
| `GET .../score` 500 (NPE) quand un `FormStep.eScore` est `null` → traité comme `0` | _ce commit_ |
| Égalité tranchée : `chosenMethodology` (choix owner) aligné sur l'association `methodology` au recalcul du score → l'étape 3 calcule le plan sur la bonne méthodologie | _ce commit_ |

---

## 5. Cas d'erreur (codes attendus)

```mermaid
flowchart TD
    req(["Requête"]) --> auth{"Token présent & valide ?"}
    auth -->|Non| e403a["403 Forbidden"]:::err
    auth -->|Oui| role{"Rôle autorisé ?<br/>(owner / contrib / observer)"}
    role -->|Non| e403b["403 Access Denied"]:::err
    role -->|Oui| valid{"Payload valide ?"}
    valid -->|"@Valid / @NotNull KO"| e400a["400 — MethodArgumentNotValid"]:::err
    valid -->|"@Validated param KO"| e400b["400 — ConstraintViolation"]:::err
    valid -->|Oui| exist{"Ressource existe ?"}
    exist -->|Non| e404["404 — *NotFoundException"]:::err
    exist -->|"doublon"| e409["409 — déjà voté / déjà commenté"]:::err
    exist -->|Oui| ok["2xx"]:::ok
    classDef err fill:#fdd,stroke:#c33,color:#111;
    classDef ok fill:#dfd,stroke:#3a3,color:#111;
```

Cas négatifs couverts par la collection Postman :

| Cas | Endpoint | Code |
|---|---|---|
| Création prévisibilité par Observateur | `POST /predictibility-analysis/{id}` | 403 |
| Score sur id inexistant | `GET /{id}/predictibility/999999/score` | 404 |
| Vote en doublon | `POST /{id}/vote` (2ᵉ fois) | 409 |
| Vote sans `predictibilityAnalysisId` | `POST /{id}/vote` | 400 |
| Vote sans token | `POST /{id}/vote` | 403 |
| Brainstorming par Observateur | `POST /brainstorming/{id}` | 403 |
| Réponse survey vide | `POST /survey/answer` | 400 |

---

## 6. Brainstorming — cycle de vie complet

`POST /brainstorming/{projectId}` renvoie le **`brainstormingId`** créé, ce qui permet d'outiller les
trois mutations qui en dépendent (`brainstormingRepository.findById`) :

- `PUT /brainstorming/{projectId}` — mise à jour (Contributeur **auteur** du commentaire)
- `PUT /brainstorming/validate/{projectId}/{brainstormingId}` — validation (owner, body `{ "isConfirmed": true }`)
- `DELETE /brainstorming/{projectId}/{brainstormingId}` — suppression (owner ou Contributeur)

Contraintes : **1 commentaire par utilisateur / question / analyse** ; la création valide que le
`questionId` appartient bien à l'analyse du projet (sinon `404`/`400`).

---

## 7. Couverture Postman (collection *ELIT - Parcours Collaboratif*)

État : **59 requêtes / 66 assertions — 0 échec**. Couverture des endpoints du contexte `collaboration` :

| Module | Couvert | Non couvert (raison) |
|---|---|---|
| Multi-criteria | GET analysis, GET score, **isFinished**, PUT iteration | PUT update step / question (*remplissage dynamique multi-steps, non bloquant*) |
| Predictibility | create, score (+ `tieBreakRequired`), chosenMethodology, isFinished, iteration | — |
| Vote | create, get, update | — |
| Brainstorming | create, get, abstract, **update, validate, delete** | — |
| Execution Plan | create, formConstraints, putAnswers, methodologies, vote, score, isFinished | — |
| Survey | question, answer, getAnswers | — |
| Report | upload, list, preview, download, delete | PUT update (*multipart redondant avec upload*) |

> Le folder `08 - Iterations (post-flux)` (itération multicritère) est volontairement **en fin de run** :
> l'itération ré-ouvre l'analyse multicritère (`isFinished=false`), l'insérer dans la chaîne ferait
> échouer la création de prévisibilité en aval (**409**).
>
> Le folder `09 - Tie break (regression)` est **auto-suffisant** (projet + analyses dédiés) : il
> reproduit le ticket d'égalité (owner vote *Agile* 3 / contributeur vote *Hybride* 3), asserte
> `tieBreakRequired === true` tant que l'owner n'a pas tranché, puis `=== false` après
> `POST .../chosenMethodology`. Il calcule le score multicritère avant de finaliser (sinon
> `FormStep.eScore` reste `null` — cf. correctif §4).
