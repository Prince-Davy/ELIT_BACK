package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectPropertyKey;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.ExecutionPlanVote;
import com.elit.outildedecision.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.exception.ProjectFolderAlreadyExistsException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.ProjectOwnerException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.service.AttachmentService;
import com.elit.outildedecision.service.ExecutionPlanService;
import com.elit.outildedecision.service.FileSystemService;
import com.elit.outildedecision.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.service.PermissionService;
import com.elit.outildedecision.service.ProjectService;
import com.elit.outildedecision.service.FeedbackIaService;
import com.elit.outildedecision.service.ResultIaService;
import com.elit.outildedecision.util.*;
import com.elit.outildedecision.model.AttachmentResponseModel;
import com.elit.outildedecision.model.ContributerModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.MulticriteriaAnalyseResponseModel;
import com.elit.outildedecision.model.PredictibilityAnalyseResponseModel;
import com.elit.outildedecision.model.ProjectModel;
import com.elit.outildedecision.model.ProjectResponseModel;
import com.elit.outildedecision.model.ResponsePageProject;
import com.elit.outildedecision.model.ResultAiModel;
import com.elit.outildedecision.model.TriModel;
import com.elit.outildedecision.repository.ResultIaRepository;
import com.elit.outildedecision.repository.VoteRepository;
import com.elit.outildedecision.repository.BrainstormingRepository;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.PredictibilityResultsRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.repository.ExecutionPlanRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.ExecutionPlanVoteRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

/**
 * The ProjectServiceImpl class implements the ProjectService interface and
 * provides the implementation of the service methods for managing projects.
 */
@Service
public class ProjectServiceImpl implements ProjectService {
  /**
   * The PROJECT_NOT_FOUND message.
   */
  static final String PROJECT_NOT_FOUND = "Ce Projet n'existe pas!";
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
   * The executionPlanService for managing the execution plans of the projects.
   */
  private final ExecutionPlanService executionPlanService;

  /**
   * The multiCriteriaService for managing the multi criteria analysis of the projects.
   */
  private final MultiCriteriaAnalysisService multiCriteriaService;

  /**
   * The feedbackIaRepository for accessing the feedback IA objects.
   */
  private final PermissionService permissionService;
  /**
   * The feedbackIaRepository for accessing the feedback IA objects.
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
   * The projectUserPermissionRepository for accessing the project user
   * permission objects in the database.
   */
  private final ProjectUserPermissionRepository projectUserPermissionRepository;

  /**
   * The executionPlanRepository for managing the executionPlan of the projects.
   */
  private final ExecutionPlanRepository executionPlanRepository;

  /**
   * The multiCriteriaAnalysisRepository for managing the multiCriteriaAnalysis of
   * the projects.
   */
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;

  /**
   * The brainstormingRepository for managing the brainstorming of the projects.
   */
  private final BrainstormingRepository brainstormingRepository;

  /**
   * The predictibilityResultsRepository for managing the predictibilityResults of
   * the projects.
   */
  private final PredictibilityResultsRepository predictibilityResultsRepository;

  /**
   * The predictibilityAnalysisRepository for managing the predictibilityAnalysis
   * of the projects.
   */
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;

  /**
   * The voteRepository for managing the vote of the projects.
   */
  private final VoteRepository voteRepository;

  /**
   * The executionPlanVoteRepo for managing the vote of the executionPlan.
   */
  private final ExecutionPlanVoteRepo executionPlanVoteRepo;

  /**
   * The AiAnswerRepository for managing answers in the IA path.
   */
  private final AiAnswerRepository aiAnswerRepository;

  /**
   * The STORING_DIRECTORY for storing the projects files and attachments.
   */
  @Value("${file.upload-dir}")
  private String storingDirectory;

  /**
   * The constructor of the ProjectServiceImpl class. used for dependency
   * injection.
   *
   * @param projectRepository                The projectRepository for accessing
   *                                         the project objects in the database.
   * @param userRepository                   The userRepository for accessing the
   *                                         user objects in the database.
   * @param executionPlanService             The executionPlanService for managing
   *                                         the execution plans of the projects.
   * @param attachmentService                The attachmentService for managing
   *                                         the
   *                                         attachments of the projects.
   * @param fileSystemService                The fileSystemService for managing the file
   *                                         system operations.
   * @param projectUserPermissionRepository  The projectUserPermissionRepository
   *                                         for accessing the project user
   *                                         permission objects in the database.
   * @param resultIaRepository
   * @param multiCriteriaService             The multiCriteriaService for managing
   *                                         the multi criteria analysis of the
   *                                         projects.
   * @param executionPlanRepository          The executionPlanRepository for
   *                                         accessing
   *                                         the executionPlan objects in the
   *                                         database.
   * @param multiCriteriaAnalysisRepository  The multiCriteriaAnalysisRepository
   *                                         for accessing
   *                                         the multiCriteriaAnalysis objects in
   *                                         the database.
   * @param brainstormingRepository          The brainstormingRepository for
   *                                         accessing
   *                                         the brainstorming objects in the
   *                                         database.
   * @param predictibilityResultsRepository  The predictibilityResultsRepository
   *                                         for accessing
   *                                         the predictibilityResults objects in
   *                                         the database.
   * @param predictibilityAnalysisRepository The predictibilityAnalysisRepository
   *                                         for accessing
   *                                         the predictibilityAnalysis objects in
   *                                         the database.
   * @param voteRepository                   The voteRepository for accessing
   *                                         the vote objects in the database.
   * @param executionPlanVoteRepo            to access executionPlanVoteRepo
   * @param aiAnswerRepository               the answerIaRepository for managing answers of the ia path.
   * @param permissionService                The permissionService for managing
   *                                         the permissions.
   * @param feedbackIaService                The feedbackIaService for managing
   *                                         the feedback IA.
   * @param resultIaService                  The resultIaService for managing the
   *                                         result IA.
   */
  public ProjectServiceImpl(final ProjectRepository projectRepository, final UserRepository userRepository,
                            final ExecutionPlanService executionPlanService, final AttachmentService attachmentService,
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
                            final VoteRepository voteRepository, final ExecutionPlanVoteRepo executionPlanVoteRepo,
                            final AiAnswerRepository aiAnswerRepository) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.attachmentService = attachmentService;
    this.executionPlanService = executionPlanService;
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
  }

  /**
   * Persists a new project in the database with the provided data.
   *
   * @param project   the project model containing the data to persist.
   * @param userEmail user email.
   * @return the response model of the newly created project.
   * @throws ProjectFolderAlreadyExistsException   if a folder with the same name
   *                                               already exists.
   * @throws UserNotFoundException                 if the chef or a contributer
   *                                               user cannot be found.
   * @throws ProjectNotFoundException              if the project cannot be found.
   * @throws IOException                           if an I/O error occurs while
   *                                               creating the project directory.
   * @throws ParseException                        if there is an error parsing
   *                                               the multicriteria analysis
   *                                               form.
   * @throws org.json.simple.parser.ParseException if there is an error parsing
   *                                               the multicriteria analysis form
   *                                               JSON.
   */
  @Override
  public ProjectResponseModel persistProject(final ProjectModel project, final String userEmail)
    throws ProjectFolderAlreadyExistsException, UserNotFoundException, ProjectNotFoundException, IOException,
    ParseException, org.json.simple.parser.ParseException, FileCouldNotBeSaved {
    Project projectToSave = new Project();
    // getting the connected user
    UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User chef = userRepository.findByUserEmail(principal.getUsername());
    Date dateOfCreation = new Date();
    String folderName = dateOfCreation.getTime() + "_" + project.getName();
    File projectDirectory = new File(storingDirectory + folderName.replace(" ", "_").toLowerCase());
    if (projectDirectory.exists()) {
      throw new ProjectFolderAlreadyExistsException("Dossier du projet dupliqué, réessayer plus tard.");
    }
    projectDirectory.mkdirs();
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

    List<ContributerModel> contributorsReturn = new ArrayList<>();

    if (Objects.nonNull(project.getContributers())) {
      for (ContributerModel contributer : project.getContributers()) {
        User user = userRepository.findById(contributer.getContributerId())
          .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas."));
        ProjectUserPermission projectUserPermission = new ProjectUserPermission();
        projectUserPermission.setUser(user);
        projectUserPermission.setProject(projectRepository.getById(projectId));
        projectUserPermission.setPermission(contributer.getRole());
        projectUserPermission.setUserFunction(contributer.getFunc());
        projectUserPermission.setId(new ProjectPropertyKey(projectId, user.getUserId()));
        projectUserPermissionRepository.save(projectUserPermission);

        ContributerModel contributerLoaded = new ContributerModel();

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

    ProjectResponseModel projectToReturn = new ProjectResponseModel();
    projectToReturn.setProjectId(projectToSave.getProjectId());
    projectToReturn.setName(projectToSave.getName());
    projectToReturn.setDescription(projectToSave.getDescription());
    projectToReturn.setIsArchived(projectToSave.getIsArchived());
    projectToReturn.setConfirmationState(projectToSave.getConfirmationState());
    projectToReturn.setChefId(projectToSave.getChefId());

    projectToReturn.setContributors(contributorsReturn);
    projectToReturn.setCreatedAt(projectToSave.getCreatedAt());
    projectToReturn.setUpdatedAt(projectToSave.getUpdatedAt());
    projectToReturn.setMultiCriteriaStatus(ProjectAnalysisState.NOT_STARTED.toString());

    return projectToReturn;
  }

  /**
   * Retrieves all projects.
   *
   * @return A list of all the projects.
   */
  @Override
  public List<Project> getAllProjects() {
    return projectRepository.findAll();
  }

  /**
   * The method for searching for a project by a keyword.
   *
   * @param keyword the keyword to search for.
   * @return the list the projects containing the keyword.
   */
  @Override
  public List<Project> getUserProjects(final String keyword) {
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
      return projects.stream()
        .filter(project -> Objects.nonNull(project.getDescription())
          && project.getDescription().contains(keyword) || project.getName().contains(keyword))
        .collect(Collectors.toList());
    }
    return projects;
  }

  /**
   * Updates the project with the given ID using the provided updated project
   * data.
   * This method can be safely overridden in a subclass if the subclass needs to
   * change the way projects are updated. When overriding this method, make sure
   * to
   * call super.updateProject() to ensure the base class functionality is
   * preserved.
   *
   * @param projectId      The ID of the project to update.
   * @param updatedProject The new project data to use for the update.
   * @return The updated project.
   * @throws ProjectNotFoundException If the project with the given ID does not
   *                                  exist.
   */
  @Override
  @Transactional(rollbackFor = {ProjectNotFoundException.class, UserNotFoundException.class})
  public ProjectResponseModel updateProject(final Long projectId, final ProjectModel updatedProject)
    throws ProjectNotFoundException {
    Optional<Project> optionalProject = projectRepository.findById(projectId);
    if (!optionalProject.isPresent()) {
      throw new ProjectNotFoundException(PROJECT_NOT_FOUND);
    }
    Project projectToSave = optionalProject.get();

    // Update project details
    projectToSave.setName(updatedProject.getName());
    projectToSave.setDescription(updatedProject.getDescription());
    // projectToSave.setUpdatedAt(new java.sql.Timestamp(new
    // java.util.Date().getTime()));

    // Save updated project
    projectRepository.save(projectToSave);
    ProjectResponseModel projectToReturn = new ProjectResponseModel();
    projectToReturn.setProjectId(projectToSave.getProjectId());
    projectToReturn.setName(projectToSave.getName());
    projectToReturn.setDescription(projectToSave.getDescription());
    projectToReturn.setIsArchived(projectToSave.getIsArchived());
    projectToReturn.setConfirmationState(projectToSave.getConfirmationState());
    projectToReturn.setChefId(projectToSave.getChefId());

    projectToReturn.setCreatedAt(projectToSave.getCreatedAt());
    projectToReturn.setUpdatedAt(projectToSave.getUpdatedAt());
    projectToReturn.setMultiCriteriaStatus(ProjectAnalysisState.NOT_STARTED.toString());

    return projectToReturn;
  }

  /**
   * The method for getting pageable to sort data.
   *
   * @param triModel Object contains data filter.
   * @return pageable.
   */
  public Pageable getPageableForSort(final TriModel triModel) {
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
   * The method for getting the list of projects of a user.
   *
   * @param triModel Object contains data filter.
   * @return the list of projects of a user.
   */
  public ResponsePageProject getUserProjectList(final TriModel triModel)
    throws UserNotAuthorizedException, ExecutionPlanNotFoundException {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // getting the connected user
    ResponsePageProject responsePageProject = new ResponsePageProject();
    List<ProjectResponseModel> projects = new ArrayList<>();
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

        Optional<User> ownerUserProject = userRepository.findById(project.getChefId());

        List<ProjectUserPermission> userPermissionList = projectUserPermissionPagination.getContent().stream()
          .filter(permission -> permission.getProject().getProjectId().equals(project.getProjectId()))
          .collect(Collectors.toList());

        List<ContributerModel> contributorModelList = new ArrayList<>();
        for (ProjectUserPermission userPermission : userPermissionList) {
          User user = userPermission.getUser();
          contributorModelList.add(new ContributerModel(user.getUserId(), user.getUserFirstName(),
            user.getUserLastName(), null, null, userPermission.getUserFunction(),
            user.getUserAvatarPath(),
            userPermission.getPermission()));
        }

        String projectStatus;
        if (project.getMultiCriteriaAnalysisList().size() == 1
          && !project.getMultiCriteriaAnalysisList().get(0).getIsFinished()) {
          projectStatus = ProjectAnalysisState.NOT_STARTED.toString();
        } else {
          boolean isFinished = true;
          for (MultiCriteriaAnalysis multiCriteriaAnalysis : project.getMultiCriteriaAnalysisList()) {
            if (!multiCriteriaAnalysis.getIsFinished()) {
              isFinished = false;
              break;
            }
          }

          if (isFinished) {
            projectStatus = ProjectAnalysisState.DONE.toString();
          } else {
            projectStatus = ProjectAnalysisState.IN_PROGRESS.toString();
          }
        }

        List<MulticriteriaAnalyseResponseModel> multicriteriaAnalysisList = new ArrayList<>();
        for (MultiCriteriaAnalysis multicriteriaAnalysis : project.getMultiCriteriaAnalysisList()) {
          PredictibilityAnalyseResponseModel predictibilityAnalyse = null;
          PredictibilityAnalysis predictibilityAnalysis = multicriteriaAnalysis.getPredictibilityAnalysis();
          if (predictibilityAnalysis != null) {
            ExecutionPlanScoreResponseModel executionPlanScore = null;
            if (predictibilityAnalysis.getExecutionPlan() != null) {
              executionPlanScore = executionPlanService
                .getExecutionPlanScore(
                  predictibilityAnalysis.getExecutionPlan().getExecutionPlanId());
            }
            predictibilityAnalyse = new PredictibilityAnalyseResponseModel(
              predictibilityAnalysis.getPredictibilityAnalysisId(),
              predictibilityAnalysis.getIsFinished(),
              predictibilityAnalysis.getPredictibilityAnalysisIteration(),
              predictibilityAnalysis.getChosenMethodology(),
              predictibilityAnalysis.getBrainstormings(),
              predictibilityAnalysis.getVotes(),
              executionPlanScore);
          }
          multicriteriaAnalysisList.add(new MulticriteriaAnalyseResponseModel(
            multicriteriaAnalysis.getMultiCriteriaAnalysisId(), multicriteriaAnalysis.getCreatedAt(),
            multicriteriaAnalysis.getUpdatedAt(),
            multicriteriaAnalysis.getMultiCriteriaAnalysisIteration(),
            multicriteriaAnalysis.getIsFinished(),
            new ArrayList<>(multicriteriaAnalysis.getFormSteps()),
            predictibilityAnalyse));
        }

        // A.I. results
        Optional<ResultAi> result = resultIaRepository.findByProject(project);
        ResultAiModel resultAnalyseIa = new ResultAiModel();
        if (result.isPresent()) {
          resultAnalyseIa.setId(result.get().getId());
          resultAnalyseIa.setProjectId(result.get().getId());
          resultAnalyseIa.setPredictedProba(result.get().getPredictedProba());
          resultAnalyseIa.setPredictedMethod(result.get().getPredictedMethod());
          resultAnalyseIa.setProjectId(result.get().getProject().getProjectId());
        }

        projects.add(new ProjectResponseModel(
          project.getProjectId(),
          project.getName(),
          project.getDescription(),
          project.getCreatedAt(),
          project.getUpdatedAt(),
          project.getConfirmationState(),
          project.getChefId(),
          ownerUserProject.get().getUserLastName(),
          ownerUserProject.get().getUserFirstName(),
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
    responsePageProject.setProjectsPage(new PageImpl<>(projects, pageable, projectUserPermissionPagination.getTotalElements()));
    responsePageProject.setTotalProjetCreated(projectUserPermissionRepository.getTotalProjectCreatedUser(triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername()));
    responsePageProject.setTotalProjetAffected(projectUserPermissionRepository.getTotalProjectAffectedUser(triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername()));
    return responsePageProject;
  }

  private Page<ProjectUserPermission> getUserAffectedProject(TriModel triModel, UserDetails userDetails, Pageable pageable) {
    if (Constants.AUDIT.equalsIgnoreCase(triModel.getTypeAnalyse())) {
      return projectUserPermissionRepository.findProjectWithAuditAffected(triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername(), pageable);
    } else {
      return projectUserPermissionRepository.findProjectAffected(triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername(), pageable);
    }
  }

  private Page<ProjectUserPermission> getUserCreatedProject(TriModel triModel, UserDetails userDetails, Pageable pageable) {
    if (Constants.AUDIT.equalsIgnoreCase(triModel.getTypeAnalyse())) {
      return projectUserPermissionRepository.findProjectWithAuditCreatedByUser(triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername(), pageable);
    } else {
      return projectUserPermissionRepository.findProjectCreatedByUser(triModel.getKey(), triModel.getTypeAnalyse(), triModel.getStatus(), userDetails.getUsername(), pageable);
    }
  }

  /**
   * Updates the confirmation state of a project to either CONFIRMED or REJECTED
   * based on the value of the "isConfirmed" field in the HttpEntity body. If
   * "isConfirmed" is true, the project's confirmation state is set to CONFIRMED.
   * If "isConfirmed" is false, the project's confirmation state is set to
   * REJECTED.
   *
   * @param id          the id of the project to update
   * @param isConfirmed the HttpEntity containing the "isConfirmed" field as a
   *                    JSON object
   * @return a ResponseEntity object with a success or error message
   * @throws ProjectNotFoundException if the project with the given id does not
   *                                  exist
   * @throws IOException              if there is an error parsing the HttpEntity
   *                                  body
   */
  @Override
  public ResponseEntity<String> validateOrRejectProject(final Long id, final HttpEntity<String> isConfirmed)
    throws ProjectNotFoundException, IOException {
    Project projectToConfirm = projectRepository.findById(id)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    ProjectConfirmationState projectConfirmationState = ProjectConfirmationState
      .valueOf(projectToConfirm.getConfirmationState());

    Map<String, Boolean> result = new ObjectMapper().readValue(isConfirmed.getBody(),
      new ObjectMapper().getTypeFactory().constructMapType(HashMap.class, String.class, Boolean.class));
    if (Boolean.TRUE.equals(result.get("isConfirmed"))) {
      if (projectConfirmationState == ProjectConfirmationState.CONFIRMED) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce projet est déjà validé");
      } else {
        projectToConfirm.setConfirmationState(ProjectConfirmationState.CONFIRMED.name());
        projectRepository.save(projectToConfirm);
        return ResponseEntity.ok().body("Le Projet a été validé");
      }
    } else {
      if (projectConfirmationState == ProjectConfirmationState.REJECTED) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce projet est déjà rejected");
      } else {
        projectToConfirm.setConfirmationState(ProjectConfirmationState.REJECTED.name());
        projectRepository.save(projectToConfirm);
        return ResponseEntity.ok().body("Le Projet a été refusé");
      }
    }
  }

  /**
   * Adds a new contributor to the specified project.
   *
   * @param projectId   the ID of the project to add the contributor to
   * @param contributor the contributor to add to the project
   * @return a ResponseEntity with a success message if the operation was
   * successful
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws UserNotFoundException    if the specified user does not exist
   */
  @Override
  public ResponseEntity<String> addProjectContributor(final Long projectId, final ContributerModel contributor)
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

    return ResponseEntity.ok().body("Le collaborateur a été bien enregistré");
  }

  /**
   * Removes a contributor from a project. If the contributor is the owner of the
   * project, this method throws a ProjectOwnerException. If the contributor does
   * not exist in the project, this method throws a UserNotFoundException.
   *
   * @param projectId     the ID of the project
   * @param contributorID the ID of the contributor to be removed
   * @return a ResponseEntity with a message indicating that the contributor has
   * been removed
   * @throws ProjectNotFoundException if the project with the given ID does not
   *                                  exist
   * @throws UserNotFoundException    if the user with the given ID does not exist
   *                                  in the project
   * @throws ProjectOwnerException    if the contributor to be removed is the
   *                                  owner of the project
   */
  @Override
  public ResponseEntity<String> deleteProjectContributor(final Long projectId, final Long contributorID)
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

    return ResponseEntity.ok().body("Le collaborateur a été bien retiré du projet");
  }

  /**
   * Retrieves the data for the project with the specified ID.
   *
   * @param projectId the ID of the project to retrieve data for
   * @return a ProjectResponseModel containing the data for the specified project
   * @throws ProjectNotFoundException       if the specified project cannot be
   *                                        found
   * @throws UserNotAuthorizedException     if the user is not authorized to vote
   *                                        in the specified execution plan
   * @throws ExecutionPlanNotFoundException if the specified execution plan cannot
   *                                        be found
   */
  @Override
  public ProjectResponseModel getProjectData(final Long projectId)
    throws ProjectNotFoundException, UserNotAuthorizedException, ExecutionPlanNotFoundException {
    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    List<ProjectUserPermission> userPermissionList = project.getProjectUserPermissions();

    List<ContributerModel> contributorModelList = new ArrayList<>();
    for (ProjectUserPermission userPermission : userPermissionList) {
      User user = userPermission.getUser();
      contributorModelList
        .add(new ContributerModel(user.getUserId(), user.getUserFirstName(), user.getUserLastName(),
          user.getUserEmail(), user.getIsAdmin(), userPermission.getUserFunction(),
          user.getUserAvatarPath(),
          userPermission.getPermission()));
    }

    List<AttachmentResponseModel> attachmentList = new ArrayList<>();
    for (Attachment attachment : project.getAttachments()) {
      attachmentList.add(new AttachmentResponseModel(attachment.getAttachmentId(), attachment.getFileName(),
        attachment.getFilePath(), attachment.getContentType(), attachment.getCreatedAt(),
        attachment.getUpdatedAt(),
        attachment.getCreator().getUserFirstName(), attachment.getCreator().getUserLastName()));
    }

    List<MulticriteriaAnalyseResponseModel> multicriteriaAnalysisList = new ArrayList<>();
    for (MultiCriteriaAnalysis multicriteriaAnalysis : project.getMultiCriteriaAnalysisList()) {
      PredictibilityAnalyseResponseModel predictibilityAnalyse = null;
      PredictibilityAnalysis predictibilityAnalysis = multicriteriaAnalysis.getPredictibilityAnalysis();
      if (predictibilityAnalysis != null) {
        ExecutionPlanScoreResponseModel executionPlanScore = null;
        if (predictibilityAnalysis.getExecutionPlan() != null) {
          executionPlanScore = executionPlanService
            .getExecutionPlanScore(predictibilityAnalysis.getExecutionPlan().getExecutionPlanId());
        }
        predictibilityAnalyse = new PredictibilityAnalyseResponseModel(
          predictibilityAnalysis.getPredictibilityAnalysisId(), predictibilityAnalysis.getIsFinished(),
          predictibilityAnalysis.getPredictibilityAnalysisIteration(),
          predictibilityAnalysis.getChosenMethodology(),
          predictibilityAnalysis.getBrainstormings(),
          predictibilityAnalysis.getVotes(),
          executionPlanScore);
      }
      multicriteriaAnalysisList.add(new MulticriteriaAnalyseResponseModel(
        multicriteriaAnalysis.getMultiCriteriaAnalysisId(), multicriteriaAnalysis.getCreatedAt(),
        multicriteriaAnalysis.getUpdatedAt(), multicriteriaAnalysis.getMultiCriteriaAnalysisIteration(),
        multicriteriaAnalysis.getIsFinished(), new ArrayList<>(multicriteriaAnalysis.getFormSteps()),
        predictibilityAnalyse));
    }

    return new ProjectResponseModel(project.getProjectId(), project.getName(), project.getDescription(),
      project.getCreatedAt(), project.getUpdatedAt(), project.getConfirmationState(), project.getChefId(),
      null, null,
      project.getIsArchived(), project.getFormProgressStatus(), project.getProjectState(), contributorModelList, attachmentList,
      multicriteriaAnalysisList, null, null);
  }

  /**
   * Deletes a project and all its related data from the system.
   *
   * @param projectId The ID of the project to be deleted.
   * @return ResponseEntity with a message indicating the success of the
   * operation.
   * @throws ProjectNotFoundException if the project with the given ID does not
   *                                  exist.
   */
  @Override
  @Transactional
  public ResponseEntity<String> deleteProject(final Long projectId) throws ProjectNotFoundException, AttachmentNotFoundException, IOException, ResponseNotFoundException {
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
      }
    }

    fileSystemService.deleteProjectFolder(projectToDelete.getFolderPath());

    // Delete the project itself
    projectRepository.delete(projectToDelete);
    return ResponseEntity.ok().body("Le projet a été bien supprimé");
  }

  /**
   * Updates the form type status of a project.
   *
   * @param projectId         the ID of the project to update
   * @param newFormTypeStatus the new form type status to set
   * @return a ResponseEntity indicating the success or failure of the update
   * operation
   * @throws ProjectNotFoundException if the specified project does not exist
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

}
