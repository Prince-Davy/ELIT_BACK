package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.exception.AnswerIaNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.model.RecommandationItemModel;
import com.elit.outildedecision.model.RecommandationKiviatModel;
import com.elit.outildedecision.model.ResultAiModel;
import com.elit.outildedecision.repository.AnswerIaRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ResultIaRepository;
import com.elit.outildedecision.service.RecommandationService;
import com.elit.outildedecision.service.ResultIaService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.transaction.Transactional;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;

import static com.elit.outildedecision.service.implementation.ProjectServiceImpl.PROJECT_NOT_FOUND;

@Service
public class ResultIaServiceImpl implements ResultIaService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResultIaServiceImpl.class);

  private final ResultIaRepository resultIaRepository;
  private final ProjectRepository projectRepository;
  private final RecommandationService recommandationService;
  private final AnswerIaRepository answerIaRepository;

  @Value("${iaApi.value-url}")
  private String urlApiAI;

  /**
   * ResultIa service implements.
   *
   * @param resultIaRepository resultIaRepository
   * @param projectRepository  projectRepository
   * @param recommandationService reccommandationService
   * @param answerIaRepository answerIaRepository
   */
  public ResultIaServiceImpl(final ResultIaRepository resultIaRepository, final ProjectRepository projectRepository,
                             final RecommandationService recommandationService, final AnswerIaRepository answerIaRepository) {
    this.resultIaRepository = resultIaRepository;
    this.projectRepository = projectRepository;
    this.recommandationService = recommandationService;
    this.answerIaRepository = answerIaRepository;
  }

  /**
   * Send data to ia keep the result and then save response ia on DB.
   *
   * @param projectId project id
   * @param answers   all answers
   * @return ResponseEntity<Object>
   */
  @Override
  @Transactional
  public ResponseEntity<Object> generateAndSaveResult(final long projectId, final List<AnswerIa> answers) {
    String iaResponse;
    try {
      // TODO : this will disable all SSL validation rendering HTTPS totally useless !
      // This was done to short-circuit bad certificate problem  when communicating with
      // pod elit-pmm-pred-integ and while logs weren't available
      TrustManager[] trustManagers = new TrustManager[]{
        new X509TrustManager() {
          @Override
          public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
          }
          @Override
          public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
          }
          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
        }
      };

      // Initialiser le contexte SSL pour le certificat https
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustManagers, null);

      // Configurer HttpsURLConnection pour utiliser le contexte SSL
      URL url = new URL(urlApiAI);
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.setSSLSocketFactory(sslContext.getSocketFactory());
      // Configurer la requête
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");
      connection.setDoOutput(true);
      // Convertir en JSON
      Gson gson = new Gson();
      String jsonPayload = gson.toJson(answers);
      // Envoyer les données à l'api de l'IA
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Error occurred while sending payload to AI Api : " + e.getMessage());
      }
      // Lire la réponse
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
        try (var reader = new java.io.BufferedReader(
          new java.io.InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
          StringBuilder response = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            response.append(line.trim());
          }
          // Récuperer la réponse de l'IA en JSON pour l'exploiter dans ResultAi
          JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
          String predictedMethod = jsonObject.get("predicted_method").getAsString();
          double predictedProba = jsonObject.get("predicted_proba").getAsDouble();

          // Créer une instance de ResultAi et définir les valeurs
          Project project = projectRepository.getById(projectId);
          ResultAi resultAi = new ResultAi();
          resultAi.setProject(project);
          resultAi.setPredictedMethod(predictedMethod);
          resultAi.setPredictedProba(predictedProba);

          // Vérifier si le résultat existe déjà pour ce projet avant de save
          Optional<ResultAi> existingResultIa = resultIaRepository.findByProject(project);
          if (existingResultIa.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Le résultat de l'analyse IA existe déjà pour ce projet.");
          } else {
            resultIaRepository.save(resultAi);
          }
          iaResponse = response.toString();
        }
      } else {
        return ResponseEntity.internalServerError().body("IA Api Request failed with response code: " + responseCode);
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return ResponseEntity.badRequest().body("An unexpected error occurred: " + e.getMessage());
    }
    return ResponseEntity.ok("AI prediction" + "\n" + iaResponse);
  }

  /**
   * Get result analysis AI from project.
   *
   * @param projectId project id
   * @return ResultAiModel
   * @throws ProjectNotFoundException  if project not found
   * @throws ResponseNotFoundException if project doesn't have an analysis IA result
   */
  @Override
  public ResultAiModel getResultIa(final long projectId) throws ProjectNotFoundException, ResponseNotFoundException {
    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));
    Optional<ResultAi> result = Optional.ofNullable(resultIaRepository.findByProject(project)
      .orElseThrow(() -> new ResponseNotFoundException("Analyse AI result not found for project ID: " + projectId)));
    ResultAiModel resultAnalyseIa = new ResultAiModel();
    resultAnalyseIa.setId(result.get().getId());
    resultAnalyseIa.setProjectId(result.get().getId());
    resultAnalyseIa.setPredictedProba(result.get().getPredictedProba());
    resultAnalyseIa.setPredictedMethod(result.get().getPredictedMethod());
    resultAnalyseIa.setProjectId(result.get().getProject().getProjectId());

    return resultAnalyseIa;
  }

  /**
   * Delete result of analysis AI by id.
   *
   * @param resultIaId id of result AI
   * @return ResponseEntity<String>
   * @throws ResponseNotFoundException if analyse AI result not found
   */
  @Override
  @Transactional
  public ResponseEntity<String> deleteResultAnalysisAI(final long resultIaId) throws ResponseNotFoundException {
    Optional<ResultAi> result = Optional.ofNullable(resultIaRepository.findById(resultIaId)
      .orElseThrow(() -> new ResponseNotFoundException("Analyse AI result not found for ID: " + resultIaId)));
    if (result.isPresent()) {
      resultIaRepository.deleteById(resultIaId);
    }
    return ResponseEntity.ok().body("Le résultat de l'analyse IA pour ce projet a bien été supprimé");
  }

  /**
   * Gets the mapping for the given projectid, assuming the user has completed AI anlysis survey and AI responded
   * successfully.
   *
   * @param projectId the project id
   * @return the mapping
   */
  @Override
  public ResponseEntity<List<RecommandationItemModel>> getMapping(final String projectId)
    throws AnswerIaNotFoundException, ResultAiNotFoundException {
    Long projectID = Long.valueOf(projectId);

    List<AnswerIa> answerIas = answerIaRepository.findByProject_ProjectId(projectID);
    if (answerIas == null || answerIas.isEmpty()) {
      throw new AnswerIaNotFoundException("No answer ai found for this projectId");
    }
    ResultAi resultAi = resultIaRepository.findByProject_ProjectId(projectID)
      .orElseThrow(() -> new ResultAiNotFoundException("No result ai found for this projectId"));

    return recommandationService.getMappings(answerIas, resultAi);
  }

  /**
   *
   * @param projectId the project id
   * @return the mapping
   */
  @Override
  public ResponseEntity<RecommandationKiviatModel> getKiviatProjectValues(final String projectId)
    throws ResultAiNotFoundException {
    Long projectID = Long.valueOf(projectId);

    ResultAi resultAi = resultIaRepository.findByProject_ProjectId(projectID)
      .orElseThrow(() -> new ResultAiNotFoundException("No result ai found for this projectId"));

    return recommandationService.getKiviatProjectValues(resultAi);
  }

}
