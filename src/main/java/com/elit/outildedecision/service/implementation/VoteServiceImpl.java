package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.Vote;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.repository.VoteRepository;
import com.elit.outildedecision.service.VoteService;
import com.elit.outildedecision.util.ProjectPermession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The VoteServiceImpl class is a service class that implements the VoteService interface and provides the necessary methods for voting and managing votes.
 */
@Service
@Slf4j
public class VoteServiceImpl implements VoteService {

  private final VoteRepository voteRepository;
  private final UserRepository userRepository;
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  private final ProjectUserPermissionRepository projectUserPermissionRepository;

  /**
   * Constructor for the VoteServiceImpl class.
   *
   * @param voteRepository                   the repository for managing votes.
   * @param userRepository                   the repository for managing users.
   * @param predictibilityAnalysisRepository the repository for managing predictibility analyses.
   * @param projectUserPermissionRepository  the repository for managing project user permissions.
   */
  public VoteServiceImpl(
    final VoteRepository voteRepository,
    final UserRepository userRepository,
    final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
    final ProjectUserPermissionRepository projectUserPermissionRepository) {
    this.voteRepository = voteRepository;
    this.userRepository = userRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
  }

  /**
   * Creates a new vote for a predictibility analysis.
   *
   * @param voteModelRequest the vote model request containing the details of the vote to be created.
   * @return a ResponseEntity with a message indicating whether the vote has been successfully created.
   * @throws VoteAlreadyExistException if a vote for the given predictibility analysis and user already exists.
   */
  @Override
  public ResponseEntity<String> createVote(final VoteModelRequest voteModelRequest)
    throws VoteAlreadyExistException {
    String userEmail =
      (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    Vote voteFromDB =
      voteRepository.findByPredictibilityAnalysisAndUserId(
        voteModelRequest.getPredictibilityAnalysisId(), user.getUserId());
    if (Objects.nonNull(voteFromDB)) {
      throw new VoteAlreadyExistException("Cet utilisateur à déjà soumis un vote.");
    }
    Vote vote = new Vote();
    vote.setChosenPMM(voteModelRequest.getChosenPMM());
    vote.setJustification(voteModelRequest.getJustification());
    vote.setPredictibilityValue(voteModelRequest.getPredictibilityValue());
    PredictibilityAnalysis analysis =
      predictibilityAnalysisRepository
        .findById(voteModelRequest.getPredictibilityAnalysisId())
        .orElseThrow(() -> new RuntimeException("Cet analyse n'existe pas"));
    vote.setPredictibilityAnalysis(analysis);
    vote.setIsConfirmed(voteModelRequest.getIsConfirmed());
    vote.setUser(user);
    voteRepository.save(vote);

    return ResponseEntity.ok().body("Le vote a bien été enregistré.");
  }

  /**
   * Checks the permissions of a user for a given project.
   *
   * @param projectId the id of the project to check the permissions for.
   * @param userId    the id of the user to check the permissions for.
   * @return a list of the permissions of the user for the given project.
   * @throws UserNotFoundException if the user is not found.
   */
  public List<String> checkPermissionByProjectIdAndUserId(final Long projectId,
                                                          final Long userId)
    throws UserNotFoundException {
    User user =
      userRepository
        .findById(userId)
        .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable."));
    List<String> permissions = new ArrayList<>();
    user.getUserProjectPermissions().stream()
      .filter(p -> p.getProject().getProjectId().equals(projectId))
      .forEach(p -> permissions.add(p.getPermission()));
    return permissions;
  }

  /**
   * Retrieves a list of vote model responses for a given predictibility analysis ID.
   *
   * @param predictibilityAnalysisId the ID of the predictibility analysis.
   * @return a list of vote model responses.
   * @throws UserNotFoundException if the user is not found.
   */
  @Override
  public List<VoteModelResponse> getVote(final Long predictibilityAnalysisId)
    throws UserNotFoundException {
    String userEmail =
      (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("userEmail" , userEmail);
    User user = userRepository.findByUserEmail(userEmail);
    log.info("user", user);
    PredictibilityAnalysis predictibilityAnalysis =
      predictibilityAnalysisRepository
        .findById(predictibilityAnalysisId)
        .orElseThrow(() -> new RuntimeException("Predictibility Analysis Id non trouvé."));

    Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();

    List<ProjectUserPermission> projectUserPermission = project.getProjectUserPermissions();

    if (Objects.isNull(projectUserPermission)) {
      throw new UserNotFoundException("Cet utilisateur n'existe pas dans le projet !");
    }
    List<VoteModelResponse> result = new ArrayList<>();

    List<String> userPermission =
      checkPermissionByProjectIdAndUserId(project.getProjectId(), user.getUserId());
    if (userPermission.contains(ProjectPermession.CDP.name())) {
      List<ProjectUserPermission> usersPermission =
        projectUserPermissionRepository.findAllByProjectId(project.getProjectId());
      for (ProjectUserPermission userP : usersPermission) {
        if (userP.getPermission().equals(ProjectPermession.CONTRIBUTER.toString())
          || userP.getPermission().equals(ProjectPermession.CDP.name())) {
          Vote userVote =
            voteRepository.findByPredictibilityAnalysisAndUserId(
              predictibilityAnalysisId, userP.getUser().getUserId());
          if (Objects.isNull(userVote)) {
            result.add(
              VoteModelResponse.builder()
                .voteId(null)
                .isConfirmed(null)
                .predictibilityAnalysisId(predictibilityAnalysisId)
                .justification(null)
                .chosenPMM(null)
                .predictibilityValue(null)
                .userId(userP.getUser().getUserId())
                .build());
          } else {
            result.add(
              VoteModelResponse.builder()
                .voteId(userVote.getVoteId())
                .isConfirmed(userVote.getIsConfirmed())
                .predictibilityAnalysisId(predictibilityAnalysisId)
                .justification(userVote.getJustification())
                .chosenPMM(userVote.getChosenPMM())
                .predictibilityValue(userVote.getPredictibilityValue())
                .userId(userP.getUser().getUserId())
                .build());
          }
        }
      }
    } else {
      Vote vote =
        voteRepository.findByPredictibilityAnalysisAndUserId(
          predictibilityAnalysisId, user.getUserId());
      if (Objects.isNull(vote)) {
        VoteModelResponse voteUser =
          VoteModelResponse.builder()
            .voteId(null)
            .isConfirmed(null)
            .justification(null)
            .chosenPMM(null)
            .predictibilityValue(null)
            .predictibilityAnalysisId(predictibilityAnalysisId)
            .userId(user.getUserId())
            .build();

        result.add(voteUser);
        return result;
      } else {
        result.add(
          new VoteModelResponse(
            vote.getVoteId(),
            vote.getChosenPMM(),
            vote.getJustification(),
            vote.getPredictibilityValue(),
            predictibilityAnalysisId,
            vote.getIsConfirmed(),
            vote.getUser().getUserId()));
      }
    }
    return result;
  }

  /**
   * Updates an existing vote with new details from the provided vote model request.
   *
   * @param voteId           the ID of the vote to update.
   * @param voteModelRequest the vote model request containing the new details of the vote.
   * @return a ResponseEntity with a message indicating whether the vote has been successfully updated.
   */
  @Override
  public ResponseEntity<String> updateVote(final Long voteId, final VoteModelRequest voteModelRequest) {
    String userEmail =
      (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    Vote voteFromDB =
      voteRepository
        .findById(voteId)
        .orElseThrow(() -> new RuntimeException("Ce vote n'existe pas!"));

    PredictibilityAnalysis predictibilityAnalysis = voteFromDB.getPredictibilityAnalysis();
    Long projectId = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId();

    ProjectUserPermission projectUserPermission =
      projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());

    if (user.getUserId().equals(voteFromDB.getUser().getUserId())
      || projectUserPermission.getPermission().contains(ProjectPermession.CDP.name())) {
      voteFromDB.setPredictibilityValue(voteModelRequest.getPredictibilityValue());
      voteFromDB.setChosenPMM(voteModelRequest.getChosenPMM());
      voteFromDB.setJustification(voteModelRequest.getJustification());
      voteRepository.save(voteFromDB);
      return ResponseEntity.status(HttpStatus.OK).body("Vote mis à jour.");
    } else {
      return ResponseEntity.status(HttpStatus.CONFLICT)
        .body("Vous n'êtes pas autorisé à modifier ce vote.");
    }
  }
}
