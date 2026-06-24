package com.elit.outildedecision.shared.project.service.implementation;

import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanVote;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectPropertyKey;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.collaboration.multicriteria.dto.MulticriteriaAnalysisResponse;
import com.elit.outildedecision.ai.result.dto.ResultResponse;
import com.elit.outildedecision.collaboration.brainstorming.repository.BrainstormingRepository;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanRepository;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanVoteRepository;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityResultsRepository;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.collaboration.vote.repository.VoteRepository;
import com.elit.outildedecision.ai.feedback.service.FeedbackIaService;
import com.elit.outildedecision.shared.attachment.service.FileSystemService;
import com.elit.outildedecision.collaboration.multicriteria.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.shared.security.PermissionService;
import com.elit.outildedecision.ai.result.service.ResultIaService;
import com.elit.outildedecision.shared.attachment.dto.AttachmentResponse;
import com.elit.outildedecision.shared.attachment.mapper.AttachmentMapper;
import com.elit.outildedecision.shared.attachment.service.AttachmentService;
import com.elit.outildedecision.shared.attachment.exception.AttachmentNotFoundException;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectOwnerException;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.project.dto.ContributorRequest;
import com.elit.outildedecision.shared.project.dto.ProjectFilterRequest;
import com.elit.outildedecision.shared.project.dto.ProjectPageResponse;
import com.elit.outildedecision.shared.project.dto.ProjectRequest;
import com.elit.outildedecision.shared.project.dto.ProjectResponse;
import com.elit.outildedecision.shared.project.mapper.ProjectMapper;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.shared.project.service.ProjectValidationResult;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.Constants;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import com.elit.outildedecision.shared.util.ProjectAnalysisState;
import com.elit.outildedecision.shared.util.ProjectConfirmationState;
import com.elit.outildedecision.shared.util.ProjectPermession;
import com.elit.outildedecision.shared.util.ProjectState;
import com.elit.outildedecision.shared.util.TriType;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ProjectService} managing the projects and their related data.
 */
@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {
  /**
   * The PROJECT_NOT_FOUND message.
   */
  public static final String PROJECT_NOT_FOUND = "Ce Projet n'existe pas!";
  /**
   * The projectRepository for accessing the project objects in the database.
   */
  private final ProjectRepository projectRepository;
  /**
   * The userRepository for accessing the user objects in the database.
   */
  private final UserRepository userRepository;
  /**
   * The attachmentService for managing the attachments of the projects.
   */
  private final AttachmentService attachmentService;
  /**
   * The multiCriteriaService for managing the multi criteria analysis of the projects.
   */
  private final MultiCriteriaAnalysisService multiCriteriaService;
  /**
   * The permissionService for managing the project permissions.
   */
  private final PermissionService permissionService;
  /**
   * The feedbackIaService for accessing the feedback IA objects.
   */
  private final FeedbackIaService feedbackIaService;
  /**
   * The resultIaService for accessing the result IA objects.
   */
  private final ResultIaService resultIaService;
  /**
   * The fileSystemService for managing the file system operations.
   */
  private final FileSystemService fileSystemService;
  /**
   * The resultIaRepository for accessing the result IA objects in the database.
   */
  private final ResultIaRepository resultIaRepository;
  /**
   * The projectUserPermissionRepository for accessing the project user permissions.
   */
  private final ProjectUserPermissionRepository projectUserPermissionRepository;
  /**
   * The executionPlanRepository for managing the executionPlan of the projects.
   */
  private final ExecutionPlanRepository executionPlanRepository;
  /**
   * The multiCriteriaAnalysisRepository for managing the multiCriteriaAnalysis of the projects.
   */
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  /**
   * The brainstormingRepository for managing the brainstorming of the projects.
   */
  private final BrainstormingRepository brainstormingRepository;
  /**
   * The predictibilityResultsRepository for managing the predictibilityResults of the projects.
   */
  private final PredictibilityResultsRepository predictibilityResultsRepository;
  /**
   * The predictibilityAnalysisRepository for managing the predictibilityAnalysis of the projects.
   */
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  /**
   * The voteRepository for managing the vote of the projects.
   */
  private final VoteRepository voteRepository;
  /**
   * The executionPlanVoteRepo for managing the vote of the executionPlan.
   */
  private final ExecutionPlanVoteRepository executionPlanVoteRepo;
  /**
   * The aiAnswerRepository for managing answers in the IA path.
   */
  private final AiAnswerRepository aiAnswerRepository;
  /**
   * The projectMapper converting entities to project DTOs.
   */
  private final ProjectMapper projectMapper;
  /**
   * The attachmentMapper converting attachment entities to attachment DTOs.
   */
  private final AttachmentMapper attachmentMapper;

  /**
   * Instantiates a new Project service.
   *
   * @param projectRepository                the project repository
   * @param userRepository                   the user repository
   * @param attachmentService                the attachment service
   * @param projectUserPermissionRepository  the project user permission repository
   * @param resultIaRepository               the result IA repository
   * @param multiCriteriaService             the multi criteria analysis service
   * @param permissionService                the permission service
   * @param feedbackIaService                the feedback IA service
   * @param resultIaService                  the result IA service
   * @param fileSystemService                the file system service
   * @param executionPlanRepository          the execution plan repository
   * @param multiCriteriaAnalysisRepository  the multi criteria analysis repository
   * @param brainstormingRepository          the brainstorming repository
   * @param predictibilityResultsRepository  the predictibility results repository
   * @param predictibilityAnalysisRepository the predictibility analysis repository
   * @param voteRepository                   the vote repository
   * @param executionPlanVoteRepo            the execution plan vote repository
   * @param aiAnswerRepository               the AI answer repository
   * @param projectMapper                    the project mapper
   * @param attachmentMapper                 the attachment mapper
   */
  public ProjectServiceImpl(final ProjectRepository projectRepository, final UserRepository userRepository,
                            final AttachmentService attachmentService,
                            final ProjectUserPermissionRepository projectUserPermissionRepository,
                            final ResultIaRepository resultIaRepository,
                            final MultiCriteriaAnalysisService multiCriteriaService,
                            final PermissionService permissionService,
                            final FeedbackIaService feedbackIaService,
                            final ResultIaService resultIaService,
                            final FileSystemService fileSystemService,
                            final ExecutionPlanRepository executionPlanRepository,
                            final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
                            final BrainstormingRepository brainstormingRepository,
                            final PredictibilityResultsRepository predictibilityResultsRepository,
                            final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
                            final VoteRepository voteRepository, final ExecutionPlanVoteRepository executionPlanVoteRepo,
                            final AiAnswerRepository aiAnswerRepository, final ProjectMapper projectMapper,
                            final AttachmentMapper attachmentMapper) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.attachmentService = attachmentService;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
    this.resultIaRepository = resultIaRepository;
    this.multiCriteriaService = multiCriteriaService;
    this.executionPlanRepository = executionPlanRepository;
    this.executionPlanVoteRepo = executionPlanVoteRepo;
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.brainstormingRepository = brainstormingRepository;
    this.predictibilityResultsRepository = predictibilityResultsRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.voteRepository = voteRepository;
    this.aiAnswerRepository = aiAnswerRepository;
    this.permissionService = permissionService;
    this.feedbackIaService = feedbackIaService;
    this.resultIaService = resultIaService;
    this.fileSystemService = fileSystemService;
    this.projectMapper = projectMapper;
    this.attachmentMapper = attachmentMapper;
  }

  /**
   * Persists a new project, its permissions, attachments and multicriteria analysis form.
   *
   * @param project   the project to create
   * @param userEmail the email of the connected user
   * @return the newly created project
   * @throws UserNotFoundException                 if the chef or a contributor cannot be found
   * @throws ProjectNotFoundException              if the project cannot be found
   * @throws IOException                           if the multicriteria analysis form cannot be read
   * @throws ParseException                        if the multicriteria analysis form cannot be parsed
   * @throws org.json.simple.parser.ParseException if the multicriteria analysis form JSON is invalid
   * @throws FileCouldNotBeSaved                   if an uploaded file cannot be saved
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ProjectResponse persistProject(final ProjectRequest project, final String userEmail)
      throws UserNotFoundException, ProjectNotFoundException, IOException,
      ParseException, org.json.simple.parser.ParseException, FileCouldNotBeSaved {
    Project projectToSave = new Project();
    // getting the connected user
    UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User chef = userRepository.findByUserEmail(principal.getUsername());
    Date dateOfCreation = new Date();
    String folderName = dateOfCreation.getTime() + "_" + project.getName();
    // No filesystem folder is created: project data and attachments are persisted in the database.
    // folderPath is kept only as the logical prefix for attachment paths.
    projectToSave.setFolderPath(folderName.replace(" ", "_").toLowerCase());
    projectToSave.setChefId(chef.getUserId());
    projectToSave.setDescription(project.getDescription());
    projectToSave.setName(project.getName());
    projectToSave.setConfirmationState(ProjectConfirmationState.CONFIRMED.name());
    projectToSave.setIsArchived(false);
    projectToSave.setFormProgressStatus(FormProgressStatus.None);
    projectToSave.setProjectState(ProjectState.PENDING);
    Long projectId = projectRepository.save(projectToSave).getProjectId();

    // adding permissions to project chef and contributers
    ProjectUserPermission projectChefPermission = new ProjectUserPermission();
    projectChefPermission.setUser(chef);
    projectChefPermission.setProject(projectRepository.getById(projectId));
    projectChefPermission.setPermission(ProjectPermession.CDP.name());
    projectChefPermission.setId(new ProjectPropertyKey(projectId, chef.getUserId()));
    projectUserPermissionRepository.save(projectChefPermission);

    List<ContributorRequest> contributorsReturn = new ArrayList<>();

    if (Objects.nonNull(project.getContributers())) {
      for (ContributorRequest contributer : project.getContributers()) {
        User user = userRepository.findById(contributer.getContributerId())
            .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas."));
        ProjectUserPermission projectUserPermission = new ProjectUserPermission();
        projectUserPermission.setUser(user);
        projectUserPermission.setProject(projectRepository.getById(projectId));
        projectUserPermission.setPermission(contributer.getRole());
        projectUserPermission.setUserFunction(contributer.getFunc());
        projectUserPermission.setId(new ProjectPropertyKey(projectId, user.getUserId()));
        projectUserPermissionRepository.save(projectUserPermission);

        ContributorRequest contributerLoaded = new ContributorRequest();

        contributerLoaded.setContributerId(user.getUserId());
        contributerLoaded.setEmail(user.getUserEmail());
        contributerLoaded.setLastName(user.getUserLastName());
        contributerLoaded.setFirstName(user.getUserFirstName());
        contributerLoaded.setFunc(contributer.getFunc());
        contributerLoaded.setAvatarPath(user.getUserAvatarPath());
        contributerLoaded.setRole(projectUserPermission.getPermission());

        contributorsReturn.add(contributerLoaded);
      }
    }
    if (Objects.nonNull(project.getFiles())) {
      attachmentService.uploadAttachment(project.getFiles(), projectId, userEmail);
    }

    multiCriteriaService.createMulticriteriaAnalysisForm(projectId);

    ProjectResponse projectToReturn = projectMapper.toResponseBase(projectToSave);
    projectToReturn.setContributors(contributorsReturn);
    projectToReturn.setMultiCriteriaStatus(ProjectAnalysisState.NOT_STARTED.toString());

    log.info("Created project {} for user {}", projectId, chef.getUserId());
    return projectToReturn;
  }

  /**
   * Retrieves all projects.
   *
   * @return the list of all projects
   */
  @Override
  public List<ProjectResponse> getAllProjects() {
    return projectRepository.findAll().stream()
        .map(projectMapper::toResponseBase)
        .toList();
  }

  /**
   * Retrieves the projects of the connected user matching the given keyword.
   *
   * @param keyword the keyword to search for
   * @return the matching projects
   */
  @Override
  public List<ProjectResponse> getUserProjects(final String keyword) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<Project> projects = new ArrayList<>();
    List<ProjectUserPermission> projectUserPermissions = projectUserPermissionRepository.findAll().stream()
        .filter(projectUserPermission -> projectUserPermission.getUser().getUserEmail()
            .equals(userDetails.getUsername()))
        .collect(Collectors.toList());
    if (!projectUserPermissions.isEmpty()) {
      for (ProjectUserPermission projectUserPermission : projectUserPermissions) {
        projects.add(projectUserPermission.getProject());
      }
    }
    if (keyword != null && !keyword.isEmpty()) {
      projects = projects.stream()
          .filter(project -> Objects.nonNull(project.getDescription())
              && project.getDescription().contains(keyword) || project.getName().contains(keyword))
          .toList();
    }
    return projects.stream().map(projectMapper::toResponseBase).toList();
  }

  /**
   * Updates the name and description of an existing project.
   *
   * @param projectId      the id of the project to update
   * @param updatedProject the updated project data
   * @return the updated project
   * @throws ProjectNotFoundException if the project does not exist
   */
  @Override
  @Transactional(rollbackFor = {ProjectNotFoundException.class, UserNotFoundException.class})
  public ProjectResponse updateProject(final Long projectId, final ProjectRequest updatedProject)
      throws ProjectNotFoundException {
    Optional<Project> optionalProject = projectRepository.findById(projectId);
    if (!optionalProject.isPresent()) {
      throw new ProjectNotFoundException(PROJECT_NOT_FOUND);
    }
    Project projectToSave = optionalProject.get();

    // Update project details
    projectToSave.setName(updatedProject.getName());
    projectToSave.setDescription(updatedProject.getDescription());

    // Save updated project
    projectRepository.save(projectToSave);

    ProjectResponse projectToReturn = projectMapper.toResponseBase(projectToSave);
    projectToReturn.setMultiCriteriaStatus(ProjectAnalysisState.NOT_STARTED.toString());

    log.info("Updated project {}", projectId);
    return projectToReturn;
  }

  /**
   * Builds the pageable used to sort the project list.
   *
   * @param triModel the filter and sort criteria
   * @return the pageable
   */
  public Pageable getPageableForSort(final ProjectFilterRequest triModel) {
    Pageable pageable = PageRequest.of(triModel.getPage(), triModel.getSize());
    if (TriType.DATEDESC.toString().equals(triModel.getSort())) {
      pageable = PageRequest.of(triModel.getPage(), triModel.getSize(), Sort.by("project.createdAt").descending());
    } else if (TriType.DATEASC.toString().equals(triModel.getSort())) {
      pageable = PageRequest.of(triModel.getPage(), triModel.getSize(), Sort.by("project.createdAt").ascending());
    } else if (TriType.NAMEDESC.toString().equals(triModel.getSort())) {
      pageable = PageRequest.of(triModel.getPage(), triModel.getSize(), Sort.by("project.name").ascending());
    } else if (TriType.NAMEASC.toString().equals(triModel.getSort())) {
      pageable = PageRequest.of(triModel.getPage(), triModel.getSize(), Sort.by("project.name").descending());
    }
    return pageable;
  }

  /**
   * Retrieves the paged list of projects of the connected user with their analyses.
   *
   * @param triModel the filter, sort and pagination criteria
   * @return the paged projects together with the created/affected totals
   * @throws UserNotAuthorizedException     if the user is not authorized to access a project
   * @throws ExecutionPlanNotFoundException if an execution plan is not found
   */
  @Override
  @Transactional(readOnly = true)
  public ProjectPageResponse getUserProjectList(final ProjectFilterRequest triModel)
      throws UserNotAuthorizedException, ExecutionPlanNotFoundException {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // getting the connected user
    ProjectPageResponse responsePageProject = new ProjectPageResponse();
    List<ProjectResponse> projects = new ArrayList<>();
    Pageable pageable = getPageableForSort(triModel);
    Page<ProjectUserPermission> projectUserPermissionPagination;
    if (triModel.isActiveTab()) {
      projectUserPermissionPagination = getUserCreatedProject(triModel, userDetails, pageable);
    } else {
      projectUserPermissionPagination = getUserAffectedProject(triModel, userDetails, pageable);
    }
    List<ProjectUserPermission> projectUserPermissions = projectUserPermissionPagination.getContent();

    if (!projectUserPermissions.isEmpty()) {
      for (ProjectUserPermission projectUserPermission : projectUserPermissions) {
        Project project = projectUserPermission.getProject();

        User ownerUserProject = userRepository.findById(project.getChefId())
            .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas."));

        List<ProjectUserPermission> userPermissionList = projectUserPermissionPagination.getContent().stream()
            .filter(permission -> permission.getProject().getProjectId().equals(project.getProjectId()))
            .collect(Collectors.toList());

        List<ContributorRequest> contributorModelList = userPermissionList.stream()
            .map(projectMapper::toContributorWithoutAccount)
            .toList();

        String projectStatus = computeProjectAnalysisStatus(project);

        List<MulticriteriaAnalysisResponse> multicriteriaAnalysisList = buildMulticriteriaAnalysisList(project);

        ResultResponse resultAnalyseIa = buildResultResponse(project);

        projects.add(new ProjectResponse(
            project.getProjectId(),
            project.getName(),
            project.getDescription(),
            project.getCreatedAt(),
            project.getUpdatedAt(),
            project.getConfirmationState(),
            project.getChefId(),
            ownerUserProject.getUserLastName(),
            ownerUserProject.getUserFirstName(),
            null,
            project.getFormProgressStatus(),
            project.getProjectState(),
            contributorModelList,
            null,
            multicriteriaAnalysisList,
            projectStatus,
            resultAnalyseIa));
      }

    }
    responsePageProject.setProjectsPage(
        new PageImpl<>(projects, pageable, projectUserPermissionPagination.getTotalElements()));
    responsePageProject.setTotalProjetCreated(getTotalProjectCreatedUser(triModel, userDetails));
    responsePageProject.setTotalProjetAffected(getTotalProjectAffectedUser(triModel, userDetails));
    return responsePageProject;
  }

  private long getTotalProjectCreatedUser(final ProjectFilterRequest triModel, final UserDetails userDetails) {
    if (Constants.AUDIT.equalsIgnoreCase(triModel.getTypeAnalyse())) {
      return projectUserPermissionRepository.getTotalProjectWithAuditCreatedUser(
          triModel.getKey(), null, "ALL", userDetails.getUsername());
    }
    if (isStrictCompletedMode(triModel)) {
      return getStrictCompletedProjectCount(triModel, userDetails, true);
    }
    return projectUserPermissionRepository.getTotalProjectCreatedUser(
        triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername());
  }

  private long getTotalProjectAffectedUser(final ProjectFilterRequest triModel, final UserDetails userDetails) {
    if (Constants.AUDIT.equalsIgnoreCase(triModel.getTypeAnalyse())) {
      return projectUserPermissionRepository.getTotalProjectWithAuditAffectedUser(
          triModel.getKey(), null, "ALL", userDetails.getUsername());
    }
    if (isStrictCompletedMode(triModel)) {
      return getStrictCompletedProjectCount(triModel, userDetails, false);
    }
    return projectUserPermissionRepository.getTotalProjectAffectedUser(
        triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername());
  }

  private Page<ProjectUserPermission> getUserAffectedProject(final ProjectFilterRequest triModel,
                                                             final UserDetails userDetails, final Pageable pageable) {
    if (Constants.AUDIT.equalsIgnoreCase(triModel.getTypeAnalyse())) {
      return projectUserPermissionRepository.findProjectWithAuditAffected(
          triModel.getKey(), null, "ALL", userDetails.getUsername(), pageable);
    }
    if (isStrictCompletedMode(triModel)) {
      return getStrictCompletedProjectPage(triModel, userDetails, pageable, false);
    }
    return projectUserPermissionRepository.findProjectAffected(
        triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername(), pageable);
  }

  private Page<ProjectUserPermission> getUserCreatedProject(final ProjectFilterRequest triModel,
                                                            final UserDetails userDetails, final Pageable pageable) {
    if (Constants.AUDIT.equalsIgnoreCase(triModel.getTypeAnalyse())) {
      return projectUserPermissionRepository.findProjectWithAuditCreatedByUser(
          triModel.getKey(), null, "ALL", userDetails.getUsername(), pageable);
    }
    if (isStrictCompletedMode(triModel)) {
      return getStrictCompletedProjectPage(triModel, userDetails, pageable, true);
    }
    return projectUserPermissionRepository.findProjectCreatedByUser(
        triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername(), pageable);
  }

  private boolean isStrictCompletedMode(final ProjectFilterRequest triModel) {
    return triModel.getStatus() != null
        && "COMPLETED".equalsIgnoreCase(triModel.getStatus())
        && triModel.getTypeAnalyse() != null
        && ("IA".equalsIgnoreCase(triModel.getTypeAnalyse())
        || "COLLABORATIVE".equalsIgnoreCase(triModel.getTypeAnalyse()));
  }

  private long getStrictCompletedProjectCount(final ProjectFilterRequest triModel, final UserDetails userDetails,
                                              final boolean creator) {
    Page<ProjectUserPermission> candidates = creator
        ? projectUserPermissionRepository.findProjectCreatedByUser(
        triModel.getKey(), "ALL", triModel.getStatus(), userDetails.getUsername(), Pageable.unpaged())
        : projectUserPermissionRepository.findProjectAffected(
        triModel.getKey(), "ALL", triModel.getStatus(), userDetails.getUsername(), Pageable.unpaged());
    return filterCompletedStrict(candidates.getContent(), triModel.getTypeAnalyse()).size();
  }

  private Page<ProjectUserPermission> getStrictCompletedProjectPage(final ProjectFilterRequest triModel,
                                                                    final UserDetails userDetails,
                                                                    final Pageable pageable, final boolean creator) {
    Page<ProjectUserPermission> candidates = creator
        ? projectUserPermissionRepository.findProjectCreatedByUser(
        triModel.getKey(), "ALL", triModel.getStatus(), userDetails.getUsername(), Pageable.unpaged())
        : projectUserPermissionRepository.findProjectAffected(
        triModel.getKey(), "ALL", triModel.getStatus(), userDetails.getUsername(), Pageable.unpaged());
    List<ProjectUserPermission> filtered = filterCompletedStrict(candidates.getContent(), triModel.getTypeAnalyse());
    if (!pageable.isPaged()) {
      return new PageImpl<>(filtered, pageable, filtered.size());
    }
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), filtered.size());
    List<ProjectUserPermission> content = start >= filtered.size() ? Collections.emptyList()
        : filtered.subList(start, end);
    return new PageImpl<>(content, pageable, filtered.size());
  }

  private List<ProjectUserPermission> filterCompletedStrict(final List<ProjectUserPermission> permissions,
                                                            final String typeAnalyse) {
    if (!"IA".equalsIgnoreCase(typeAnalyse) && !"COLLABORATIVE".equalsIgnoreCase(typeAnalyse)) {
      return permissions;
    }
    return permissions.stream()
        .filter(permission -> {
          Project project = permission.getProject();
          if ("IA".equalsIgnoreCase(typeAnalyse)) {
            return isCompletedByIa(project);
          }
          return isCompletedByCollaborative(project);
        })
        .toList();
  }

  private boolean isCompletedByIa(final Project project) {
    return resultIaRepository.findByProject(project).isPresent();
  }

  private boolean isCompletedByCollaborative(final Project project) {
    MultiCriteriaAnalysis latestMultiCriteriaAnalysis =
        multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(project.getProjectId());
    if (latestMultiCriteriaAnalysis == null) {
      return false;
    }
    PredictibilityAnalysis latestPredictibilityAnalysis = predictibilityAnalysisRepository
        .findByLastIterationAndMultiCriteriaId(latestMultiCriteriaAnalysis.getMultiCriteriaAnalysisId());
    return latestPredictibilityAnalysis != null && Boolean.TRUE.equals(latestPredictibilityAnalysis.getIsFinished());
  }

  /**
   * Validates or rejects a project, returning the resulting outcome.
   *
   * @param id        the id of the project to validate or reject
   * @param confirmed {@code true} to validate, {@code false} to reject
   * @return the validation outcome
   * @throws ProjectNotFoundException if the project does not exist
   */
  @Override
  public ProjectValidationResult validateOrRejectProject(final Long id, final boolean confirmed)
      throws ProjectNotFoundException {
    Project projectToConfirm = projectRepository.findById(id)
        .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    ProjectConfirmationState projectConfirmationState = ProjectConfirmationState
        .valueOf(projectToConfirm.getConfirmationState());

    if (confirmed) {
      if (projectConfirmationState == ProjectConfirmationState.CONFIRMED) {
        return ProjectValidationResult.ALREADY_CONFIRMED;
      }
      projectToConfirm.setConfirmationState(ProjectConfirmationState.CONFIRMED.name());
      projectRepository.save(projectToConfirm);
      log.info("Validated project {}", id);
      return ProjectValidationResult.VALIDATED;
    }
    if (projectConfirmationState == ProjectConfirmationState.REJECTED) {
      return ProjectValidationResult.ALREADY_REJECTED;
    }
    projectToConfirm.setConfirmationState(ProjectConfirmationState.REJECTED.name());
    projectRepository.save(projectToConfirm);
    log.info("Rejected project {}", id);
    return ProjectValidationResult.REJECTED;
  }

  /**
   * Adds a contributor to a project.
   *
   * @param projectId   the id of the project
   * @param contributor the contributor to add
   * @return the confirmation message
   * @throws ProjectNotFoundException if the project does not exist
   * @throws UserNotFoundException    if the user does not exist
   */
  @Override
  public String addProjectContributor(final Long projectId, final ContributorRequest contributor)
      throws ProjectNotFoundException, UserNotFoundException {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas! "));

    User user = userRepository.findById(contributor.getContributerId())
        .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas !"));

    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission(contributor.getRole());
    projectUserPermission.setUserFunction(contributor.getFunc());
    projectUserPermission.setId(new ProjectPropertyKey(projectId, user.getUserId()));
    projectUserPermissionRepository.save(projectUserPermission);

    log.info("Added contributor {} to project {}", user.getUserId(), projectId);
    return "Le collaborateur a été bien enregistré";
  }

  /**
   * Removes a contributor from a project.
   *
   * @param projectId     the id of the project
   * @param contributorID the id of the contributor to remove
   * @return the confirmation message
   * @throws ProjectOwnerException    if the contributor is the owner of the project
   * @throws UserNotFoundException    if the contributor does not exist in the project
   * @throws ProjectNotFoundException if the project does not exist
   */
  @Override
  public String deleteProjectContributor(final Long projectId, final Long contributorID)
      throws ProjectOwnerException, UserNotFoundException, ProjectNotFoundException {
    boolean doesProjectExist = projectRepository.existsById(projectId);
    if (Boolean.FALSE.equals(doesProjectExist)) {
      throw new ProjectNotFoundException(PROJECT_NOT_FOUND);
    }
    ProjectUserPermission projectUserPermission = projectUserPermissionRepository.findByProjectIdUserID(projectId,
        contributorID);

    if (Objects.isNull(projectUserPermission)) {
      throw new UserNotFoundException("Cet utilisateur n'existe pas dans le projet !");
    }

    if (projectUserPermission.getPermission().contains(ProjectPermession.CDP.name())) {
      throw new ProjectOwnerException("Cet utilisateur ne peut pas etre elimine car c'est le chef du projet!");
    }

    projectUserPermissionRepository.deleteByProjectUser(projectId, contributorID);

    log.info("Removed contributor {} from project {}", contributorID, projectId);
    return "Le collaborateur a été bien retiré du projet";
  }

  /**
   * Retrieves the full data of a project (contributors, attachments, analyses).
   *
   * @param projectId the id of the project
   * @return the project data
   * @throws ProjectNotFoundException       if the project does not exist
   * @throws UserNotAuthorizedException     if the user is not authorized to access the project
   * @throws ExecutionPlanNotFoundException if an execution plan is not found
   */
  @Override
  @Transactional(readOnly = true)
  public ProjectResponse getProjectData(final Long projectId)
      throws ProjectNotFoundException, UserNotAuthorizedException, ExecutionPlanNotFoundException {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    List<ContributorRequest> contributorModelList = project.getProjectUserPermissions().stream()
        .map(projectMapper::toContributor)
        .collect(Collectors.toList());

    List<AttachmentResponse> attachmentList = attachmentMapper.toResponseList(project.getAttachments());

    List<MulticriteriaAnalysisResponse> multicriteriaAnalysisList = buildMulticriteriaAnalysisList(project);

    ResultResponse resultAnalyseIa = buildResultResponse(project);

    return new ProjectResponse(project.getProjectId(), project.getName(), project.getDescription(),
        project.getCreatedAt(), project.getUpdatedAt(), project.getConfirmationState(), project.getChefId(),
        null, null,
        project.getIsArchived(), project.getFormProgressStatus(), project.getProjectState(), contributorModelList,
        attachmentList,
        multicriteriaAnalysisList, null, resultAnalyseIa);
  }

  /**
   * Deletes a project and all its related data (permissions, analyses, files, feedbacks).
   *
   * @param projectId the id of the project to delete
   * @return the deletion confirmation message
   * @throws ProjectNotFoundException    if the project does not exist
   * @throws AttachmentNotFoundException if an attachment cannot be found
   * @throws IOException                 if the project folder cannot be deleted
   * @throws ResponseNotFoundException   if a related response cannot be found
   */
  @Override
  @Transactional
  public String deleteProject(final Long projectId)
      throws ProjectNotFoundException, AttachmentNotFoundException, IOException, ResponseNotFoundException {
    Project projectToDelete = projectRepository.findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Le projet n'existe pas."));

    permissionService.removePermissions(projectToDelete);
    feedbackIaService.deleteFeedbacksByProjectId(projectId);
    resultIaService.deleteResultAnalysisAIByProjectId(projectId);

    // Handle related PredictibilityAnalysis entity
    if (!projectToDelete.getMultiCriteriaAnalysisList().isEmpty()) {
      for (MultiCriteriaAnalysis multiCriteriaAnalysis : projectToDelete.getMultiCriteriaAnalysisList()) {
        PredictibilityAnalysis predictibilityAnalysis = multiCriteriaAnalysis.getPredictibilityAnalysis();
        aiAnswerRepository.deleteByProject_ProjectId(projectId);

        if (predictibilityAnalysis != null) {
          deletePredictibilityAnalysis(predictibilityAnalysis);
        }
      }
    }

    fileSystemService.deleteProjectFolder(projectToDelete.getFolderPath());

    // Delete the project itself
    projectRepository.delete(projectToDelete);
    log.info("Deleted project {}", projectId);
    return "Le projet a été bien supprimé";
  }

  /**
   * Updates the form type status of a project.
   *
   * @param projectId         the id of the project to update
   * @param newFormTypeStatus the new form type status
   * @return {@code true} if the status was updated
   * @throws ProjectNotFoundException if the project does not exist
   */
  @Override
  @Transactional
  public boolean updateFormTypeStatus(final Long projectId, final FormProgressStatus newFormTypeStatus)
      throws ProjectNotFoundException {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    FormProgressStatus currentFormTypeStatus = project.getFormProgressStatus();

    if (currentFormTypeStatus != newFormTypeStatus
        && (currentFormTypeStatus == FormProgressStatus.IA
        || currentFormTypeStatus == FormProgressStatus.COLLABORATIVE
        || currentFormTypeStatus == FormProgressStatus.BOTH)) {
      project.setFormProgressStatus(FormProgressStatus.BOTH);
    } else {
      project.setFormProgressStatus(newFormTypeStatus);
    }
    project.setProjectState(ProjectState.IN_PROGRESS);
    projectRepository.save(project);
    return true;
  }

  /**
   * Deletes a {@link PredictibilityAnalysis} and all its related data (execution plan and its votes,
   * votes, brainstormings, predictibility results and linked multi-criteria analysis), after
   * nullifying its methodology foreign key.
   *
   * @param predictibilityAnalysis the predictibility analysis to delete
   */
  private void deletePredictibilityAnalysis(final PredictibilityAnalysis predictibilityAnalysis) {
    // Nullify the foreign key reference to Methodology
    predictibilityAnalysis.setMethodology(null);
    predictibilityAnalysisRepository.save(predictibilityAnalysis);
    // Delete related entities
    if (predictibilityAnalysis.getExecutionPlan() != null) {
      List<ExecutionPlanVote> executionPlanVotes = executionPlanVoteRepo
          .findAllVotesByExecutionPlanId(
              predictibilityAnalysis.getExecutionPlan().getExecutionPlanId());
      executionPlanVoteRepo.deleteAll(executionPlanVotes);
      executionPlanRepository.delete(predictibilityAnalysis.getExecutionPlan());
    }
    if (predictibilityAnalysis.getVotes() != null) {
      voteRepository.deleteAll(predictibilityAnalysis.getVotes());
    }
    if (predictibilityAnalysis.getBrainstormings() != null) {
      brainstormingRepository.deleteAll(predictibilityAnalysis.getBrainstormings());
    }
    if (predictibilityAnalysis.getPredictibilityResults() != null) {
      predictibilityResultsRepository.deleteAll(predictibilityAnalysis.getPredictibilityResults());
    }
    if (predictibilityAnalysis.getMultiCriteriaAnalysis() != null) {
      multiCriteriaAnalysisRepository.delete(predictibilityAnalysis.getMultiCriteriaAnalysis());
    }
    // Delete PredictibilityAnalysis
    predictibilityAnalysisRepository.delete(predictibilityAnalysis);
  }

  /**
   * Computes the multi-criteria analysis status of a project: {@code NOT_STARTED} when the single
   * analysis is still open, {@code IN_PROGRESS} when at least one analysis is unfinished, and
   * {@code DONE} otherwise.
   *
   * @param project the project whose analyses are inspected
   * @return the analysis status label
   */
  private String computeProjectAnalysisStatus(final Project project) {
    if (project.getMultiCriteriaAnalysisList().size() == 1
        && !project.getMultiCriteriaAnalysisList().get(0).getIsFinished()) {
      return ProjectAnalysisState.NOT_STARTED.toString();
    }
    for (MultiCriteriaAnalysis multiCriteriaAnalysis : project.getMultiCriteriaAnalysisList()) {
      if (Boolean.FALSE.equals(multiCriteriaAnalysis.getIsFinished())) {
        return ProjectAnalysisState.IN_PROGRESS.toString();
      }
    }
    return ProjectAnalysisState.DONE.toString();
  }

  /**
   * Builds the list of {@link MulticriteriaAnalysisResponse} of a project by delegating to the
   * multi-criteria context, which owns the resolution of its entity graph into DTOs.
   *
   * @param project the project whose multi-criteria analyses are mapped
   * @return the mapped multi-criteria analyses
   * @throws ExecutionPlanNotFoundException if an execution plan score cannot be found
   * @throws UserNotAuthorizedException     if the user is not authorized to access an execution plan
   */
  private List<MulticriteriaAnalysisResponse> buildMulticriteriaAnalysisList(final Project project)
      throws ExecutionPlanNotFoundException, UserNotAuthorizedException {
    return multiCriteriaService.buildProjectAnalyses(project);
  }

  /**
   * Builds the AI {@link ResultResponse} of a project, leaving it empty when no AI result exists.
   *
   * @param project the project whose AI result is mapped
   * @return the populated (or empty) AI result response
   */
  private ResultResponse buildResultResponse(final Project project) {
    ResultResponse resultAnalyseIa = new ResultResponse();
    Optional<ResultAi> result = resultIaRepository.findByProject(project);
    if (result.isPresent()) {
      resultAnalyseIa.setId(result.get().getId());
      resultAnalyseIa.setProjectId(result.get().getId());
      resultAnalyseIa.setPredictedProba(result.get().getPredictedProba());
      resultAnalyseIa.setPredictedMethod(result.get().getPredictedMethod());
      resultAnalyseIa.setProjectId(result.get().getProject().getProjectId());
    }
    return resultAnalyseIa;
  }

}
