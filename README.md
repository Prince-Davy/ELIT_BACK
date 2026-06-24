# ELIT — Backend (`com.elit:outildedecision`)

API REST **Spring Boot 3.5 / Java 21** au service de l'outil d'aide à la décision ELIT.
Tous les endpoints sont exposés sous le context path `/api`.

## 🚩 Documentation

- [Documentation complète](https://capgemini.sharepoint.com/:w:/r/sites/ELIThELponITprojectmanagement/Shared%20Documents/General/1%20-%20Dev%20Team%20area/Environnement%20de%20d%C3%A9veloppement/Guide%20d%27installation%20ELIT.docx?d=w6adc79bd058f4752ba25bf73a31cf7d9&csf=1&web=1&e=XgrhjU)
- [Good practices document](https://capgemini.sharepoint.com/:w:/r/sites/ELIThELponITprojectmanagement/_layouts/15/Doc.aspx?sourcedoc=%7B5775C1E1-62A0-41F1-98E3-B741BCC4A495%7D&file=BonnePratique.docx&action=default&mobileredirect=true) — n'hésitez pas à le mettre à jour.
- 📚 **Docs techniques dans [`docs/`](docs/)** :
  - [Vue d'ensemble de l'architecture](docs/ARCHITECTURE_OVERVIEW.md)
  - [Règles de calcul métier par composant](docs/REGLES_CALCUL_METIER.md)
  - [Contrat des endpoints](docs/ENDPOINTS_BACKEND.md)

> 📊 Ces docs contiennent des diagrammes [Mermaid](https://mermaid.js.org/). Sur **IntelliJ IDEA**,
> installez le plugin **Mermaid** (Settings → Plugins → Marketplace) pour les voir dans l'aperçu Markdown
> (GitHub et VSCode les affichent nativement).

> 🚨 **NON NÉGOCIABLE** — dès que vous touchez à une **règle de calcul métier** (un calculateur du
> package `calculate/`), vous **DEVEZ** mettre à jour [`docs/REGLES_CALCUL_METIER.md`](docs/REGLES_CALCUL_METIER.md)
> (et l'architecture si elle bouge) **dans le même commit**. Une formule modifiée sans doc à jour est un
> commit incomplet. 🚨

## Prérequis

- **JDK 21** (Temurin recommandé).
- on utilise le wrapper Maven (`mvnw`).
- Docker (uniquement pour le lancement conteneurisé).

> ⚠️ Toujours préfixer les commandes Maven par le JDK et utiliser le wrapper.
>
> PowerShell (adaptez le chemin à votre installation du JDK 21) :
>
> ```powershell
>  .\mvnw.cmd <goals>
> ```

## 🏅 Règle d'or — le code vert ou rien

> **Avant tout commit** : `clean test` puis `checkstyle:check pmd:check` doivent être **verts**.
> **Avant chaque push** : `clean verify` doit être **vert**.

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd checkstyle:check pmd:check
.\mvnw.cmd clean verify   # avant le push
```

> ☠️ Quiconque push du code rouge sans lancer le gate `rm -rf` sa propre dignité : **build broken = skill issue**, `git blame` ne pardonne pas et le CI te `kill -9` en place publique. Sois pas ce dev. 💀

## Configuration

- **Comment fonctionnent les `.properties`** : `application.properties` est la **base commune**
  (valeurs par défaut/prod), chargée en premier. Spring applique ensuite
  `application-<profil>.properties` par-dessus : ces fichiers ne contiennent **que les valeurs qui
  diffèrent** de la base (pas de recopie). Les valeurs sont résolues depuis le `.env` via la syntaxe
  `${MA_VARIABLE}`, avec repli optionnel `${MA_VARIABLE:valeur_par_defaut}` quand un défaut est défini.
- **Profils disponibles** : `local`, `docker`, `test`. Le profil actif est choisi via
  `SPRING_PROFILES_ACTIVE`.
- Les lancements local/Docker lisent les secrets depuis un fichier `.env`
  (`spring.config.import=optional:file:.env`). Dupliquez `.env.tpl` → `.env`.
- Base de données par défaut : **MySQL**. `DB_SERVER=localhost` pour un lancement IDE/Maven, `db` pour Docker.
- Le profil **`test` démarre sur H2 en mémoire**, sans réseau ni `.env` : c'est ce dont dépend le
  harnais de snapshots, il doit rester auto-suffisant.

## Lancer l'application en local (sans Docker — recommandé pour le développement)

1. Dupliquer `.env.tpl` en `.env` et renseigner les variables (notamment `DB_SERVER=localhost`).
2. Lancer l'application (préfixez par le JDK 21, adaptez le chemin à votre installation) :

   ```powershell
   $env:JAVA_HOME="C:\Users\<vous>\.jdks\temurin-21.0.11"; .\mvnw.cmd spring-boot:run
   ```

   Pour forcer un profil explicitement :

   ```powershell
   $env:JAVA_HOME="C:\Users\<vous>\.jdks\temurin-21.0.11"; $env:SPRING_PROFILES_ACTIVE="local"; .\mvnw.cmd spring-boot:run
   ```

   L'application démarre sur **http://localhost:8080**, context path `/api`
   (Swagger UI : http://localhost:8080/api/swagger-ui.html).

## Lancer l'application avec Docker (iso AzureDevOps)

1. Créer le réseau partagé :

   ```shell
   docker network create elit_shared_network
   ```

2. Démarrer la stack (base, phpMyAdmin et backend) :

   ```shell
   docker-compose -f dev.docker-compose.yml up --build --force-recreate --remove-orphans
   ```

   > Si vous lancez le backend depuis l'IDE ou Maven, commentez le service `back` dans
   > `dev.docker-compose.yml` et passez `DB_SERVER=localhost`.

## Build & tests

| Tâche                                                               | Goals (après le préfixe JDK ci-dessus)                  |
| ------------------------------------------------------------------- | ------------------------------------------------------- |
| Compilation + tests unitaires                                       | `clean test`                                            |
| Gate qualité complète (ce que la CI exécute)                        | `clean verify` — tests + JaCoCo + Checkstyle + PMD      |
| Lint seul                                                           | `checkstyle:check pmd:check`                            |
| Un seul test (classe)                                               | `test -Dtest=AuditServiceImplTest`                      |
| Un seul test (méthode)                                              | `test -Dtest=AuditServiceImplTest#shouldReturnNotFound` |
| Ré-enregistrer les snapshots (changement de contrat acté seulement) | `test "-Dsnapshots.record=true"`                        |

**Avant tout commit**, `clean test` puis `checkstyle:check pmd:check` doivent être au vert.
Checkstyle et PMD sont liés à la phase `verify` et font échouer le build à la moindre violation.

## Base de données

- Schéma piloté par Hibernate avec **`ddl-auto=update`** (Liquibase a été retiré, le schéma est gelé).
- Les noms `@Table` / `@Column` sont conservés à l'identique lors des renommages d'entités, sous peine
  de créer une table fantôme.

## Jeux de données initiaux (seeds)

Au démarrage, `DataSeedInitializer` (un `ApplicationRunner` dans `shared/config/seed/`) exécute **une
fois** tous les `DataSeeder` enregistrés, sur **tous les environnements**.

| Seeder           | Contenu                                                                                                                | Source                       | Switch dédié                 |
| ---------------- | ---------------------------------------------------------------------------------------------------------------------- | ---------------------------- | ---------------------------- |
| `demo-users`     | Les 4 comptes de démo (voir [Comptes utilisateurs locaux](#comptes-utilisateurs-locaux)), mot de passe hashé en BCrypt | code Java (`DemoDataSeeder`) | `app.seed.demo.enabled`      |
| `reference-data` | Données métier de référence : `methodology`, `method`, `execution_plan_question`, `survey_question`, `category_comment`, `global_comment` | scripts SQL `db/seed/reference/*.sql` | `app.seed.reference.enabled` |

**Principes :**

- **Interrupteur global** : `app.seed.enabled` (défaut `true`) ; chaque seeder a son propre switch
  (défaut `true`).
- **Idempotent** : chaque seeder vérifie l'existence avant d'insérer (`demo-users` par e-mail,
  `reference-data` insère une table seulement si elle est vide `COUNT(*) = 0`). Un redémarrage ne
  duplique ni n'écrase rien.
- **Gestion d'erreur** : sur le profil `test`, l'échec d'un seeder est loggué puis ignoré (le contexte
  de test reste debout) ; sur **tout autre profil**, un échec **interrompt le démarrage** pour éviter
  une base à moitié peuplée.
- **Profil `test`** : les deux seeders sont désactivés (`app.seed.demo.enabled=false`,
  `app.seed.reference.enabled=false`) — chaque test sème ses propres données.
- **`reference-data` cible MySQL** : les scripts utilisent le dialecte historique MySQL et ne tournent
  donc que sur les environnements `local`/`docker`/prod (pas sur H2).
- **Extensible** : pour ajouter un jeu de données, créer un `@Component` implémentant `DataSeeder` —
  il est découvert et exécuté automatiquement, sans modifier l'initializer.

Pour désactiver tout le seeding en local, ajouter dans `.env` (ou en propriété) :

```properties
app.seed.enabled=false
```

## Documentation API (Swagger / OpenAPI)

- Swagger UI : `${BACKEND_URL}/api/swagger-ui.html`
- JSON : `${BACKEND_URL}/api/v3/api-docs`
- YAML : `${BACKEND_URL}/api/v3/api-docs.yaml`

## Comptes utilisateurs locaux

| Id  | Prénom   | Nom    | Login                | Mot de passe |
| :-- | :------- | :----- | :------------------- | :----------- |
| 1   | John     | Doe    | user@user.com        | Azerty1234!? |
| 2   | Nancy    | Michel | admin@elite.fr       | Azerty1234!? |
| 3   | Charles  | Baker  | contributer@elit.com | Azerty1234!? |
| 4   | Rochelle | Wright | observer@elit.com    | Azerty1234!? |

## Conventions de commit

Le projet suit la convention **[Conventional Commits](https://www.conventionalcommits.org/)** :

```
<type>(<scope>): <description courte à l'impératif>
```

- **type** : `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `perf`, `style`, `build`, `ci`.
- **scope** (optionnel) : le périmètre touché (ex. `audit`, `ai`, `collaboration`, `shared`).
- **description** : concise, à l'impératif présent, sans point final.

Exemples (issus de l'historique du projet) :

```
refactor(collaborative): éradication totale des packages plats
feat(audit): ajoute le calcul du score de criticité
fix(ai): corrige le mapping du kiviat
```
