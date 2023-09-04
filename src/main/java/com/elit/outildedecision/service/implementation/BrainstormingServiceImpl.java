
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Brainstorming;
import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.exception.ExistingBrainstormingException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.BrainstormingModel;
import com.elit.outildedecision.model.CriteriaModel;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.model.FormStepModelForBrainstorming;
import com.elit.outildedecision.repository.BrainstormingRepository;
import com.elit.outildedecision.repository.FormQuestionRepository;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.BrainstormingService;
import com.elit.outildedecision.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.elit.outildedecision.util.Constants.ANALYSIS_DOESNT_EXIST;
import static com.elit.outildedecision.util.Constants.BRAINSTORMING_DOESNT_EXIST;

/**
 * this class is the implementation of the BrainstormingService interface.
 * and it contains all the methods that are used to manage the brainstorming
 *
 */
@Service
public class BrainstormingServiceImpl implements BrainstormingService {

  private final Logger log = LoggerFactory.getLogger(BrainstormingServiceImpl.class);

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
   * the constructor of the class.
   * dependency injection of the repositories via the constructor
   *
   * @param brainstormingRepository dependency injection of the brainstorming repository
   * @param formQuestionRepository dependency injection of the form question repository
   * @param userRepository dependency injection of the user repository
   * @param predictibilityAnalysisRepository dependency injection of the predictibility analysis repository
   * @param projectUserPermissionRepository dependency injection of the project user permission repository
   */
  public BrainstormingServiceImpl(
      final BrainstormingRepository brainstormingRepository,
      final FormQuestionRepository formQuestionRepository,
      final UserRepository userRepository,
      final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
      final ProjectUserPermissionRepository projectUserPermissionRepository) {
    this.brainstormingRepository = brainstormingRepository;
    this.formQuestionRepository = formQuestionRepository;
    this.userRepository = userRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
  }

  /**
   * this method is used create a brainstorming for a predictability analysis.
   *
   * @param projectId the id of the project
   * @param brainstormingModel the brainstorming model that contains the data related to the brainstorming
   * @return a list of brainstorming
   */
  @Override
  public ResponseEntity<String> createBrainstorming(final Long projectId, final BrainstormingModel brainstormingModel)
    throws PredictibilityAnalysisNotFoundException, ExistingBrainstormingException, ProjectNotFoundException {
    String userEmail =
      (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    PredictibilityAnalysis predictibilityAnalysis =
      predictibilityAnalysisRepository
        .findById(brainstormingModel.getPredictibilityAnalysisId())
        .orElseThrow(
          () -> new PredictibilityAnalysisNotFoundException(ANALYSIS_DOESNT_EXIST));

    if (!Objects.equals(predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw  new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    Brainstorming savedBrainstorming =
      brainstormingRepository.findByUserQuestionPredictibilityId(
        user.getUserId(),
        brainstormingModel.getQuestionId(),
        predictibilityAnalysis.getPredictibilityAnalysisId());

    if (!Objects.isNull(savedBrainstorming)) {
      throw new ExistingBrainstormingException("Cet utilisateur à déjà soumis un commentaire.");
    }
    Brainstorming brainstorming = new Brainstorming();
    brainstorming.setPredictibilityAnalysis(predictibilityAnalysis);
    brainstorming.setBrainstormingText(brainstormingModel.getBrainstormingText());
    brainstorming.setFormQuestion(
      formQuestionRepository.getById(brainstormingModel.getQuestionId()));
    brainstorming.setIsChecked(false);
    brainstorming.setUser(user);
    brainstorming.setIsChecked(brainstormingModel.getIsChecked());
    brainstormingRepository.save(brainstorming);
    return ResponseEntity.ok().body("Le commentaire du brainstorming a bien été enregistré.");
  }

  /**
   *
   * this method is used to update a brainstorming.
   *
   * @param projectId the id of the project
   * @param brainstormingModel the brainstorming model that contains the data
   *                           with which the brainstorming will be updated
   * @return response entity
   */
  @Override
  public ResponseEntity<String> updateBrainstorming(final Long projectId, final BrainstormingModel brainstormingModel)
    throws BrainstormingNotFoundException, ProjectNotFoundException {
    Brainstorming brainstorming =
      brainstormingRepository
        .findById(brainstormingModel.getBrainstormingId())
        .orElseThrow(
          () -> new BrainstormingNotFoundException(BRAINSTORMING_DOESNT_EXIST));

    if (!Objects.equals(brainstorming.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    String userEmail =
      (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    if (!brainstorming.getUser().getUserId().equals(user.getUserId())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body("L'utilisateur enregistré ne correspond pas à l'utilisateur du brainstorming");
    }

    brainstorming.setBrainstormingText(brainstormingModel.getBrainstormingText());
    brainstorming.setIsChecked(brainstormingModel.getIsChecked());

    brainstormingRepository.save(brainstorming);

    return ResponseEntity.ok().body(Constants.BRAINSTORMING_SAVED);
  }

  /**
   *
   * this method is used to delete a brainstorming from a predictability analysis.
   * @param projectId the id of the project
   * @param brainstormingId the id of the brainstorming that will be deleted
   * @return response entity
   */
  @Override
  public ResponseEntity<String> deleteBrainstorming(final Long projectId, final Long brainstormingId)
    throws BrainstormingNotFoundException, ProjectNotFoundException {
    Brainstorming brainstorming =
      brainstormingRepository
        .findById(brainstormingId)
        .orElseThrow(
          () -> new BrainstormingNotFoundException(BRAINSTORMING_DOESNT_EXIST));

    if (!Objects.equals(brainstorming.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }
    brainstormingRepository.delete(brainstorming);
    return ResponseEntity.ok().body("Le commentaire du brainstorming a bien été supprimé.");
  }

  /**
   * this is used to check the brainstorming or not.
   *
   * @param projectId  the id of the project
   * @param brainstormingId the id of the brainstorming that will be validated or rejected
   * @param isChecked the boolean that will be used to validate or reject the brainstorming
   * @return a list of brainstorming
   */
  @Override
  public ResponseEntity<String> validateOrRejectBrainstorming(
    final Long projectId, final Long brainstormingId, final HttpEntity<String> isChecked)
    throws JsonProcessingException, BrainstormingNotFoundException, ProjectNotFoundException {
    Brainstorming brainstorming =
      brainstormingRepository
        .findById(brainstormingId)
        .orElseThrow(
          () -> new BrainstormingNotFoundException(BRAINSTORMING_DOESNT_EXIST));

    if (!Objects.equals(brainstorming.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    Map<String, Boolean> result = new ObjectMapper().readValue(isChecked.getBody(), HashMap.class);

    brainstorming.setIsChecked(result.get("isConfirmed"));

    brainstormingRepository.save(brainstorming);
    return ResponseEntity.ok().body("Le commentaire a bien été enregistré.");
  }

  /**
   * this method is used to get all the brainstorming of a predictability analysis.
   * @param projectId
   * @param predictabilityAnalysisId the id of the predictability analysis
   * @throws PredictibilityAnalysisNotFoundException if the predictability analysis doesn't exist
   * @return a list of brainstorming
   */
  @Override
  public ResponseEntity<Object> getBrainstorming(final Long projectId, final Long predictabilityAnalysisId) throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {

    PredictibilityAnalysis predictibilityAnalysis =
      predictibilityAnalysisRepository
        .findById(predictabilityAnalysisId)
        .orElseThrow(
          () -> new PredictibilityAnalysisNotFoundException(ANALYSIS_DOESNT_EXIST));

    if (!Objects.equals(predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw  new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    List<FormStepModelForBrainstorming> result = getFormStepsWithBrainstorming(predictibilityAnalysis);

    return ResponseEntity.ok().body(result);
  }


  /**
   * this method is used to get a brainstorming abstract of a predictability analysis.
   * @param projectId  the id of the project
   * @param predictabilityAnalysisId the id of the predictability analysis
   * @return response entity that contains the brainstorming abstract
   */
  @Override
  public ResponseEntity<Object> getBrainstormingAbstract(final Long projectId, final Long predictabilityAnalysisId)
    throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    return getBrainstorming(projectId, predictabilityAnalysisId);
  }

  private List<FormStepModelForBrainstorming> getFormStepsWithBrainstorming(final PredictibilityAnalysis predictibilityAnalysis) {
    List<FormStep> formStepList =
      predictibilityAnalysis.getMultiCriteriaAnalysis().getFormSteps().stream()
        .filter(
          formStep ->
            formStep.getFormQuestions().stream()
              .filter(formQuestion -> !formQuestion.getBrainstormings().isEmpty())
              .count()
              > 0)
        .collect(Collectors.toList());
    this.log.info("ABSTRACT formStepList size: {}", formStepList.size());

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
        List<BrainstormingModel> braistormingList = new ArrayList<>();
        for (Brainstorming brainstorming : formQuestion.getBrainstormings()) {
          User user = userRepository.getById(brainstorming.getUser().getUserId());
          String userRole =
            projectUserPermissionRepository
              .findByProjectIdUserID(projectId, user.getUserId())
              .getPermission();
          braistormingList.add(
            new BrainstormingModel(
              brainstorming.getBrainstormingId(),
              brainstorming.getBrainstormingText(),
              brainstorming.getIsChecked(),
              formQuestion.getQuestionId(),
              brainstorming.getPredictibilityAnalysis().getPredictibilityAnalysisId(),
              user.getUserId(),
              user.getUserFirstName(),
              user.getUserLastName(),
              userRole));
        }
        formQuestionList.add(
          new FormQuestionModel(
            formQuestion.getQuestionId(),
            formQuestion.getQuestionText(),
            formQuestion.getAnswerText(),
            formQuestion.getQuestionRef(),
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
