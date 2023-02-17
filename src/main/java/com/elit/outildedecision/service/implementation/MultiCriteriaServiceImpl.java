package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.CriteriaModel;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.model.MulticriteriaAnalyseResponseModel;
import com.elit.outildedecision.exception.EscoreException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.model.*;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.util.ProjectPermession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MultiCriteriaServiceImpl implements MultiCriteriaAnalysisService {

  @Value("${max-allowed-evaluation}")
  private static final int MAX_ALLOWED_EVALUATION_VALUE = 5;
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  private final FormStepRepo formStepRepo;
  private final FormQuestionRepository formQuestionRepository;
  private final CriteriaRepository criteriaRepository;
  private final StepWeightRepo stepWeightRepo;
  private final UserRepository userRepository;
  private final ProjectUserPermissionRepository projectUserPermissionRepository;
  private final ProjectRepository projectRepository;
  @Value("${file.questions-file}")
  private String JSON_FILE;
  @Value("${file.upload-dir}")
  private String UPLOAD_DIR;

  public MultiCriteriaServiceImpl(
      MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
      FormStepRepo formStepRepo,
      FormQuestionRepository formQuestionRepository,
      CriteriaRepository criteriaRepository,
      StepWeightRepo stepWeightRepo,
      ReportRepository reportRepository,
      UserRepository userRepository,
      ProjectUserPermissionRepository projectUserPermissionRepository,
      ProjectRepository projectRepository) {
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.formStepRepo = formStepRepo;
    this.formQuestionRepository = formQuestionRepository;
    this.criteriaRepository = criteriaRepository;
    this.stepWeightRepo = stepWeightRepo;
    this.userRepository = userRepository;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
    this.projectRepository = projectRepository;
  }

  /**
   * @param projectId
   * @return ResponseEntity<String>
   * @throws IOException
   * @throws org.json.simple.parser.ParseException
   */
  @Override
  public ResponseEntity<String> createMulticriteriaAnalysisForm(Long projectId)
      throws IOException, org.json.simple.parser.ParseException {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new RuntimeException("Ce projet n'existe pas"));
    MultiCriteriaAnalysis existingMultiCriteriaAnalysis =
        multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(projectId);
    if (Objects.nonNull(existingMultiCriteriaAnalysis)) {
      if (!existingMultiCriteriaAnalysis.getIsFinished()) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("Une Analyse multicritère est déjà en cours pour ce projet");
      }
    }
    MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setMultiCriteriaAnalysisIteration(
        (Objects.nonNull(existingMultiCriteriaAnalysis))
            ? existingMultiCriteriaAnalysis.getMultiCriteriaAnalysisIteration() + 1
            : 1);
    multiCriteriaAnalysis.setIsFinished(false);
    multiCriteriaAnalysis.setProject(project);
    MultiCriteriaAnalysis persistedAnalysis =
        multiCriteriaAnalysisRepository.save(multiCriteriaAnalysis);

    FileReader reader = new FileReader(JSON_FILE);

    JSONParser jsonParser = new JSONParser();
    JSONObject multiCriteriaJson = (JSONObject) jsonParser.parse(reader);
    for (Object key : multiCriteriaJson.keySet()) {
      JSONObject step = (JSONObject) multiCriteriaJson.get(key);

      FormStep persistedStep =
          formStepRepo.save(
              FormStep.builder()
                  .stepName(key.toString())
                  .stepRef((Long) step.get("stepRef"))
                  .multiCriteriaAnalysis(persistedAnalysis)
                  .build());
      JSONObject questions = (JSONObject) step.get("Questions");
      JSONArray criterias = (JSONArray) step.get("Criterias");

      List<Float> initialWeights =
          (criterias.size() == 2) ? List.of(0.5f, 0.5f) : List.of(0.5f, 0.25f, 0.25f);

      for (int i = 0; i < criterias.size(); i++) {
        JSONObject criteriaData = (JSONObject) criterias.get(i);
        stepWeightRepo.save(
            StepWeight.builder()
                .criteriaName(criteriaData.get("name").toString())
                .criteriaDescription(criteriaData.get("description").toString())
                .weightValue(initialWeights.get(i))
                .formStep(persistedStep)
                .build());
      }

      for (Object questionKey : questions.keySet()) {
        FormQuestion persistedFormQuestion =
            formQuestionRepository.save(
                FormQuestion.builder()
                    .formStep(persistedStep)
                    .questionText(questions.get(questionKey).toString())
                    .questionRef(questionKey.toString())
                    .build());

        for (int i = 0; i < criterias.size(); i++) {
          JSONObject criteriaData = (JSONObject) criterias.get(i);
          criteriaRepository.save(
              Criteria.builder()
                  .criteriaName(criteriaData.get("name").toString())
                  .formQuestion(persistedFormQuestion)
                  .build());
        }
      }
    }
    return ResponseEntity.ok().body("Votre Analyse multicritère est crée avec succés");
  }

  @Override
  public ResponseEntity<Object> getMulticriteriaAnalysis(Long projectId) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new RuntimeException("Ce projet n'existe pas"));

    List<MultiCriteriaAnalysis> multiCriteriaAnalysisList = project.getMultiCriteriaAnalysisList();

    if (multiCriteriaAnalysisList.size() == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Aucune Analyse Multicritère Trouvée");

    } else {

      MultiCriteriaAnalysis lastMultiCriteriaAnalysis = multiCriteriaAnalysisList.get(0);
      MulticriteriaAnalyseResponseModel result =
          new MulticriteriaAnalyseResponseModel(
              lastMultiCriteriaAnalysis.getMultiCriteriaAnalysisId(),
              lastMultiCriteriaAnalysis.getCreatedAt(),
              lastMultiCriteriaAnalysis.getUpdatedAt(),
              lastMultiCriteriaAnalysis.getMultiCriteriaAnalysisIteration(),
              lastMultiCriteriaAnalysis.getIsFinished(),
              lastMultiCriteriaAnalysis.getFormSteps(),
              null);

      return ResponseEntity.ok().body(result);
    }
  }

  @Override
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> updateMultiCriteriaAnalysis(
      Long projectId, FormStep multiCriteriaAnalysisStep) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new RuntimeException("Ce projet n'existe pas"));
    FormStep formStepToUpdate =
        formStepRepo
            .findById(multiCriteriaAnalysisStep.getStepId())
            .orElseThrow(() -> new RuntimeException("cette étape n'existe pas"));
    Set<FormQuestion> formQuestionListToUpdate = formStepToUpdate.getFormQuestions();
    Set<StepWeight> stepWeightsToUpdate = formStepToUpdate.getStepWeights();
    Set<FormQuestion> formQuestions = multiCriteriaAnalysisStep.getFormQuestions();
    Set<StepWeight> stepWeights = multiCriteriaAnalysisStep.getStepWeights();
    for (StepWeight weight : stepWeightsToUpdate) {
      StepWeight stepWeight =
          stepWeights.stream()
              .filter(sw -> sw.getCriteriaName().equals(weight.getCriteriaName()))
              .collect(Collectors.toList())
              .get(0);
      weight.setWeightValue(stepWeight.getWeightValue());
      stepWeightRepo.save(weight);
    }
    for (FormQuestion formQuestion : formQuestionListToUpdate) {
      Set<Criteria> criteriaList = formQuestion.getCriterias();
      FormQuestion formquestionFromClient =
          formQuestions.stream()
              .filter(q -> q.getQuestionRef().equals(formQuestion.getQuestionRef()))
              .collect(Collectors.toList())
              .get(0);
      if (Objects.nonNull(formquestionFromClient.getAnswerText())) {
        formQuestion.setAnswerText(formquestionFromClient.getAnswerText());
      }
      for (Criteria criteriaToUpdate : criteriaList) {
        Criteria criteriaFromClient =
            formquestionFromClient.getCriterias().stream()
                .filter(
                    criteria ->
                        criteria.getCriteriaName().equals(criteriaToUpdate.getCriteriaName()))
                .collect(Collectors.toList())
                .get(0);
        if (criteriaFromClient.getCriteriaValue() != 0) {
          criteriaToUpdate.setCriteriaValue(criteriaFromClient.getCriteriaValue());
        }
        criteriaRepository.save(criteriaToUpdate);
      }
      formQuestionRepository.save(formQuestion);
    }
    return ResponseEntity.ok().body("Votre Modification sont enregistrées avec succés");
  }

  @Override
  public ResponseEntity<String> updateMultiCriteriaQuestion(FormQuestionModel formQuestionModel)
      throws MulticriteriaNotFoundException {
    FormQuestion formQuestion =
        formQuestionRepository
            .findById(formQuestionModel.getQuestionId())
            .orElseThrow(() -> new MulticriteriaNotFoundException("Ce fichier n'existe pas !"));
    formQuestion.setAnswerText(formQuestionModel.getAnswerText());
    formQuestionRepository.save(formQuestion);
    for (CriteriaModel criteriaModel : formQuestionModel.getCriterias()) {
      Criteria criteria = criteriaRepository.getById(criteriaModel.getCriteriaId());
      criteria.setCriteriaValue(criteriaModel.getCriteriaValue());
      criteriaRepository.save(criteria);
    }
    return ResponseEntity.ok().body("Votre Modification sont enregistrées avec succés");
  }

  @Override
  @Transactional
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<MultiCriteriaAnalysisScoreModel> calculateMultiCriteriaAnalysisScore(
      Long projectId) throws QuestionsNotFoundException, IOException, EscoreException {
    MultiCriteriaAnalysis multiCriteriaAnalysis =
        multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(projectId);
    List<FormStep> multiCriteriaAnalysisFormSteps = multiCriteriaAnalysis.getFormSteps();
    MultiCriteriaAnalysisScoreModel multiCriteriaAnalysisScore =
        new MultiCriteriaAnalysisScoreModel();
    this.iterateOverSteps(multiCriteriaAnalysisFormSteps, multiCriteriaAnalysisScore);
    multiCriteriaAnalysis.setIsFinished(true);
    multiCriteriaAnalysisRepository.save(multiCriteriaAnalysis);
    return ResponseEntity.ok().body(multiCriteriaAnalysisScore);
  }

  private boolean checkPermissions(Long projectId, String permissionName) {
    String userEmail =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());
    return Objects.nonNull(projectUserPermission)
        && permissionName.equals(projectUserPermission.getPermission());
  }

  @Override
  public ResponseEntity<String> increaseMulticriteriaIteration(Long analyseId)
      throws MulticriteriaNotFoundException, UserNotAuthorizedException {
    MultiCriteriaAnalysis multiCriteriaAnalysis =
        multiCriteriaAnalysisRepository
            .findById(analyseId)
            .orElseThrow(
                () ->
                    new MulticriteriaNotFoundException(
                        "Cette analyse multicritères n'existe pas."));
    Project project = multiCriteriaAnalysis.getProject();
    if (checkPermissions(project.getProjectId(), ProjectPermession.CDP.name())) {
      multiCriteriaAnalysis.setMultiCriteriaAnalysisIteration(
          multiCriteriaAnalysis.getMultiCriteriaAnalysisIteration() + 1);
      multiCriteriaAnalysisRepository.save(multiCriteriaAnalysis);
    } else {
      throw new UserNotAuthorizedException(
          "Vous n'êtes pas autorisé à changer l'itération de cette analyse multicritères.");
    }

    return ResponseEntity.ok().body("L'analyse multicritères a bien changé d'itération.");
  }

  @Transactional
  private void iterateOverSteps(
      List<FormStep> multiCriteriaAnalysisFormSteps,
      MultiCriteriaAnalysisScoreModel multiCriteriaAnalysisScore)
      throws QuestionsNotFoundException, EscoreException {
    for (FormStep formStep : multiCriteriaAnalysisFormSteps) {
      MultiCriteriaStepScoreModel multiCriteriaStepScore = new MultiCriteriaStepScoreModel();
      float eScore = calculateMultiCriteriaStepEscore(formStep);
      multiCriteriaStepScore.setStepName(formStep.getStepName());
      multiCriteriaStepScore.setEScore(eScore);
      multiCriteriaStepScore.setCriteriaScores(calculteCriteriaScore(formStep));
      formStep.setEScore(eScore);
      formStepRepo.save(formStep);
      List<MultiCriteriaStepScoreModel> stepscores =
          multiCriteriaAnalysisScore.getMultiCriteriaStepScores();
      if (Objects.isNull(stepscores)) {
        List<MultiCriteriaStepScoreModel> stepscore = new ArrayList<>();
        stepscore.add(multiCriteriaStepScore);
        multiCriteriaAnalysisScore.setMultiCriteriaStepScores(stepscore);
      } else {
        stepscores.add(multiCriteriaStepScore);
        multiCriteriaAnalysisScore.setMultiCriteriaStepScores(stepscores);
      }
    }
  }

  private float calculateMultiCriteriaStepEscore(FormStep formStep)
      throws QuestionsNotFoundException, EscoreException {
    Set<Criteria> criterias =
        formStep.getFormQuestions().stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new QuestionsNotFoundException("Erreur lors de la récupération des questions"))
            .getCriterias();
    float multiCriteriaStepEscoreNumenator = 0;
    float multiCriteriaStepEscoreDenominator = 0;
    for (Criteria criteria : criterias) {
      float criteriaValueSum = this.iterateOverQuestionAndCalculateCriteriaSum(criteria, formStep);
      float criteriaValueAMax = this.iterateOverQuestionAndCalculateAMax(criteria, formStep);
      StepWeight stepWeight =
          stepWeightRepo.findByStepIdAndCriteriaName(
              formStep.getStepId(), criteria.getCriteriaName());
      multiCriteriaStepEscoreNumenator += stepWeight.getWeightValue() * criteriaValueSum;
      multiCriteriaStepEscoreDenominator += stepWeight.getWeightValue() * criteriaValueAMax;
    }
    if (multiCriteriaStepEscoreDenominator == 0) {
      throw new EscoreException("Le dénominateur du e-score ne peux être 0");
    }
    return multiCriteriaStepEscoreNumenator / multiCriteriaStepEscoreDenominator;
  }

  private float iterateOverQuestionAndCalculateCriteriaSum(Criteria criteria, FormStep formStep) {
    List<Criteria> criteriaList =
        criteriaRepository.getCriteriaByStepIdAndCriteriaName(
            formStep.getStepId(), criteria.getCriteriaName());
    int criteriaSum = 0;
    for (Criteria cr : criteriaList) {
      criteriaSum += cr.getCriteriaValue();
    }
    return (float) criteriaSum;
  }

  private float iterateOverQuestionAndCalculateAMax(Criteria criteria, FormStep formStep) {
    List<Criteria> criteriaList =
        criteriaRepository.getCriteriaByStepIdAndCriteriaName(
            formStep.getStepId(), criteria.getCriteriaName());
    return (float) MAX_ALLOWED_EVALUATION_VALUE * criteriaList.size();
  }

  private List<CriteriaScoreModel> calculteCriteriaScore(FormStep formStep)
      throws QuestionsNotFoundException {
    Set<Criteria> criterias =
        formStep.getFormQuestions().stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new QuestionsNotFoundException("Erreur lors de la récupération des questions"))
            .getCriterias();
    int questionSize = formStep.getFormQuestions().size();
    List<CriteriaScoreModel> criteriaScoreModels = new ArrayList<>();
    for (Criteria criteria : criterias) {
      CriteriaScoreModel criteriaScoreModel = new CriteriaScoreModel();
      criteriaScoreModel.setCriteriaName(criteria.getCriteriaName());
      float criteriaScore =
          iterateOverQuestionAndCalculateCriteriaSum(criteria, formStep) / questionSize;
      criteriaScoreModel.setCriteriaScore(criteriaScore);
      criteriaScoreModels.add(criteriaScoreModel);
    }
    return criteriaScoreModels;
  }
}
