package com.elit.outildedecision.collaboration.brainstorming.service.implementation;

import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingRequest;
import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingResponse;
import com.elit.outildedecision.collaboration.brainstorming.entity.Brainstorming;
import com.elit.outildedecision.collaboration.brainstorming.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.collaboration.brainstorming.exception.ExistingBrainstormingException;
import com.elit.outildedecision.collaboration.brainstorming.mapper.BrainstormingMapper;
import com.elit.outildedecision.collaboration.brainstorming.repository.BrainstormingRepository;
import com.elit.outildedecision.collaboration.brainstorming.service.BrainstormingService;
import com.elit.outildedecision.collaboration.multicriteria.entity.Criteria;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormQuestion;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.collaboration.multicriteria.dto.CriteriaModel;
import com.elit.outildedecision.collaboration.multicriteria.dto.FormQuestionModel;
import com.elit.outildedecision.collaboration.brainstorming.dto.FormStepModelForBrainstorming;
import com.elit.outildedecision.collaboration.multicriteria.repository.FormQuestionRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.exception.ForbiddenException;
import com.elit.outildedecision.shared.exception.NotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * this class is the implementation of the BrainstormingService interface.
 * and it contains all the methods that are used to manage the brainstorming
 *
 */
@Service
@Slf4j
public class BrainstormingServiceImpl implements BrainstormingService {

  /**
   * the brainstorming repository that will be used to access the data.
   */
  private final BrainstormingRepository brainstormingRepository;
  /**
   * the predictibility analysis repository that will be used to access the data.
   */
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  /**
   * the form question repository that will be used to access the data.
   */
  private final FormQuestionRepository formQuestionRepository;
  /**
   * the project repository that will be used to access the data.
   */
  private final ProjectUserPermissionRepository projectUserPermissionRepository;
  /**
   * the user repository that will be used to access the data.
   */
  private final UserRepository userRepository;
  /**
   * the mapper used to convert brainstorming entities to/from DTOs.
   */
  private final BrainstormingMapper brainstormingMapper;

  /**
   * the constructor of the class.
   * dependency injection of the repositories via the constructor
   *
   * @param brainstormingRepository dependency injection of the brainstorming repository
   * @param formQuestionRepository dependency injection of the form question repository
   * @param userRepository dependency injection of the user repository
   * @param predictibilityAnalysisRepository dependency injection of the predictibility analysis repository
   * @param projectUserPermissionRepository dependency injection of the project user permission repository
   * @param brainstormingMapper dependency injection of the brainstorming mapper
   */
  public BrainstormingServiceImpl(
      final BrainstormingRepository brainstormingRepository,
      final FormQuestionRepository formQuestionRepository,
      final UserRepository userRepository,
      final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
      final ProjectUserPermissionRepository projectUserPermissionRepository,
      final BrainstormingMapper brainstormingMapper) {
    this.brainstormingRepository = brainstormingRepository;
    this.formQuestionRepository = formQuestionRepository;
    this.userRepository = userRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
    this.brainstormingMapper = brainstormingMapper;
  }

  /**
   * this method is used create a brainstorming for a predictability analysis.
   *
   * @param projectId the id of the project
   * @param request the payload that contains the data related to the brainstorming
   * @return the id of the created brainstorming
   */
  @Override
  @Transactional
  public Long createBrainstorming(final Long projectId, final BrainstormingRequest request)
    throws PredictibilityAnalysisNotFoundException, ExistingBrainstormingException, ProjectNotFoundException {
    UserDetails userDetails = (UserDetails)  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userDetails.getUsername());

    PredictibilityAnalysis predictibilityAnalysis =
      predictibilityAnalysisRepository
        .findById(request.getPredictibilityAnalysisId())
        .orElseThrow(
          () -> new PredictibilityAnalysisNotFoundException(Constants.ANALYSIS_DOESNT_EXIST));

    if (!Objects.equals(predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw  new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    Brainstorming savedBrainstorming =
      brainstormingRepository.findByUserQuestionPredictibilityId(
        user.getUserId(),
        request.getQuestionId(),
        predictibilityAnalysis.getPredictibilityAnalysisId());

    if (!Objects.isNull(savedBrainstorming)) {
      throw new ExistingBrainstormingException("Cet utilisateur à déjà soumis un commentaire.");
    }
    FormQuestion formQuestion = formQuestionRepository.findById(request.getQuestionId())
        .orElseThrow(() -> new NotFoundException("Cette question n'existe pas."));
    Long questionAnalysisId =
        formQuestion.getFormStep().getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId();
    Long projectAnalysisId =
        predictibilityAnalysis.getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId();
    if (!Objects.equals(questionAnalysisId, projectAnalysisId)) {
      throw new BadRequestException("Cette question n'appartient pas à l'analyse de ce projet.");
    }

    Brainstorming brainstorming = brainstormingMapper.toEntity(request);
    brainstorming.setPredictibilityAnalysis(predictibilityAnalysis);
    brainstorming.setFormQuestion(formQuestion);
    brainstorming.setUser(user);
    Brainstorming createdBrainstorming = brainstormingRepository.save(brainstorming);
    log.info(
      "Brainstorming created for projectId={} analysisId={} questionId={} userId={}",
      projectId,
      predictibilityAnalysis.getPredictibilityAnalysisId(),
      request.getQuestionId(),
      user.getUserId());
    return createdBrainstorming.getBrainstormingId();
  }

  /**
   *
   * this method is used to update a brainstorming.
   *
   * @param projectId the id of the project
   * @param request the payload with which the brainstorming will be updated
   * @return the confirmation message
   */
  @Override
  @Transactional
  public String updateBrainstorming(final Long projectId, final BrainstormingRequest request)
    throws BrainstormingNotFoundException, ProjectNotFoundException {
    Brainstorming brainstorming =
      brainstormingRepository
        .findById(request.getBrainstormingId())
        .orElseThrow(
          () -> new BrainstormingNotFoundException(Constants.BRAINSTORMING_DOESNT_EXIST));

    if (!Objects.equals(brainstorming.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    UserDetails userDetails = (UserDetails)  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userDetails.getUsername());

    if (!brainstorming.getUser().getUserId().equals(user.getUserId())) {
      log.warn(
        "Brainstorming update rejected: userId={} is not the author of brainstormingId={}",
        user.getUserId(),
        brainstorming.getBrainstormingId());
      throw new ForbiddenException(
        "L'utilisateur enregistré ne correspond pas à l'utilisateur du brainstorming");
    }

    brainstorming.setBrainstormingText(request.getBrainstormingText());
    brainstorming.setIsChecked(request.getIsChecked());

    brainstormingRepository.save(brainstorming);
    log.info(
      "Brainstorming updated for projectId={} brainstormingId={} userId={}",
      projectId,
      brainstorming.getBrainstormingId(),
      user.getUserId());

    return Constants.BRAINSTORMING_SAVED;
  }

  /**
   *
   * this method is used to delete a brainstorming from a predictability analysis.
   * @param projectId the id of the project
   * @param brainstormingId the id of the brainstorming that will be deleted
   * @return the confirmation message
   */
  @Override
  @Transactional
  public String deleteBrainstorming(final Long projectId, final Long brainstormingId)
    throws BrainstormingNotFoundException, ProjectNotFoundException {
    Brainstorming brainstorming =
      brainstormingRepository
        .findById(brainstormingId)
        .orElseThrow(
          () -> new BrainstormingNotFoundException(Constants.BRAINSTORMING_DOESNT_EXIST));
    if (!Objects.equals(brainstorming.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }
    brainstormingRepository.delete(brainstorming);
    log.info("Brainstorming deleted for projectId={} brainstormingId={}", projectId, brainstormingId);
    return "Le commentaire du brainstorming a bien été supprimé.";
  }

  /**
   * this is used to check the brainstorming or not.
   *
   * @param projectId  the id of the project
   * @param brainstormingId the id of the brainstorming that will be validated or rejected
   * @param isChecked the boolean that will be used to validate or reject the brainstorming
   * @return the confirmation message
   */
  @Override
  @Transactional
  public String validateOrRejectBrainstorming(
    final Long projectId, final Long brainstormingId, final HttpEntity<String> isChecked)
    throws JsonProcessingException, BrainstormingNotFoundException, ProjectNotFoundException {
    Brainstorming brainstorming =
      brainstormingRepository
        .findById(brainstormingId)
        .orElseThrow(
          () -> new BrainstormingNotFoundException(Constants.BRAINSTORMING_DOESNT_EXIST));

    if (!Objects.equals(brainstorming.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    Map<String, Boolean> result = new ObjectMapper().readValue(isChecked.getBody(), HashMap.class);

    brainstorming.setIsChecked(result.get("isConfirmed"));

    brainstormingRepository.save(brainstorming);
    log.info(
      "Brainstorming validation updated for projectId={} brainstormingId={} isChecked={}",
      projectId,
      brainstormingId,
      brainstorming.getIsChecked());
    return "Le commentaire a bien été enregistré.";
  }

  /**
   * this method is used to get all the brainstorming of a predictability analysis.
   * @param projectId the id of the project
   * @param predictabilityAnalysisId the id of the predictability analysis
   * @throws PredictibilityAnalysisNotFoundException if the predictability analysis doesn't exist
   * @return a list of form steps carrying their brainstormings
   */
  @Override
  @Transactional(readOnly = true)
  public List<FormStepModelForBrainstorming> getBrainstorming(final Long projectId, final Long predictabilityAnalysisId)
    throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    return getBrainstormingForProject(projectId, predictabilityAnalysisId);
  }


  /**
   * this method is used to get a brainstorming abstract of a predictability analysis.
   * @param projectId  the id of the project
   * @param predictabilityAnalysisId the id of the predictability analysis
   * @return a list of form steps carrying their brainstormings
   */
  @Override
  @Transactional(readOnly = true)
  public List<FormStepModelForBrainstorming> getBrainstormingAbstract(final Long projectId, final Long predictabilityAnalysisId)
    throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    return getBrainstormingForProject(projectId, predictabilityAnalysisId);
  }

  /**
   * Resolves the predictability analysis, checks it belongs to the project and returns its form
   * steps with brainstormings. Private so callers reuse the logic without self-invoking another
   * {@code @Transactional} public method (the inner annotation would otherwise be ignored).
   *
   * @param projectId the id of the project
   * @param predictabilityAnalysisId the id of the predictability analysis
   * @return a list of form steps carrying their brainstormings
   */
  private List<FormStepModelForBrainstorming> getBrainstormingForProject(
      final Long projectId, final Long predictabilityAnalysisId) {
    PredictibilityAnalysis predictibilityAnalysis =
      predictibilityAnalysisRepository
        .findById(predictabilityAnalysisId)
        .orElseThrow(
          () -> new PredictibilityAnalysisNotFoundException(Constants.ANALYSIS_DOESNT_EXIST));

    if (!Objects.equals(predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw  new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    return getFormStepsWithBrainstorming(predictibilityAnalysis);
  }

  /**
   * this method is used to get a brainstorming abstract from the analysis id alone,
   * resolving the owning project internally.
   * @param predictabilityAnalysisId the id of the predictability analysis
   * @return a list of form steps carrying their brainstormings
   */
  @Override
  @Transactional(readOnly = true)
  public List<FormStepModelForBrainstorming> getBrainstormingAbstractByAnalysisId(final Long predictabilityAnalysisId)
    throws PredictibilityAnalysisNotFoundException {
    PredictibilityAnalysis predictibilityAnalysis =
      predictibilityAnalysisRepository
        .findById(predictabilityAnalysisId)
        .orElseThrow(
          () -> new PredictibilityAnalysisNotFoundException(Constants.ANALYSIS_DOESNT_EXIST));

    return getFormStepsWithBrainstorming(predictibilityAnalysis);
  }

  private List<FormStepModelForBrainstorming> getFormStepsWithBrainstorming(final PredictibilityAnalysis predictibilityAnalysis) {
    List<FormStep> formStepList =
      predictibilityAnalysis.getMultiCriteriaAnalysis().getFormSteps().stream()
        .filter(formStep -> formStep.getEScore() < 0.5)
        .collect(Collectors.toList());
    log.info("Brainstorming abstract built from {} form steps", formStepList.size());

    Long projectId = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId();

    List<FormStepModelForBrainstorming> result = new ArrayList<>();
    for (FormStep formStep : formStepList) {
      List<FormQuestionModel> formQuestionList = new ArrayList<>();
      for (FormQuestion formQuestion : formStep.getFormQuestions()) {
        List<CriteriaModel> criteriaList = new ArrayList<>();
        for (Criteria criteria : formQuestion.getCriterias()) {
          criteriaList.add(
            new CriteriaModel(
              criteria.getCriteriaId(),
              criteria.getCriteriaName(),
              criteria.getCriteriaValue(),
              formQuestion.getQuestionId()));
        }
        List<BrainstormingResponse> braistormingList = new ArrayList<>();
        for (Brainstorming brainstorming : formQuestion.getBrainstormings()) {
          User user = userRepository.getById(brainstorming.getUser().getUserId());
          String userRole =
            projectUserPermissionRepository
              .findByProjectIdUserID(projectId, user.getUserId())
              .getPermission();
          braistormingList.add(
            brainstormingMapper.toResponse(brainstorming, formQuestion.getQuestionId(), userRole));
        }
        formQuestionList.add(
          new FormQuestionModel(
            formQuestion.getQuestionId(),
            formQuestion.getQuestionText(),
            formQuestion.getAnswerText(),
            formQuestion.getQuestionRef(),
            formQuestion.getUpdateQuestion(),
            formStep.getStepId(),
            criteriaList,
            braistormingList));
      }

      result.add(
        new FormStepModelForBrainstorming(
          formStep.getStepId(),
          formStep.getStepName(),
          formStep.getStepRef(),
          formStep.getEScore(),
          formStep.getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId(),
          formQuestionList));
    }

    return result;
  }

}
