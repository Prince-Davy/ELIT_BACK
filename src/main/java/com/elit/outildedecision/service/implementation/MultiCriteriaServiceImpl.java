package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.StepWeight;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.EscoreException;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.CriteriaModel;
import com.elit.outildedecision.model.CriteriaScoreModel;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.model.MultiCriteriaAnalysisScoreModel;
import com.elit.outildedecision.model.MultiCriteriaStepScoreModel;
import com.elit.outildedecision.model.MulticriteriaAnalyseResponseModel;
import com.elit.outildedecision.repository.CriteriaRepository;
import com.elit.outildedecision.repository.FormQuestionRepository;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.StepWeightRepo;
import com.elit.outildedecision.repository.UserRepository;
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

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link MultiCriteriaAnalysisService} interface.
 * Provides methods to perform multi-criteria analysis on a given project.
 */
@Service
public class MultiCriteriaServiceImpl implements MultiCriteriaAnalysisService {
  /**
   * The maximum allowed value for evaluation.
   */
  @Value("${max-allowed-evaluation}")
  private int maxAllowedEvaluationValue;

  /**
   * Repository for accessing multi-criteria analysis data.
   */
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;

  /**
   * Repository for accessing form step data.
   */
  private final FormStepRepo formStepRepo;

  /**
   * Repository for accessing form question data.
   */
  private final FormQuestionRepository formQuestionRepository;

  /**
   * Repository for accessing criteria data.
   */
  private final CriteriaRepository criteriaRepository;

  /**
   * Repository for accessing step weight data.
   */
  private final StepWeightRepo stepWeightRepo;

  /**
   * Repository for accessing user data.
   */
  private final UserRepository userRepository;

  /**
   * Repository for accessing project user permission data.
   */
  private final ProjectUserPermissionRepository projectUserPermissionRepository;

  /**
   * Repository for accessing project data.
   */
  private final ProjectRepository projectRepository;

  /**
   * The JSON file containing questions.
   */
  @Value("${file.questions-file}")
  private String questionsJsonFile;
  /**
   * The value of a quarter.
   */
  private static final float QUARTER = 0.25f;
  /**
   * The value of a half.
   */
  private static final float HALF = 0.5f;

  /**
   * Constructor for the {@link MultiCriteriaServiceImpl} class.
   * it's for dependency injection.
   *
   * @param multiCriteriaAnalysisRepository the repository for multi-criteria analysis entities
   * @param formStepRepo                    the repository for form step entities
   * @param formQuestionRepository          the repository for form question entities
   * @param criteriaRepository              the repository for criteria entities
   * @param stepWeightRepo                  the repository for step weight entities
   * @param userRepository                  the repository for user entities
   * @param projectUserPermissionRepository the repository for project user permission entities
   * @param projectRepository               the repository for project entities
   */
  public MultiCriteriaServiceImpl(
    final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
    final FormStepRepo formStepRepo,
    final FormQuestionRepository formQuestionRepository,
    final CriteriaRepository criteriaRepository,
    final StepWeightRepo stepWeightRepo,
    final UserRepository userRepository,
    final ProjectUserPermissionRepository projectUserPermissionRepository,
    final ProjectRepository projectRepository) {
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
   * Create a new multicriteria analysis form for a project.
   *
   * @param projectId the id of the project to create the form for.
   * @return a ResponseEntity with a success message if the form was created successfully, or a
   * conflict status if there is already an ongoing analysis for the project.
   * @throws IOException if there is an error reading the JSON file.
   * @throws org.json.simple.parser.ParseException if there is an error parsing the JSON file.
   */
  @Override
  public ResponseEntity<String> createMulticriteriaAnalysisForm(final Long projectId)
    throws IOException, org.json.simple.parser.ParseException {
    Project project =
      projectRepository
        .findById(projectId)
        .orElseThrow(() -> new RuntimeException("Ce projet n'existe pas"));
    MultiCriteriaAnalysis existingMultiCriteriaAnalysis =
      multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(projectId);
    if (Objects.nonNull(existingMultiCriteriaAnalysis) && !existingMultiCriteriaAnalysis.getIsFinished()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
        .body("Une Analyse multicritère est déjà en cours pour ce projet");
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

    FileReader reader = new FileReader(questionsJsonFile);

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
        (criterias.size() == 2) ? List.of(HALF, HALF) : List.of(HALF, QUARTER, QUARTER);

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
  /**
   * Retrieves the latest multicriteria analysis for a given project and returns it as a
   * {@link MulticriteriaAnalyseResponseModel}.
   *
   * @param projectId the ID of the project to retrieve the multicriteria analysis for.
   * @return a ResponseEntity containing the latest multicriteria analysis as a
   * {@link MulticriteriaAnalyseResponseModel} if found, or an error message with an HTTP status
   * code otherwise.
   * @throws ProjectNotFoundException if no project with the given ID exists in the database.
   */

  @Override
  public ResponseEntity<Object> getMulticriteriaAnalysis(final Long projectId) throws ProjectNotFoundException {
    Project project =
      projectRepository
        .findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas"));

    List<MultiCriteriaAnalysis> multiCriteriaAnalysisList = project.getMultiCriteriaAnalysisList();

    if (multiCriteriaAnalysisList.isEmpty()) {
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
  /**
   * Update a MultiCriteriaAnalysis.
   *
   * @param projectId the id of the project
   * @param multiCriteriaAnalysisStep the step of the MultiCriteriaAnalysis to update
   * @return a ResponseEntity with a success message
   * @throws RuntimeException if the project or the step do not exist
   */
  @Override
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<String> updateMultiCriteriaAnalysis(
    final Long projectId, final FormStep multiCriteriaAnalysisStep) {
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
  /**
   * Updates a multi-criteria question's answer text and criteria values based on the provided {@link FormQuestionModel}.
   *
   * @param formQuestionModel the {@link FormQuestionModel} containing the updated answer text and criteria values
   * @return a {@link ResponseEntity} containing a success message
   * @throws MulticriteriaNotFoundException if the multi-criteria question with the provided ID is not found
   */
  @Override
  public ResponseEntity<String> updateMultiCriteriaQuestion(final FormQuestionModel formQuestionModel)
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
  /**
   * Calculates the multi-criteria analysis score for the specified project.
   *
   * @param projectId the ID of the project to calculate the score for
   * @return a ResponseEntity containing the calculated score in a MultiCriteriaAnalysisScoreModel object
   * @throws QuestionsNotFoundException if the questions for the multi-criteria analysis cannot be found
   * @throws IOException if there is an I/O exception while calculating the score
   * @throws EscoreException if there is an error while calculating the E-score
   */
  @Override
  @Transactional
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<MultiCriteriaAnalysisScoreModel> calculateMultiCriteriaAnalysisScore(final Long projectId)
    throws QuestionsNotFoundException, IOException, EscoreException {
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
  /**
   * Checks whether the current user has a specific permission for a project.
   *
   * @param projectId      the ID of the project to check
   * @param permissionName the name of the permission to check
   * @return true if the user has the specified permission, false otherwise
   */
  private boolean checkPermissions(final Long projectId, final String permissionName) {
    String userEmail =
      (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    ProjectUserPermission projectUserPermission =
      projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());
    return Objects.nonNull(projectUserPermission)
      && permissionName.equals(projectUserPermission.getPermission());
  }
  /**
   * Increases the iteration number of a MultiCriteriaAnalysis.
   *
   * @param analyseId the ID of the MultiCriteriaAnalysis to update
   * @return a ResponseEntity with a success message if the update was successful
   * @throws MulticriteriaNotFoundException if the MultiCriteriaAnalysis with the given ID does not exist
   * @throws UserNotAuthorizedException if the user does not have the necessary permissions to update the iteration number
   */
  @Override
  public ResponseEntity<String> increaseMulticriteriaIteration(final Long analyseId)
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

  /**
   * Iterates over the given list of FormStep objects, calculates the eScore and criteria scores for
   * each step, and saves the updated scores to the corresponding FormStep object in the database.
   * Finally, populates a MultiCriteriaAnalysisScoreModel object with the calculated scores for each
   * step.
   *
   * @param multiCriteriaAnalysisFormSteps the list of FormStep objects to iterate over
   * @param multiCriteriaAnalysisScore the MultiCriteriaAnalysisScoreModel object to populate with
   *     calculated scores
   * @throws QuestionsNotFoundException if a required question is not found in the database
   * @throws EscoreException if an error occurs while calculating the eScore
   */
  private void iterateOverSteps(
    final List<FormStep> multiCriteriaAnalysisFormSteps,
    final MultiCriteriaAnalysisScoreModel multiCriteriaAnalysisScore)
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
  /**
   * Calculates the e-score for a given form step in a multi-criteria analysis.
   *
   * @param formStep The form step for which to calculate the e-score.
   * @return The calculated e-score.
   * @throws QuestionsNotFoundException If the questions for the form step cannot be found.
   * @throws EscoreException If the denominator of the e-score calculation is 0.
   */
  private float calculateMultiCriteriaStepEscore(final FormStep formStep)
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
  /**
   * Calculates the sum of the criteria values for the specified criteria in the given form step.
   *
   * @param criteria the criteria to calculate the sum for
   * @param formStep the form step to search for the criteria in
   * @return the sum of the criteria values for the specified criteria
   */
  private float iterateOverQuestionAndCalculateCriteriaSum(final Criteria criteria, final FormStep formStep) {
    List<Criteria> criteriaList =
      criteriaRepository.getCriteriaByStepIdAndCriteriaName(
        formStep.getStepId(), criteria.getCriteriaName());
    int criteriaSum = 0;
    for (Criteria cr : criteriaList) {
      criteriaSum += cr.getCriteriaValue();
    }
    return criteriaSum;
  }
  /**
   * Calculates the maximum possible value for the specified criteria in the given form step.
   *
   * @param criteria the criteria to calculate the maximum value for
   * @param formStep the form step to search for the criteria in
   * @return the maximum possible value for the specified criteria
   */
  private float iterateOverQuestionAndCalculateAMax(final Criteria criteria, final FormStep formStep) {
    List<Criteria> criteriaList =
      criteriaRepository.getCriteriaByStepIdAndCriteriaName(
        formStep.getStepId(), criteria.getCriteriaName());
    return (float) maxAllowedEvaluationValue * criteriaList.size();
  }
  /**
   * Calculates the score for each criteria in the given form step.
   *
   * @param formStep the form step to calculate the criteria scores for
   * @return a list of criteria score models containing the score for each criteria in the form step
   * @throws QuestionsNotFoundException if there are no questions in the form step
   */
  private List<CriteriaScoreModel> calculteCriteriaScore(final FormStep formStep)
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
