package com.elit.outildedecision.collaboration.vote.service.implementation;

import com.elit.outildedecision.collaboration.vote.dto.VoteRequest;
import com.elit.outildedecision.collaboration.vote.dto.VoteResponse;
import com.elit.outildedecision.collaboration.vote.entity.Vote;
import com.elit.outildedecision.collaboration.vote.exception.VoteNotFoundException;
import com.elit.outildedecision.collaboration.vote.mapper.VoteMapper;
import com.elit.outildedecision.collaboration.vote.repository.VoteRepository;
import com.elit.outildedecision.collaboration.vote.service.VoteService;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.executionplan.exception.VoteAlreadyExistException;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.Constants;
import com.elit.outildedecision.shared.util.ProjectPermession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the vote service: manages the votes cast on a predictibility analysis.
 */
@Service
@Slf4j
public class VoteServiceImpl implements VoteService {

  /**
   * Repository for accessing vote data.
   */
  private final VoteRepository voteRepository;

  /**
   * Repository for accessing user data.
   */
  private final UserRepository userRepository;

  /**
   * Repository for accessing predictibility Analysis data.
   */
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;

  /**
   * Repository for accessing project user permission data.
   */
  private final ProjectUserPermissionRepository projectUserPermissionRepository;

  /**
   * Mapper between vote entities and their DTOs.
   */
  private final VoteMapper voteMapper;

  /**
   * Constructor for the VoteServiceImpl class.
   *
   * @param voteRepository                   the repository for managing votes
   * @param userRepository                   the repository for managing users
   * @param predictibilityAnalysisRepository the repository for managing predictibility analyses
   * @param projectUserPermissionRepository  the repository for managing project user permissions
   * @param voteMapper                       the vote mapper
   */
  public VoteServiceImpl(
    final VoteRepository voteRepository,
    final UserRepository userRepository,
    final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
    final ProjectUserPermissionRepository projectUserPermissionRepository,
    final VoteMapper voteMapper) {
    this.voteRepository = voteRepository;
    this.userRepository = userRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
    this.voteMapper = voteMapper;
  }

  /**
   * Creates a new vote for a predictibility analysis.
   *
   * @param projectId   the project id
   * @param voteRequest the vote model request containing the details of the vote to be created
   * @return a message indicating the vote has been successfully created
   * @throws VoteAlreadyExistException if a vote for the given analysis and user already exists
   */
  @Override
  @Transactional
  public String createVote(final Long projectId, final VoteRequest voteRequest)
    throws VoteAlreadyExistException, ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userDetails.getUsername());

    Vote voteFromDB =
      voteRepository.findByPredictibilityAnalysisAndUserId(
        voteRequest.getPredictibilityAnalysisId(), user.getUserId());

    if (Objects.nonNull(voteFromDB)) {
      throw new VoteAlreadyExistException("Cet utilisateur a déjà soumis un vote.");
    }

    PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.findById(voteRequest.getPredictibilityAnalysisId())
      .orElseThrow(() -> new PredictibilityAnalysisNotFoundException("Analyse de Predictibilité n'existe pas "));

    if (!Objects.equals(predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    Vote vote = voteMapper.toEntity(voteRequest);
    vote.setPredictibilityAnalysis(predictibilityAnalysis);
    vote.setUser(user);
    voteRepository.save(vote);

    log.info("Vote created for projectId={} userId={} predictibilityAnalysisId={}",
      projectId, user.getUserId(), voteRequest.getPredictibilityAnalysisId());

    return "Le vote a bien été enregistré.";
  }

  /**
   * Checks the permissions of a user for a given project.
   *
   * @param projectId the id of the project to check the permissions for
   * @param userId    the id of the user to check the permissions for
   * @return a list of the permissions of the user for the given project
   * @throws UserNotFoundException if the user is not found
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
   * Retrieves a list of vote responses for a given predictibility analysis ID.
   *
   * @param predictibilityAnalysisId the ID of the predictibility analysis
   * @return a list of vote responses
   * @throws UserNotFoundException if the user is not found
   */
  @Override
  @Transactional(readOnly = true)
  public List<VoteResponse> getVote(final Long predictibilityAnalysisId)
    throws UserNotFoundException {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userDetails.getUsername());

    PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository
      .findById(predictibilityAnalysisId)
      .orElseThrow(() -> new PredictibilityAnalysisNotFoundException("Predictibility Analysis Id non trouvé."));

    Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();

    List<ProjectUserPermission> projectUserPermission = project.getProjectUserPermissions();

    if (Objects.isNull(projectUserPermission)) {
      throw new UserNotFoundException("Cet utilisateur n'existe pas dans le projet !");
    }

    List<String> userPermission = checkPermissionByProjectIdAndUserId(project.getProjectId(), user.getUserId());

    if (userPermission.contains(ProjectPermession.CDP.name())) {
      return getAllVotesForCDP(project, predictibilityAnalysisId);
    }
    return getVoteForNonCDPUser(user, predictibilityAnalysisId);
  }

  /**
   * Retrieves the votes of all contributors and CDP users of a project for an analysis.
   *
   * @param project                  the project
   * @param predictibilityAnalysisId the ID of the predictibility analysis
   * @return a list of vote responses
   */
  private List<VoteResponse> getAllVotesForCDP(final Project project, final Long predictibilityAnalysisId) {
    List<VoteResponse> result = new ArrayList<>();
    List<ProjectUserPermission> usersPermission = projectUserPermissionRepository.findAllByProjectId(project.getProjectId());
    for (ProjectUserPermission userP : usersPermission) {
      if (userP.getPermission().equals(ProjectPermession.CONTRIBUTER.toString())
        || userP.getPermission().equals(ProjectPermession.CDP.name())) {
        Vote userVote = voteRepository.findByPredictibilityAnalysisAndUserId(predictibilityAnalysisId, userP.getUser().getUserId());
        result.add(toVoteResponse(userVote, predictibilityAnalysisId, userP.getUser().getUserId()));
      }
    }
    return result;
  }

  /**
   * Retrieves the vote of a non CDP user.
   *
   * @param user                     the user
   * @param predictibilityAnalysisId the predictibility analysis id
   * @return a list of vote responses
   */
  private List<VoteResponse> getVoteForNonCDPUser(final User user, final Long predictibilityAnalysisId) {
    List<VoteResponse> result = new ArrayList<>();
    List<Vote> votes = voteRepository.findAllByPredictibilityAnalysisId(predictibilityAnalysisId);
    for (Vote vote : votes) {
      result.add(toVoteResponse(vote, predictibilityAnalysisId, user.getUserId()));
    }
    return result;
  }

  /**
   * Builds a vote response from a vote, falling back to an empty placeholder when the user
   * has not voted yet.
   *
   * @param vote                     the vote, may be {@code null}
   * @param predictibilityAnalysisId the predictibility analysis id
   * @param userId                   the user id
   * @return a vote response
   */
  private VoteResponse toVoteResponse(final Vote vote, final Long predictibilityAnalysisId, final Long userId) {
    if (Objects.isNull(vote)) {
      return VoteResponse.builder()
        .voteId(null)
        .isConfirmed(null)
        .predictibilityAnalysisId(predictibilityAnalysisId)
        .justification(null)
        .chosenPMM(null)
        .predictibilityValue(null)
        .userId(userId)
        .build();
    }
    return voteMapper.toResponse(vote);
  }

  /**
   * Updates an existing vote with new details from the provided vote request.
   *
   * @param projectId   the project id
   * @param voteId      the ID of the vote to update
   * @param voteRequest the vote request containing the new details of the vote
   * @return the success message
   */
  @Override
  @Transactional
  public String updateVote(final Long projectId, final Long voteId, final VoteRequest voteRequest)
    throws ProjectNotFoundException, VoteNotFoundException {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userDetails.getUsername());

    Vote voteFromDB =
      voteRepository
        .findById(voteId)
        .orElseThrow(() -> new VoteNotFoundException("Ce vote n'existe pas!"));

    if (!Objects.equals(voteFromDB.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    if (!user.getUserId().equals(voteFromDB.getUser().getUserId())) {
      log.warn("User userId={} is not allowed to update voteId={} of projectId={}",
        user.getUserId(), voteId, projectId);
      throw new ConflictException("Vous n'êtes pas autorisé à modifier ce vote.");
    }

    voteFromDB.setPredictibilityValue(voteRequest.getPredictibilityValue());
    voteFromDB.setChosenPMM(voteRequest.getChosenPMM());
    voteFromDB.setJustification(voteRequest.getJustification());
    voteRepository.save(voteFromDB);

    log.info("Vote voteId={} updated by userId={} for projectId={}", voteId, user.getUserId(), projectId);
    return "Vote mis à jour.";
  }
}
