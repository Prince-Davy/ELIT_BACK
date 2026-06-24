package com.elit.outildedecision.collaboration.multicriteria.service.implementation;


import com.elit.outildedecision.collaboration.multicriteria.calculate.MultiCriteriaScoreCalculator;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaAnalysisScoreResponse;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepRequest;
import com.elit.outildedecision.collaboration.multicriteria.dto.MultiCriteriaStepScoreResponse;
import com.elit.outildedecision.collaboration.multicriteria.dto.MulticriteriaAnalysisResponse;
import com.elit.outildedecision.collaboration.multicriteria.entity.Criteria;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormQuestion;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.entity.StepWeight;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanScoreResponse;
import com.elit.outildedecision.collaboration.executionplan.service.ExecutionPlanService;
import com.elit.outildedecision.collaboration.predictibility.dto.PredictibilityAnalyseResponseModel;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.exception.EscoreException;
import com.elit.outildedecision.collaboration.multicriteria.exception.FormValidationException;
import com.elit.outildedecision.collaboration.multicriteria.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.exception.StepFormNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.mapper.MulticriteriaMapper;
import com.elit.outildedecision.collaboration.multicriteria.repository.CriteriaRepository;
import com.elit.outildedecision.collaboration.multicriteria.repository.FormQuestionRepository;
import com.elit.outildedecision.collaboration.multicriteria.repository.FormStepRepository;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.collaboration.multicriteria.repository.StepWeightRepository;
import com.elit.outildedecision.collaboration.multicriteria.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.collaboration.multicriteria.validator.MultiCriteriaFormValidator;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.collaboration.multicriteria.dto.CriteriaModel;
import com.elit.outildedecision.collaboration.multicriteria.dto.FormQuestionModel;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.ProjectPermession;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the {@link MultiCriteriaAnalysisService} interface. Provides methods to perform multi-criteria analysis on a given
 * project.
 */
@Service
@Slf4j
public class MultiCriteriaServiceImpl implements MultiCriteriaAnalysisService {
  /**
   * The value of a quarter.
   */
  private static final float QUARTER = 0.25f;
  /**
   * The value of a half.
   */
  private static final float HALF = 0.5f;
  /**
   * Error message used when a multi-criteria analysis cannot be found.
   */
  private static final String MULTICRITERIA_NOT_FOUND_MESSAGE = "Cette analyse multicritères n'existe pas.";
  /**
   * Repository for accessing multi-criteria analysis data.
   */
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  /**
   * Repository for accessing form step data.
   */
  private final FormStepRepository formStepRepository;
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
  private final StepWeightRepository stepWeightRepository;
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
   * Mapper for multi-criteria analysis response DTOs.
   */
  private final MulticriteriaMapper multicriteriaMapper;
  /**
   * Pure calculator of the multi-criteria step score (eScore and per-criteria scores).
   */
  private final MultiCriteriaScoreCalculator multiCriteriaScoreCalculator;
  /**
   * Service providing the execution-plan score embedded in the predictibility analysis response.
   */
  private final ExecutionPlanService executionPlanService;
  /**
   * The maximum allowed value for evaluation.
   */
  @Value("${max-allowed-evaluation}")
  private int maxAllowedEvaluationValue;
  /**
   * The JSON file containing questions.
   */
  @Value("${file.questions-file}")
  private String questionsJsonFile;

  /**
   * The resource loader.
   */
  private final ResourceLoader resourceLoader;

  /**
   * Constructor for the {@link MultiCriteriaServiceImpl} class. it's for dependency injection.
   *
   * @param multiCriteriaAnalysisRepository the repository for multi-criteria analysis entities
   * @param formStepRepository              the repository for form step entities
   * @param formQuestionRepository          the repository for form question entities
   * @param criteriaRepository              the repository for criteria entities
   * @param stepWeightRepository            the repository for step weight entities
   * @param userRepository                  the repository for user entities
   * @param projectUserPermissionRepository the repository for project user permission entities
   * @param projectRepository               the repository for project entities
   * @param multicriteriaMapper             the multi-criteria response mapper
   * @param multiCriteriaScoreCalculator    the multi-criteria step score calculator
   * @param executionPlanService            the execution-plan service for the embedded score
   * @param resourceLoader                  the resources loadaer
   */
  public MultiCriteriaServiceImpl(final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository, final FormStepRepository formStepRepository,
                                  final FormQuestionRepository formQuestionRepository, final CriteriaRepository criteriaRepository, final StepWeightRepository stepWeightRepository,
                                  final UserRepository userRepository, final ProjectUserPermissionRepository projectUserPermissionRepository,
                                  final ProjectRepository projectRepository, final MulticriteriaMapper multicriteriaMapper,
                                  final MultiCriteriaScoreCalculator multiCriteriaScoreCalculator, final ExecutionPlanService executionPlanService,
                                  final ResourceLoader resourceLoader) {
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.formStepRepository = formStepRepository;
    this.formQuestionRepository = formQuestionRepository;
    this.criteriaRepository = criteriaRepository;
    this.stepWeightRepository = stepWeightRepository;
    this.userRepository = userRepository;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
    this.projectRepository = projectRepository;
    this.multicriteriaMapper = multicriteriaMapper;
    this.multiCriteriaScoreCalculator = multiCriteriaScoreCalculator;
    this.executionPlanService = executionPlanService;
    this.resourceLoader = resourceLoader;
  }

  /**
   * Create a new multicriteria analysis form for a project. When an analysis is already in progress
   * for the project the call is a no-op.
   *
   * @param projectId the id of the project to create the form for.
   * @throws IOException    if there is an error reading the JSON file.
   * @throws ParseException if there is an error parsing the JSON file.
   */
  @Override
  public void createMulticriteriaAnalysisForm(final Long projectId)
    throws IOException, ParseException, ProjectNotFoundException {
    Project project = getProject(projectId);
    MultiCriteriaAnalysis existingMultiCriteriaAnalysis = getExistingMultiCriteriaAnalysis(projectId);

    if (isAnalysisInProgress(existingMultiCriteriaAnalysis)) {
      log.warn("A multi-criteria analysis is already in progress for project {}, skipping creation", projectId);
      return;
    }

    MultiCriteriaAnalysis persistedAnalysis = createNewMultiCriteriaAnalysis(project, existingMultiCriteriaAnalysis);
    JSONObject multiCriteriaJson = readQuestionsJsonFile();
    processMultiCriteriaJson(multiCriteriaJson, persistedAnalysis);

    log.info("Created multi-criteria analysis {} for project {}", persistedAnalysis.getMultiCriteriaAnalysisId(), projectId);
  }

  private Project getProject(final Long projectId) throws ProjectNotFoundException {
    return projectRepository
      .findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas"));
  }

  private MultiCriteriaAnalysis getExistingMultiCriteriaAnalysis(final Long projectId) {
    return multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(projectId);
  }

  private boolean isAnalysisInProgress(final MultiCriteriaAnalysis existingMultiCriteriaAnalysis) {
    return Objects.nonNull(existingMultiCriteriaAnalysis) && Boolean.TRUE.equals(!existingMultiCriteriaAnalysis.getIsFinished());
  }

  private MultiCriteriaAnalysis createNewMultiCriteriaAnalysis(final Project project,
                                                               final MultiCriteriaAnalysis existingMultiCriteriaAnalysis) {
    MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setMultiCriteriaAnalysisIteration(
      Objects.nonNull(existingMultiCriteriaAnalysis) ? existingMultiCriteriaAnalysis.getMultiCriteriaAnalysisIteration() + 1 : 1);
    multiCriteriaAnalysis.setIsFinished(false);
    multiCriteriaAnalysis.setProject(project);
    return multiCriteriaAnalysisRepository.save(multiCriteriaAnalysis);
  }

  private JSONObject readQuestionsJsonFile() throws IOException, ParseException {
    try (InputStream resourceAsStream = resourceLoader.getResource(questionsJsonFile).getInputStream()) {
      InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(resourceAsStream), StandardCharsets.UTF_8);
      BufferedReader reader = new BufferedReader(inputStreamReader);
      return (JSONObject) new JSONParser().parse(reader);
    }
  }

  private void processMultiCriteriaJson(final JSONObject multiCriteriaJson, final MultiCriteriaAnalysis persistedAnalysis) {
    Iterator<Map.Entry<String, JSONObject>> multiCreteriaIterator = multiCriteriaJson.entrySet().iterator();

    while (multiCreteriaIterator.hasNext()) {
      Map.Entry<String, JSONObject> entry = multiCreteriaIterator.next();
      JSONObject step = (JSONObject) multiCriteriaJson.get(entry.getKey());

      FormStep persistedStep = saveFormStep(entry, persistedAnalysis);
      JSONObject questions = (JSONObject) step.get("Questions");
      JSONArray criterias = (JSONArray) step.get("Criterias");

      saveStepWeights(criterias, persistedStep);
      saveFormQuestionsAndCriteria(questions, criterias, persistedStep);
    }
  }

  private FormStep saveFormStep(final Map.Entry<String, JSONObject> entry, final MultiCriteriaAnalysis persistedAnalysis) {
    JSONObject step = entry.getValue();
    return formStepRepository.save(
      FormStep.builder().stepName(entry.getKey()).stepRef((Long) step.get("stepRef")).multiCriteriaAnalysis(persistedAnalysis).build());
  }

  private void saveStepWeights(final JSONArray criterias, final FormStep persistedStep) {
    List<Float> initialWeights = (criterias.size() == 2) ? List.of(HALF, HALF) : List.of(HALF, QUARTER, QUARTER);

    for (int i = 0; i < criterias.size(); i++) {
      JSONObject criteriaData = (JSONObject) criterias.get(i);
      stepWeightRepository.save(StepWeight.builder()
        .criteriaName(criteriaData.get("name").toString())
        .criteriaDescription(criteriaData.get("description").toString())
        .weightValue(initialWeights.get(i))
        .formStep(persistedStep)
        .build());
    }
  }

  private void saveFormQuestionsAndCriteria(final JSONObject questions, final JSONArray criterias, final FormStep persistedStep) {
    Iterator<Map.Entry<String, String>> questionIterator = questions.entrySet().iterator();

    while (questionIterator.hasNext()) {
      Map.Entry<String, String> question = questionIterator.next();
      FormQuestion persistedFormQuestion = saveFormQuestion(persistedStep, question);

      for (int i = 0; i < criterias.size(); i++) {
        JSONObject criteriaData = (JSONObject) criterias.get(i);
        boolean skipSave = sortCriteriaQuestion(question, criteriaData);
        if (!skipSave) {
          criteriaRepository.save(
            Criteria.builder().criteriaName(criteriaData.get("name").toString()).formQuestion(persistedFormQuestion).build());
        }
      }
    }
  }

  private boolean sortCriteriaQuestion(final Map.Entry<String, String> question, final JSONObject criteriaData) {
    String questionString = question.toString();
    String criteriaName = criteriaData.get("name").toString();
    return questionString.contains("Spec_1") && criteriaName.contains("Rigueur") || questionString.contains("Unc_2") && criteriaName.contains("Complétude") || questionString.contains("Man_1") && criteriaName.contains("Disponibilité") || questionString.contains("Man_2") && criteriaName.contains("Régularité") || questionString.contains("Man_4") && criteriaName.contains("Régularité");
  }

  private FormQuestion saveFormQuestion(final FormStep persistedStep, final Map.Entry<String, String> question) {
    return formQuestionRepository.save(
      FormQuestion.builder().formStep(persistedStep).questionText(question.getValue()).questionRef(question.getKey()).updateQuestion(false).build());
  }

  /**
   * Retrieves the latest multicriteria analysis for a given project and returns it as a {@link MulticriteriaAnalysisResponse}.
   *
   * @param projectId the ID of the project to retrieve the multicriteria analysis for.
   * @return the latest multicriteria analysis when found, an empty optional otherwise.
   * @throws ProjectNotFoundException if no project with the given ID exists in the database.
   */
  @Override
  @Transactional(readOnly = true)
  public Optional<MulticriteriaAnalysisResponse> getMulticriteriaAnalysis(final Long projectId) throws ProjectNotFoundException {
    Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas"));

    List<MultiCriteriaAnalysis> multiCriteriaAnalysisList = project.getMultiCriteriaAnalysisList();

    if (multiCriteriaAnalysisList.isEmpty()) {
      return Optional.empty();
    }

    MultiCriteriaAnalysis lastMultiCriteriaAnalysis = multiCriteriaAnalysisList.get(0);
    // The mapper resolves the lazy entity graph into DTOs while still inside this read-only
    // transaction, so nothing lazy is left for Jackson to initialise after the session closes.
    return Optional.of(multicriteriaMapper.toResponse(lastMultiCriteriaAnalysis));
  }

  /**
   * Update a MultiCriteriaAnalysis.
   * Validates that all required fields (text answers and evaluation criteria) are properly filled.
   *
   * @param projectId                 the id of the project
   * @param multiCriteriaAnalysisStep the step of the MultiCriteriaAnalysis to update
   * @throws StepFormNotFoundException if the step does not exist
   * @throws FormValidationException if validation fails (missing answers or criteria)
   */
  @Override
  @Transactional
  public void updateMultiCriteriaAnalysis(
    final Long projectId, final MultiCriteriaStepRequest multiCriteriaAnalysisStep) throws StepFormNotFoundException, FormValidationException {
    // Validate the form before proceeding with the update
    MultiCriteriaFormValidator.ValidationResult validationResult = MultiCriteriaFormValidator.validateMultiCriteriaStep(multiCriteriaAnalysisStep);
    if (validationResult.hasErrors()) {
      throw new FormValidationException(validationResult.buildErrorMessage());
    }
    FormStep formStepToUpdate =
      formStepRepository
        .findById(multiCriteriaAnalysisStep.getStepId())
        .orElseThrow(() -> new StepFormNotFoundException("cette étape n'existe pas"));
    Set<FormQuestion> formQuestionListToUpdate = formStepToUpdate.getFormQuestions();
    Set<StepWeight> stepWeightsToUpdate = formStepToUpdate.getStepWeights();
    Set<FormQuestion> formQuestions = multiCriteriaAnalysisStep.getFormQuestions();
    Set<StepWeight> stepWeights = multiCriteriaAnalysisStep.getStepWeights();
    for (StepWeight weight : stepWeightsToUpdate) {
      StepWeight stepWeight = stepWeights.stream()
        .filter(sw -> sw.getCriteriaName().equals(weight.getCriteriaName()))
        .toList()
        .get(0);
      weight.setWeightValue(stepWeight.getWeightValue());
      stepWeightRepository.save(weight);
    }
    for (FormQuestion formQuestion : formQuestionListToUpdate) {
      Set<Criteria> criteriaList = formQuestion.getCriterias();
      FormQuestion formquestionFromClient = formQuestions.stream()
        .filter(q -> q.getQuestionRef().equals(formQuestion.getQuestionRef()))
        .toList()
        .get(0);
      if (Objects.nonNull(formquestionFromClient.getAnswerText())) {
        formQuestion.setAnswerText(formquestionFromClient.getAnswerText());
      }
      for (Criteria criteriaToUpdate : criteriaList) {
        Criteria criteriaFromClient = formquestionFromClient.getCriterias()
          .stream()
          .filter(criteria -> criteria.getCriteriaName().equals(criteriaToUpdate.getCriteriaName()))
          .toList()
          .get(0);
        if (criteriaFromClient.getCriteriaValue() != 0) {
          criteriaToUpdate.setCriteriaValue(criteriaFromClient.getCriteriaValue());
        }
        criteriaRepository.save(criteriaToUpdate);
      }
      formQuestionRepository.save(formQuestion);
    }

    log.info("Updated multi-criteria analysis step {} for project {}", multiCriteriaAnalysisStep.getStepId(), projectId);
  }

  /**
   * Updates a multi-criteria question's answer text and criteria values based on the provided {@link FormQuestionModel}.
   *
   * @param projectId         the id of the project
   * @param formQuestionModel the {@link FormQuestionModel} containing the updated answer text and criteria values
   * @throws MulticriteriaNotFoundException if the question (or its analysis) does not belong to the project
   */
  @Override
  @Transactional
  public void updateMultiCriteriaQuestion(final Long projectId, final FormQuestionModel formQuestionModel)
    throws MulticriteriaNotFoundException, StepFormNotFoundException {

    FormQuestion formQuestion = formQuestionRepository.findById(formQuestionModel.getQuestionId())
      .orElseThrow(() -> new MulticriteriaNotFoundException("Ce fichier n'existe pas !"));

    FormStep formStep = formStepRepository.findById(formQuestionModel.getStepId())
      .orElseThrow(() -> new StepFormNotFoundException("l'étape associé a cette question n'existe pas "));

    if (!Objects.equals(formStep.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new MulticriteriaNotFoundException(MULTICRITERIA_NOT_FOUND_MESSAGE);
    }

    formQuestion.setAnswerText(formQuestionModel.getAnswerText());
    formQuestion.setUpdateQuestion(true);
    formQuestionRepository.save(formQuestion);
    for (CriteriaModel criteriaModel : formQuestionModel.getCriterias()) {
      Criteria criteria = criteriaRepository.getById(criteriaModel.getCriteriaId());
      criteria.setCriteriaValue(criteriaModel.getCriteriaValue());
      criteriaRepository.save(criteria);
    }
    log.info("Updated multi-criteria question {} for project {}", formQuestionModel.getQuestionId(), projectId);
  }

  /**
   * Calculates the multi-criteria analysis score for the specified project.
   *
   * @param projectId the ID of the project to calculate the score for
   * @return the calculated score in a MultiCriteriaAnalysisScoreResponse object
   * @throws QuestionsNotFoundException if the questions for the multi-criteria analysis cannot be found
   * @throws EscoreException            if there is an error while calculating the E-score
   */
  @Override
  @Transactional
  public MultiCriteriaAnalysisScoreResponse calculateMultiCriteriaAnalysisScore(final Long projectId)
    throws QuestionsNotFoundException, EscoreException {
    MultiCriteriaAnalysis multiCriteriaAnalysis = multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(projectId);
    if (multiCriteriaAnalysis == null) {
      throw new MulticriteriaNotFoundException("Aucune analyse multicritère pour ce projet.");
    }
    List<FormStep> multiCriteriaAnalysisFormSteps = multiCriteriaAnalysis.getFormSteps();
    MultiCriteriaAnalysisScoreResponse multiCriteriaAnalysisScore = new MultiCriteriaAnalysisScoreResponse();
    this.iterateOverSteps(multiCriteriaAnalysisFormSteps, multiCriteriaAnalysisScore);
    log.info("Calculated multi-criteria analysis score for analysis {} of project {}",
      multiCriteriaAnalysis.getMultiCriteriaAnalysisId(), projectId);
    return multiCriteriaAnalysisScore;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void markMultiCriteriaAnalysisAsFinished(final Long projectId, final Long analyseId)
    throws MulticriteriaNotFoundException {
    MultiCriteriaAnalysis multiCriteriaAnalysis = multiCriteriaAnalysisRepository.findById(analyseId)
      .orElseThrow(() -> new MulticriteriaNotFoundException(MULTICRITERIA_NOT_FOUND_MESSAGE));
    if (!Objects.equals(multiCriteriaAnalysis.getProject().getProjectId(), projectId)) {
      throw new MulticriteriaNotFoundException(MULTICRITERIA_NOT_FOUND_MESSAGE);
    }
    multiCriteriaAnalysis.setIsFinished(true);
    multiCriteriaAnalysisRepository.save(multiCriteriaAnalysis);
    log.info("Marked multi-criteria analysis {} as finished for project {}", analyseId, projectId);
  }

  /**
   * Checks whether the current user has a specific permission for a project.
   *
   * @param projectId      the ID of the project to check
   * @param permissionName the name of the permission to check
   * @return true if the user has the specified permission, false otherwise
   */
  private boolean checkPermissions(final Long projectId, final String permissionName) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userDetails.getUsername());

    ProjectUserPermission projectUserPermission = projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());
    return Objects.nonNull(projectUserPermission) && permissionName.equals(projectUserPermission.getPermission());
  }

  /**
   * Increases the iteration number of a MultiCriteriaAnalysis.
   *
   * @param analyseId the ID of the MultiCriteriaAnalysis to update
   * @throws MulticriteriaNotFoundException if the MultiCriteriaAnalysis with the given ID does not exist
   * @throws UserNotAuthorizedException     if the user does not have the necessary permissions to update the iteration number
   */
  @Override
  public void increaseMulticriteriaIteration(final Long analyseId)
    throws MulticriteriaNotFoundException, UserNotAuthorizedException {
    MultiCriteriaAnalysis multiCriteriaAnalysis =
      multiCriteriaAnalysisRepository
        .findById(analyseId)
        .orElseThrow(
          () ->
            new MulticriteriaNotFoundException(MULTICRITERIA_NOT_FOUND_MESSAGE));
    Project project = multiCriteriaAnalysis.getProject();
    if (checkPermissions(project.getProjectId(), ProjectPermession.CDP.name())) {
      multiCriteriaAnalysis.setMultiCriteriaAnalysisIteration(
        multiCriteriaAnalysis.getMultiCriteriaAnalysisIteration() + 1);
      multiCriteriaAnalysisRepository.save(multiCriteriaAnalysis);
      log.info("Increased iteration of multi-criteria analysis {} for project {}", analyseId, project.getProjectId());
    } else {
      throw new UserNotAuthorizedException(
        "Vous n'êtes pas autorisé à changer l'itération de cette analyse multicritères.");
    }
  }

  /**
   * Increases the iteration number of a MultiCriteriaAnalysis.
   *
   * @param projectId the ID of project
   * @param analyseId the ID of the MultiCriteriaAnalysis to update
   * @throws MulticriteriaNotFoundException if the analysis does not exist or does not belong to the project
   */
  @Override
  public void increaseMulticriteriaIteration(final Long projectId, final Long analyseId) throws MulticriteriaNotFoundException {
    MultiCriteriaAnalysis multiCriteriaAnalysis = multiCriteriaAnalysisRepository.findById(analyseId)
      .orElseThrow(() -> new MulticriteriaNotFoundException(MULTICRITERIA_NOT_FOUND_MESSAGE));
    if (!Objects.equals(multiCriteriaAnalysis.getProject().getProjectId(), projectId)) {
      throw new MulticriteriaNotFoundException(MULTICRITERIA_NOT_FOUND_MESSAGE);
    }
    multiCriteriaAnalysis.setMultiCriteriaAnalysisIteration(multiCriteriaAnalysis.getMultiCriteriaAnalysisIteration() + 1);
    multiCriteriaAnalysisRepository.save(multiCriteriaAnalysis);
    log.info("Increased iteration of multi-criteria analysis {} for project {}", analyseId, projectId);
  }

  /**
   * Iterates over the given list of FormStep objects, delegates the eScore and criteria-score
   * computation to {@link MultiCriteriaScoreCalculator}, persists the eScore on each step and
   * populates the {@link MultiCriteriaAnalysisScoreResponse} with the per-step scores.
   *
   * @param multiCriteriaAnalysisFormSteps the list of FormStep objects to iterate over
   * @param multiCriteriaAnalysisScore     the MultiCriteriaAnalysisScoreResponse object to populate with calculated scores
   * @throws QuestionsNotFoundException if a required question is not found
   * @throws EscoreException            if an error occurs while calculating the eScore
   */
  private void iterateOverSteps(final List<FormStep> multiCriteriaAnalysisFormSteps,
                                final MultiCriteriaAnalysisScoreResponse multiCriteriaAnalysisScore) throws QuestionsNotFoundException, EscoreException {
    List<MultiCriteriaStepScoreResponse> stepScores = new ArrayList<>();
    for (FormStep formStep : multiCriteriaAnalysisFormSteps) {
      MultiCriteriaStepScoreResponse stepScore =
        multiCriteriaScoreCalculator.computeStepScore(formStep, maxAllowedEvaluationValue);
      formStep.setEScore(stepScore.getEScore());
      formStepRepository.save(formStep);
      stepScores.add(stepScore);
    }
    if (!stepScores.isEmpty()) {
      multiCriteriaAnalysisScore.setMultiCriteriaStepScores(stepScores);
    }
  }

  /**
   * Resolves each multi-criteria analysis of the project into a self-contained response, attaching
   * the predictibility analysis (and its execution-plan score when present) to every entry.
   *
   * @param project the project whose multi-criteria analyses are mapped
   * @return the list of multi-criteria analysis responses (empty when the project has none)
   * @throws ExecutionPlanNotFoundException if a referenced execution plan cannot be found
   * @throws UserNotAuthorizedException if the user is not authorized to read an execution-plan score
   */
  @Override
  public List<MulticriteriaAnalysisResponse> buildProjectAnalyses(final Project project)
      throws ExecutionPlanNotFoundException, UserNotAuthorizedException {
    List<MulticriteriaAnalysisResponse> multicriteriaAnalysisList = new ArrayList<>();
    for (MultiCriteriaAnalysis multicriteriaAnalysis : project.getMultiCriteriaAnalysisList()) {
      PredictibilityAnalyseResponseModel predictibilityAnalyse = null;
      PredictibilityAnalysis predictibilityAnalysis = multicriteriaAnalysis.getPredictibilityAnalysis();
      if (predictibilityAnalysis != null) {
        ExecutionPlanScoreResponse executionPlanScore = null;
        if (predictibilityAnalysis.getExecutionPlan() != null) {
          executionPlanScore = executionPlanService
              .getExecutionPlanScore(predictibilityAnalysis.getExecutionPlan().getExecutionPlanId());
        }
        predictibilityAnalyse = new PredictibilityAnalyseResponseModel(
            predictibilityAnalysis.getPredictibilityAnalysisId(),
            predictibilityAnalysis.getIsFinished(),
            predictibilityAnalysis.getPredictibilityAnalysisIteration(),
            predictibilityAnalysis.getChosenMethodology(),
            multicriteriaMapper.toBrainstormingSummaries(predictibilityAnalysis.getBrainstormings()),
            multicriteriaMapper.toVoteSummaries(predictibilityAnalysis.getVotes()),
            executionPlanScore);
      }
      multicriteriaAnalysisList.add(new MulticriteriaAnalysisResponse(
          multicriteriaAnalysis.getMultiCriteriaAnalysisId(), multicriteriaAnalysis.getCreatedAt(),
          multicriteriaAnalysis.getUpdatedAt(),
          multicriteriaAnalysis.getMultiCriteriaAnalysisIteration(),
          multicriteriaAnalysis.getIsFinished(),
          multicriteriaMapper.toFormStepResponses(multicriteriaAnalysis.getFormSteps()),
          predictibilityAnalyse));
    }
    return multicriteriaAnalysisList;
  }
}
