# ==========================================================================
# ELIT backend — local environment template. Copy to `.env` and fill in.
# Consumed by application(-local|-docker).properties via spring.config.import.
# ==========================================================================

# --- Database ---
MYSQL_USER=test
MYSQL_PASSWORD=test
MYSQL_ROOT_PASSWORD=test
MYSQL_DATABASE=outilprisedecision
# Host:port — `localhost:3306` for IDE/Maven, the `db` service is for Docker
DB_SERVER=localhost:3306
DB_SERVER_WITH_DOCKER=db:3306

# --- Security ---
SPRING_BACK_USERNAME=
SPRING_BACK_PASSWORD=
JWT_SECRET_BACK=ELITE2022

# --- Mail (SMTP) ---
SPRING_MAIL_USERNAME=elit.sogeti@sogetilabs.fr
SPRING_MAIL_PASSWORD=sxcwmyfhazuhtzic

# --- Azure Communication Service ---
# Overrides the default baked into application.properties. Keep the real key out of git.
# FYI the secret for sogetilabs cannot be shared in local dev, it will be available only in integ and recette env
#AZURE_COMMUNICATION_CONNECTION_STRING=endpoint=https://sogetilabs-communication-service.europe.communication.azure.com/;accesskey=<your-access-key>

# --- URLs (back / front) ---
REACT_APP_BACKEND_API=http://localhost:8080
FRONTEND_URL=http://localhost:3000

# --- AI prediction (PMM) ---
# Integ endpoint, or http://ppmprediction:80/api/ when running it in Docker.
# The host MUST NOT contain an underscore: Java/Reactor Netty (WebClient) rejects it
# ("Host is not specified"). Name the prediction service "ppmprediction" in its compose.
# Both stacks must share the same Docker network; create it once:
#   docker network create elit_shared_network
# and declare it as `external: true` in both compose files.
AI_API_URL_INTEG=https://integ.elit-pmm-pred.innovation-factory.io/api/
AI_API_URL_WITH_DOCKER=http://ppmprediction:80/api/

# --- File paths (injected by the deployment manifests; the app reads JSON from the classpath) ---
FILE_UPLOAD_DIR=src/main/resources/projects/
AVATAR_UPLOAD_DIR=src/main/resources/avatars/
FILE_QUESTIONS=src/main/java/com/elit/outildedecision/util/MultiCriteriaAnalysisFormSchema.Json
FILE_RECOMMANDATIONS=sharedresources/com/elit/outildedecision/util/RecommandationsIA.json

# --- Seeds (optional — default: enabled on local/docker). Uncomment to disable. ---
# APP_SEED_ENABLED=false            # disables all startup seeding
# APP_SEED_DEMO_ENABLED=false       # disables demo accounts
# APP_SEED_REFERENCE_ENABLED=false  # disables business reference data
# DEMO_SEED_PASSWORD=Azerty1234!?   # demo accounts shared password
