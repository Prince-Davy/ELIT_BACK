# Endpoints backend appelés par ELIT_FRONT

> 📌 **SOURCE DE VÉRITÉ.** Ce fichier est dérivé du code front (`src/common/endpoints.js` +
> `src/hooks/apis/`) et fait foi pour le contrat d'API front/back. Une copie miroir vit dans
> `ELIT_BACK/docs/ENDPOINTS_BACKEND.md` (utilisée par `MIGRATION_PLAN.md`) : la modifier là-bas
> est interdit, resynchroniser depuis ici après toute mise à jour.

Document généré pour servir de base à la refactorisation du back. Les endpoints sont organisés par parcours utilisateur. Le préfixe de base est `REACT_APP_BACKEND_API` (voir `.env`), tous les chemins ci-dessous sont relatifs à cette base.

> Note : certains chemins sont construits avec `urlJoin(ENDPOINTS.xxx, ...params)`. Quand le projectId est inséré **avant** le segment de base (ex: `urlJoin(projectId, ENDPOINTS.previsibilityAnalysisVote, ...)`), le chemin final est `/{projectId}/vote/...` au lieu de `/vote/{projectId}/...`. Ces incohérences de placement de `projectId` sont signalées avec ⚠️ car elles sont de bons candidats à harmoniser lors de la refacto.

---

## 1. Transverse — Authentification & session

Fichier: `src/context/AuthProvider.js`, `src/utils/AxiosInstance.js`

| Méthode | Endpoint                                  | Description                                          |
| ------- | ----------------------------------------- | ---------------------------------------------------- |
| POST    | `/register`                               | Inscription d'un nouvel utilisateur                  |
| POST    | `/sendRegistrationConfirmationEmailAgain` | Renvoi de l'email de confirmation d'inscription      |
| POST    | `/login`                                  | Connexion (retourne access/refresh tokens)           |
| GET     | `/refreshToken`                           | Rafraîchissement du token d'accès                    |
| POST    | `/send-reset-password-email`              | Envoi de l'email de réinitialisation de mot de passe |
| POST    | `/reset-password?token={token}`           | Réinitialisation du mot de passe                     |

---

## 2. Transverse — Gestion utilisateur / profil

Fichier: `src/hooks/apis/UserApi.js`

| Méthode | Endpoint                                        | Description                                               |
| ------- | ----------------------------------------------- | --------------------------------------------------------- |
| GET     | `/user/users`                                   | Liste de tous les utilisateurs                            |
| GET     | `/user/users?keyword={keyword}`                 | Recherche d'utilisateurs par mot-clé                      |
| GET     | `/user/users?onlyActive=true&keyword={keyword}`     | Recherche d'utilisateurs actifs et vérifiés par mot-clé   |
| GET     | `/user/{userId}`                                | Récupère un utilisateur par son id                        |
| GET     | `/user/avatar/{userId}`                         | Récupère l'avatar (blob) d'un utilisateur                 |
| PUT     | `/user/avatar/{userId}`                         | Met à jour l'avatar (multipart)                           |
| DELETE  | `/user/avatar/{userId}`                         | Supprime l'avatar                                         |
| PUT     | `/user/info/{userId}`                           | Met à jour les informations du profil (multipart)         |
| PUT     | `/user/password/{userId}`                       | Met à jour le mot de passe (multipart)                    |

---

## 3. Transverse — Gestion des projets

Fichier: `src/hooks/apis/ProjetApi.js`, `src/hooks/apis/AdminApi.js`

| Méthode | Endpoint                                                   | Description                                                                                                     |
| ------- | ---------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------- |
| POST    | `/user/projects`                                           | Création d'un projet (multipart)                                                                                |
| GET     | `/user/projects`                                           | Liste des projets de l'utilisateur (legacy)                                                                     |
| DELETE  | `/user/projects/{projectId}`                               | Suppression d'un projet                                                                                         |
| PUT     | `/user/projects/{projectId}`                               | Mise à jour d'un projet (multipart)                                                                             |
| DELETE  | `/user/projects/{projectId}/delete-contributor/{collabId}` | Retrait d'un collaborateur du projet                                                                            |
| POST    | `/user/projects/{projectId}/add-contributor`               | Ajout d'un collaborateur (multipart)                                                                            |
| GET     | `/user/projects/getdata/{projectId}`                       | Détail complet d'un projet (page Dashboard projet)                                                              |
| POST    | `/user/projectlist/`                                       | Liste paginée/filtrée des projets (page d'accueil, body: page, size, key, status, sort, activeTab, typeAnalyse) |
| PUT     | `/admin/projects/{projectId}`                              | Confirmation/rejet d'un projet (`{ isConfirmed: bool }`) — utilisé aussi côté admin                             |

### Administration (rôle admin)

| Méthode | Endpoint                      | Description                                |
| ------- | ----------------------------- | ------------------------------------------ |
| GET     | `/admin/projects/`            | Liste des projets en attente de validation |
| PUT     | `/admin/projects/{projectId}` | Valide un projet (`{ isConfirmed: true }`) |
| DELETE  | `/admin/projects/{projectId}` | Supprime un projet (vue admin)             |

---

## 4. Transverse — Fichiers / pièces jointes

Fichier: `src/hooks/apis/FileApi.js`

| Méthode | Endpoint                                                    | Description                                                                                                                                             |
| ------- | ----------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------- |
| GET     | `/api/user/attachments/preview/{projectId}/{attachementId}` | ⚠️ Prévisualisation d'un fichier (blob) — préfixe `/api` codé en dur, incohérent avec les autres endpoints `user/attachments` (sans `/api`), à corriger |
| GET     | `/user/attachments/download/{projectId}/{attachementId}`    | Téléchargement d'un fichier (blob)                                                                                                                      |
| DELETE  | `/user/attachments/{attachementId}/{projectId}`             | Suppression d'un fichier                                                                                                                                |
| PUT     | `/user/attachments/edit/{id}`                               | Renommage d'un fichier                                                                                                                                  |
| POST    | `/user/attachments/upload/{projectId}`                      | Upload de fichier(s) (multipart)                                                                                                                        |

---

## 5. Transverse — Rapports (Reports)

Fichier: `src/hooks/apis/ReportApi.js`
Utilisé en fin de parcours collaboratif (plan d'exécution / méthodologie) pour générer/gérer le rapport final.

| Méthode | Endpoint                                                           | Description                                   |
| ------- | ------------------------------------------------------------------ | --------------------------------------------- |
| POST    | `/report/upload/{projectId}/{analyseId}/{type}/{iteration}`        | Upload d'un rapport (multipart)               |
| PUT     | `/report/upload/{projectId}/{analyseId}/{type}/{iteration}/update` | Mise à jour d'un rapport existant (multipart) |
| GET     | `/report/{projectId}`                                              | Liste des rapports d'un projet                |
| GET     | `/report/preview/{reportId}`                                       | Prévisualisation d'un rapport (blob PDF)      |
| GET     | `/report/download/{reportId}`                                      | Téléchargement d'un rapport (blob)            |
| DELETE  | `/report/{reportId}`                                               | Suppression d'un rapport                      |

---

## 6. Parcours collaboratif

### 6.1 Analyse multicritère

Fichier: `src/hooks/apis/AnalyseMulticritereApi.js`, `src/hooks/apis/Brainstorming.js`

| Méthode | Endpoint                                                     | Description                                                                                              |
| ------- | ------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------- |
| GET     | `/multi-criteria-analysis/{projectId}`                       | Récupère l'analyse multicritère du projet                                                                |
| PUT     | `/multi-criteria-analysis/{projectId}`                       | Met à jour les réponses de l'analyse multicritère                                                        |
| PUT     | `/{projectId}/multi-criteria-analysis/iteration/{analyseId}` | ⚠️ Incrémente l'itération de l'analyse multicritère (projectId en préfixe)                               |
| GET     | `/multi-criteria-analysis/{projectId}/score`                 | Récupère les résultats/score de l'analyse multicritère (lecture seule, idempotent)                       |
| PUT     | `/{projectId}/multi-criteria-analysis/isFinished/{analyseId}`| Marque l'analyse multicritère comme terminée                                                             |
| PUT     | `/{projectId}/multi-criteria-analysis/question`              | ⚠️ Modifie une question de l'analyse multicritère (depuis le module Brainstorming, projectId en préfixe) |

### 6.2 Analyse de prévisibilité (+ vote)

Fichier: `src/hooks/apis/AnalysePrevisibiliteApi.js`

| Méthode | Endpoint                                                    | Description                                                                                          |
| ------- | ----------------------------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| POST    | `/predictibility-analysis/{projetId}`                       | Création d'une analyse de prévisibilité pour le projet                                               |
| GET     | `/vote/{id}`                                                | Récupère tous les votes pour une analyse de prévisibilité                                            |
| POST    | `/{projectId}/vote/`                                        | ⚠️ Création d'un vote (projectId en préfixe, alors que GET et PUT ci-dessous ont `vote/` en préfixe) |
| PUT     | `/vote/{projectId}/{voteId}`                                | Mise à jour d'un vote                                                                                |
| GET     | `/{projectId}/predictibility/{analyseId}/score`             | Récupère le résultat/score de l'analyse de prévisibilité                                             |
| PUT     | `/{projectId}/predictibility/iteration/{analyseId}`         | Passe à l'itération suivante de l'analyse de prévisibilité                                           |
| PUT     | `/{projectId}/predictibility/isFinished/{analyseId}`        | Marque l'analyse de prévisibilité comme terminée                                                     |
| POST    | `/{projectId}/predictibility/{analyseId}/chosenMethodology` | Enregistre la méthodologie choisie à l'issue de l'analyse de prévisibilité                           |

### 6.3 Brainstorming

Fichier: `src/hooks/apis/Brainstorming.js`, `src/hooks/apis/PlanExecutionApi.js`

| Méthode | Endpoint                                                | Description                                                                                 |
| ------- | ------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| GET     | `/brainstorming/{projectId}/{idAL}`                     | Récupère le brainstorming d'une analyse (idAL = id analyse de prévisibilité)                |
| GET     | `/brainstorming/abstract/{projectId}/{idAL}`            | Récupère le résumé du brainstorming                                                         |
| GET     | `/brainstorming/abstract/{prevId}`                      | Récupère le résumé du brainstorming par id (sans projectId — utilisé dans PlanExecutionApi) |
| DELETE  | `/brainstorming/{projectId}/{commentId}`                | Supprime un commentaire de brainstorming                                                    |
| POST    | `/brainstorming/{projectId}`                            | Ajoute un commentaire de brainstorming                                                      |
| PUT     | `/brainstorming/{projectId}`                            | Modifie un commentaire de brainstorming                                                     |
| PUT     | `/brainstorming/validate/{projectId}/{brainstormingId}` | Valide un commentaire de brainstorming                                                      |

### 6.4 Plan d'exécution

Fichier: `src/hooks/apis/PlanExecutionApi.js`

| Méthode | Endpoint                                                                  | Description                                                   |
| ------- | ------------------------------------------------------------------------- | ------------------------------------------------------------- |
| POST    | `/executionPlan/create/{projectId}`                                       | Création d'un nouveau plan d'exécution                        |
| GET     | `/{projectId}/executionPlan/formConstraints/{idPE}`                       | Récupère les questions/contraintes du plan d'exécution        |
| GET     | `/{projectId}/executionPlan/methodologies/{idPE}`                         | Récupère les méthodologies proposées pour le plan d'exécution |
| POST    | `/{projectId}/executionPlan/methodologies/vote`                           | Enregistre le vote sur une méthodologie                       |
| PUT     | `/{projectId}/executionPlan/formConstraints/putAnswers/{executionPlanId}` | Enregistre les réponses aux contraintes                       |
| GET     | `/{projectId}/executionPlan/{idPE}/score`                                 | Récupère le résultat/score du plan d'exécution                |
| POST    | `/{projectId}/executionPlan/isFinished/{idPE}`                            | Marque le plan d'exécution comme terminé                      |
| GET     | `/survey/question`                                                        | Récupère les questions du sondage de satisfaction             |
| GET     | `/survey/answer/{userId}`                                                 | Récupère les réponses au sondage pour un utilisateur          |
| POST    | `/survey/answer`                                                          | Enregistre une réponse au sondage                             |

**Flux global parcours collaboratif** : `AnalyseMulticriteres` → `AnalysePrevisibilite` (avec votes/itérations, `Brainstorming` en option) → `PlanExecution` (avec rapport final via Reports).

---

## 7. Parcours IA

### 7.1 Data Entry (saisie + lancement analyse IA)

Fichier: `src/hooks/apis/DataEntryApi.js`

| Méthode | Endpoint                            | Description                                                 |
| ------- | ----------------------------------- | ----------------------------------------------------------- |
| POST    | `/answers/{projectId}`              | Sauvegarde des réponses du formulaire de saisie             |
| GET     | `/answers/{projectId}`              | Récupère les réponses sauvegardées (pré-remplissage)        |
| POST    | `/projects/{projectId}/ai-analysis` | Envoie les réponses pour déclencher le calcul IA            |
| GET     | `/result/ai/{projectId}`            | Récupère le résultat IA (méthodologie + score de confiance) |
| GET     | `/result/ai/mapping/{projectId}`    | Récupère le mapping des conseils IA associé au résultat     |

### 7.2 Methodology Choice (choix de méthodologie + feedbacks + Kiviat)

Fichier: `src/hooks/apis/MethodologyChoiceApi.js`, `src/services/methodologyChoiceApi.ts`

| Méthode | Endpoint                                    | Description                                                                                                                                  |
| ------- | ------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| GET     | `/feedbacks/projectIa/{projectId}/{userId}` | Récupère le feedback utilisateur sur la méthodologie proposée                                                                                |
| POST    | `/feedbacks/projectIa/{projectId}`          | Envoie un feedback sur la méthodologie proposée                                                                                              |
| DELETE  | `/feedbacks/projectIa/{projectId}/{userId}` | Supprime un feedback                                                                                                                         |
| GET     | `/kiviat/methodology/{projectId}`           | Données du diagramme Kiviat (axe méthodologie), avec retry jusqu'à 4 tentatives sur 404                                                      |
| GET     | `/kiviat/project/{projectId}`               | Données du diagramme Kiviat (axe projet), avec retry jusqu'à 4 tentatives sur 404                                                            |
| POST    | `/result/ai/{projectId}`                    | ⚠️ Relance l'analyse de méthodologie avec le payload "PMM" (format 25 questions filtré côté back) — même base que le GET en 7.1 mais en POST |

### 7.3 Method Recommendation (recommandation de méthode + feedbacks)

Fichier: `src/hooks/apis/MethodRecommendationApi.js`, `src/services/methodRecommendationApi.ts`

| Méthode | Endpoint                                                               | Description                                                                                                                  |
| ------- | ---------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| GET     | `/projects/{projectId}/methodology-evaluations`                        | Récupère les résultats de recommandation de méthode                                                                          |
| POST    | `/feedbacks-method/projectIa/method/{projectId}`                       | Envoie un feedback sur la méthode recommandée                                                                                |
| GET     | `/feedbacks-method/projectIa/method/{projectId}`                       | Récupère tous les feedbacks sur la méthode recommandée                                                                       |
| GET     | `/feedbacks-method/projectIa/method/{projectId}/{userId}/{methodName}` | Récupère le feedback pour un projet/utilisateur/méthode donnés                                                               |
| DELETE  | `/feedbacks-method/projectIa/method/{projectId}/{userId}/{methodName}` | Supprime un feedback                                                                                                         |
| POST    | `/result/ai/method-recommendation/{projectId}`                         | ⚠️ Endpoint "placeholder" côté front (commenté comme à confirmer avec le back), payload PMM avec le résultat de méthodologie |

**Flux global parcours IA** : `DataEntryPage` (7.1, saisie + envoi IA) → attente 5-7s → `MethodologyChoicePage` (7.1 GET résultats + 7.2 feedback/Kiviat) → `MethodRecommendationPage` (7.3).

---

## 8. Audit

Fichier: `src/hooks/apis/AuditApi.ts`

| Méthode | Endpoint                                     | Description                                                                                            |
| ------- | -------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| GET     | `/audit/project/{projectId}/has-audit`       | Vérifie si un projet a déjà un audit                                                                   |
| GET     | `/audit/project/{projectId}/audit/{auditId}` | Récupère un audit spécifique (données brutes, pas les résultats)                                       |
| GET     | `/audit/project/{projectId}`                 | Récupère tous les audits d'un projet (ou un audit précis via filtrage côté front avec `targetAuditId`) |
| POST    | `/audit`                                     | Création d'un audit (`{ projectId, method, questions }`)                                               |
| POST    | `/audit/updateAudit`                         | Mise à jour des réponses d'un audit existant                                                           |
| POST    | `/audit/{auditId}/calculate`                 | Lance le calcul du score de l'audit                                                                    |
| GET     | `/audit/{auditId}/auditScore`                | Récupère le score calculé de l'audit                                                                   |

**Flux global audit** : `AuditPage` (questionnaire, création/MAJ via `/audit` et `/audit/updateAudit`) → lancement du calcul (`/audit/{auditId}/calculate`) → `AuditScore` (`/audit/{auditId}/auditScore`).

---

## 9. Champs consommés — croisement `endpoints.js` × `hooks/apis/`

Cette section descend au niveau **champ** : que le front **envoie** dans le corps de requête, et quels champs de **réponse** il **lit réellement**. C'est ce niveau qui casse si le back renomme/déplace un champ.

**Légende du couplage :**

- 🔴 **fort** — le front lit des champs nommés précis (`resultAi.predictedProba`, `response.data.auditId`, …). Renommer côté back casse le front silencieusement. **À surveiller en priorité.**
- 🟡 **moyen** — payload/réponse structuré dont la forme est figée par le front (DTO typé, objet construit), mais souvent stocké tel quel dans Redux.
- ⚪ **opaque (pass-through)** — le front transmet/stocke l'objet sans dépendre de noms de champs au niveau du hook (le couplage réel est plus bas, dans les composants/reducers).

### 9.1 Parcours IA — le plus fortement couplé

| Endpoint (verbe)                                                                 | Corps envoyé (champs)                                                                | Réponse lue (champs)                                                                                                                                              | Couplage |
| -------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| `POST /answers/{projectId}`                                                      | `{ data: { "<questionId>": "<answerId>", … } }`                                      | — (status 201 seul)                                                                                                                                               | 🟡       |
| `GET /answers/{projectId}`                                                       | —                                                                                    | `data` = `{ "<questionId>": "<answerId>" }` (pré-remplissage formulaire)                                                                                          | 🟡       |
| `POST /projects/{projectId}/ai-analysis`                                         | `{ data: { "<questionId>": "<answerId>", … } }`                                      | —                                                                                                                                                                 | 🟡       |
| `GET /result/ai/{projectId}`                                                     | —                                                                                    | **`predictedProba`** (→ `trustScore = Math.round`), **`predictedMethod`** (→ `recommandedMethodology`) — voir `methodologyChoiceSaga.fetchMethodologyResultsSaga` | 🔴       |
| `GET /result/ai/mapping/{projectId}`                                             | —                                                                                    | tableau (`data.length > 0` requis) stocké tel quel dans `advicesDataJson`                                                                                         | 🟡       |
| `POST /result/ai/{projectId}` (PMM step 2)                                       | `{ data: { "<pmmField>": "<valeur FR>", … } }` (21 questions, exclut Q1/Q2/Q6/Q25)   | `MethodologyResult` = `{ predictedMethod, predictedProba, status? }`                                                                                              | 🔴       |
| `POST /result/ai/method-recommendation/{projectId}` (PMM step 3, ⚠️ placeholder) | `{ data: { "<pmmField>": … }, model_type: "agile" \| "traditional" }` (25 questions) | `MethodRecommendationResult` = `{ recommendedMethod, confidenceScore, alternatives?, status? }`                                                                   | 🔴       |
| `GET /projects/{projectId}/methodology-evaluations`                              | —                                                                                    | tableau de recommandations stocké tel quel dans `recommendations`                                                                                                 | ⚪       |
| `GET/POST/DELETE /feedbacks/projectIa/...`                                       | `body` = objet feedback (construit côté page)                                        | `data` feedback stocké tel quel                                                                                                                                   | ⚪       |
| `GET/POST/DELETE /feedbacks-method/projectIa/method/...`                         | `body` = objet feedback                                                              | `data` feedback stocké tel quel                                                                                                                                   | ⚪       |
| `GET /kiviat/methodology/{projectId}` · `GET /kiviat/project/{projectId}`        | —                                                                                    | `data` du diagramme (consommé dans le composant Kiviat)                                                                                                           | 🟡       |

> Le nom des `<pmmField>` (clé de `data`) vient de `pmmConfig.json` (`question.pmmField`) via `utils/pmmTransformer.ts`. Côté back, ces clés (« Project Complexity », « Duration (month) », … ) **et** les noms de retour `predictedMethod` / `predictedProba` / `recommendedMethod` / `confidenceScore` sont le contrat le plus sensible du parcours IA.
> `model_type` est dérivé du résultat de méthodologie (`Agile→agile`, `Traditional`/`Hybrid→traditional`).

### 9.2 Audit — DTO typé (couplage fort)

| Endpoint (verbe)                                 | Corps envoyé (champs)                                                                                             | Réponse lue (champs)                                                | Couplage |
| ------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------- | -------- |
| `GET /audit/project/{projectId}/has-audit`       | —                                                                                                                 | booléen brut (`data === true`)                                      | 🔴       |
| `GET /audit/project/{projectId}`                 | —                                                                                                                 | tableau d'audits ; filtré côté front sur **`audit.auditId`**        | 🔴       |
| `GET /audit/project/{projectId}/audit/{auditId}` | —                                                                                                                 | objet audit (brut)                                                  | ⚪       |
| `POST /audit`                                    | `{ projectId, method (UPPERCASE), questions: [] }`                                                                | **`response.data.auditId`** (stocké en sessionStorage)              | 🔴       |
| `POST /audit/updateAudit`                        | `{ projectId, auditId, questions: [{ category, … }] }` (`categoryDisplayName` → `category` via `convertCategory`) | objet audit                                                         | 🔴       |
| `POST /audit/{auditId}/calculate`                | —                                                                                                                 | `AuditScoreDTO`                                                     | 🔴       |
| `GET /audit/{auditId}/auditScore`                | —                                                                                                                 | `AuditScoreDTO` (404 = pas encore calculé, traité comme non-erreur) | 🔴       |

**`AuditScoreDTO`** (contrat figé par `reducers/auditScore/auditScoreReducer.ts`) :
`auditScoreId`, **`auditId`** (clé d'indexation Redux), `userId`, `globalScorePercentage`, `globalComment { id, method, level, text, title }`, `auditCategoryResults[] { category, scorePercentageCategory, level, categoryCommentId, text }`, `questionResponses[] { questionId, questionRef, category, answerLabel, answerValue, score }`, `submissionDate`.
`level` ∈ `{ BON, MOYEN, MAUVAIS }` (enum `LevelScore`).

### 9.3 Parcours collaboratif — surtout pass-through

Les hooks renvoient `response.data` quasi tel quel vers les reducers/composants ; le couplage fin se joue dans les composants. À surveiller au niveau hook :

| Endpoint (verbe)                                                                                    | Corps envoyé                           | Réponse lue (au niveau hook)                          | Couplage |
| --------------------------------------------------------------------------------------------------- | -------------------------------------- | ----------------------------------------------------- | -------- |
| `GET /multi-criteria-analysis/{projectId}`                                                          | —                                      | objet analyse (consommé dans les composants)          | ⚪       |
| `PUT /multi-criteria-analysis/{projectId}`                                                          | `data` (réponses, construit côté page) | status                                                | ⚪       |
| `GET /multi-criteria-analysis/{projectId}/score`                                                    | —                                      | objet score                                           | ⚪       |
| `PUT /{projectId}/multi-criteria-analysis/question`                                                 | `payload` (question modifiée)          | status                                                | ⚪       |
| `POST /{projectId}/vote/` · `PUT /vote/{projectId}/{voteId}`                                        | `data` / `payload` (vote)              | status 200 ; erreur lit `error.response.data.message` | ⚪       |
| `GET /vote/{id}`                                                                                    | —                                      | tableau de votes                                      | ⚪       |
| `GET /{projectId}/predictibility/{analyseId}/score`                                                 | —                                      | objet score prévisibilité                             | ⚪       |
| `POST /{projectId}/predictibility/{analyseId}/chosenMethodology`                                    | **`{ chosenMethodology: <valeur> }`**  | status 200                                            | 🔴       |
| `GET /brainstorming/...` (×3)                                                                       | —                                      | brainstorming / résumé                                | ⚪       |
| `POST·PUT·DELETE·validate /brainstorming/...`                                                       | `payload` (commentaire)                | objet commentaire                                     | ⚪       |
| `GET /{projectId}/executionPlan/formConstraints/{idPE}` · `/methodologies/{idPE}` · `/{idPE}/score` | —                                      | questions / méthodologies / score                     | ⚪       |
| `POST /{projectId}/executionPlan/methodologies/vote`                                                | `payload` (vote)                       | response ; erreur lit `error.response.data.message`   | ⚪       |
| `PUT /{projectId}/executionPlan/formConstraints/putAnswers/{executionPlanId}`                       | `payload` (réponses)                   | response                                              | ⚪       |
| `GET /survey/question` · `GET /survey/answer/{userId}` · `POST /survey/answer`                      | `payload` (réponse survey)             | objet survey                                          | ⚪       |

> Pour les ⚪ ci-dessus, le contrat de champ réel est porté par les composants/reducers (ex: `executionPlanReducer`, composants `PlanExecution`, `StageBaseStep*`). Si le back refactore ces structures, la surveillance doit se faire **côté composants**, pas au niveau hook.

### 9.4 Synthèse — champs « à surveiller en priorité » pour la refacto back

1. **IA** : clés `data` PMM (`pmmConfig.json` → `pmmField`) en entrée ; `predictedMethod`, `predictedProba`, `recommendedMethod`, `confidenceScore`, `alternatives` en sortie.
2. **Audit** : `auditId` (création + indexation Redux) et tout le `AuditScoreDTO` (9.2).
3. **DataEntry** : forme `{ data: { "<questionId>": "<answerId>" } }` (aller-retour save/prefill).
4. **chosenMethodology** : clé `chosenMethodology` du POST prévisibilité.
5. **Messages d'erreur** : plusieurs hooks affichent `error.response.data.message` (vote prévisibilité, vote plan d'exécution) — garder ce champ `message` dans les réponses d'erreur.

---

## Récapitulatif des incohérences à examiner pour la refacto

1. **Ordre `projectId` / segment de ressource non uniforme** : plusieurs endpoints placent `projectId` avant le segment fixe (`/{projectId}/vote/`, `/{projectId}/predictibility/...`, `/{projectId}/executionPlan/...`, `/{projectId}/multi-criteria-analysis/...`) alors que d'autres le placent après (`/vote/{id}`, `/multi-criteria-analysis/{projectId}`, `/predictibility-analysis/{projetId}`). À harmoniser (ex: tout en `/{resource}/{projectId}/...`).
2. **`POST /{projectId}/vote/` vs `PUT /vote/{projectId}/{voteId}`** : création et mise à jour de vote n'ont pas le même ordre de segments.
3. **`/api/user/attachments/preview/...`** dans `FileApi.previewFile` a un préfixe `/api` en dur, absent des autres appels `user/attachments/*`.
4. **Deux endpoints `result/ai` réutilisés à des fins différentes** : GET pour récupérer le résultat IA (Data Entry / Methodology Choice) et POST pour relancer l'analyse avec un payload PMM (Methodology Choice) — même chemin de base, sémantiques différentes selon le verbe HTTP.
5. **`/result/ai/method-recommendation/{projectId}`** est documenté dans le code comme "à confirmer avec le backend" (placeholder), à valider/officialiser.
6. **`PUT /admin/projects/{projectId}`** est utilisé à la fois pour la validation côté admin et pour la validation/rejet côté `ProjetApi.validationProjectApi` — vérifier si un seul endpoint doit servir ces deux cas d'usage ou s'ils doivent être séparés (droits différents : admin vs chef de projet).
