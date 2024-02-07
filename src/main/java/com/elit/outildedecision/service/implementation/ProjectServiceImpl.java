package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Attachment;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectPropertyKey;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.exception.ProjectFolderAlreadyExistsException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ProjectOwnerException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.AttachmentResponseModel;
import com.elit.outildedecision.model.ContributerModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.MulticriteriaAnalyseResponseModel;
import com.elit.outildedecision.model.PredictibilityAnalyseResponseModel;
import com.elit.outildedecision.model.ProjectModel;
import com.elit.outildedecision.model.ProjectResponseModel;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.AttachmentService;
import com.elit.outildedecision.service.ExecutionPlanService;
import com.elit.outildedecision.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.service.ProjectService;
import com.elit.outildedecision.util.ProjectAnalysisState;
import com.elit.outildedecision.util.ProjectConfirmationState;
import com.elit.outildedecision.util.ProjectPermession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The ProjectServiceImpl class implements the ProjectService interface and
 * provides the implementation of the service methods for managing projects.
 */
@Service
public class ProjectServiceImpl implements ProjectService {
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
     * The projectUserPermissionRepository for accessing the project user permission
     * objects in the database.
     */
    private final ProjectUserPermissionRepository projectUserPermissionRepository;
    /**
     * The multiCriteriaService for managing the multi criteria analysis of the
     * projects.
     */
    private final MultiCriteriaAnalysisService multiCriteriaService;
    /**
     * The STORING_DIRECTORY for storing the projects files and attachments.
     */
    @Value("${file.upload-dir}")
    private String storingDirectory;
    /**
     * The PROJECT_NOT_FOUND message.
     */
    private static final String PROJECT_NOT_FOUND = "Ce Projet n'existe pas!";

    /**
     * The constructor of the ProjectServiceImpl class. used for dependency
     * injection.
     *
     * @param projectRepository               The projectRepository for accessing
     *                                        the project objects in the database.
     * @param userRepository                  The userRepository for accessing the
     *                                        user objects in the database.
     * @param executionPlanService            The executionPlanService for managing
     *                                        the execution plans of the projects.
     * @param attachmentService               The attachmentService for managing the
     *                                        attachments of the projects.
     * @param projectUserPermissionRepository The projectUserPermissionRepository
     *                                        for accessing the project user
     *                                        permission objects in the database.
     * @param multiCriteriaService            The multiCriteriaService for managing
     *                                        the multi criteria analysis of the
     *                                        projects.
     */
    public ProjectServiceImpl(final ProjectRepository projectRepository, final UserRepository userRepository,
            final ExecutionPlanService executionPlanService, final AttachmentService attachmentService,
            final ProjectUserPermissionRepository projectUserPermissionRepository,
            final MultiCriteriaAnalysisService multiCriteriaService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.attachmentService = attachmentService;
        this.executionPlanService = executionPlanService;
        this.projectUserPermissionRepository = projectUserPermissionRepository;
        this.multiCriteriaService = multiCriteriaService;
    }

    /**
     *
     * Persists a new project in the database with the provided data.
     *
     * @param project the project model containing the data to persist.
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
        Long projectId = projectRepository.save(projectToSave).getProjectId();

        // adding permissions to project chef and contributers
        ProjectUserPermission projectChefPermission = new ProjectUserPermission();
        projectChefPermission.setUser(chef);
        projectChefPermission.setProject(projectRepository.getById(projectId));
        projectChefPermission.setPermission(ProjectPermession.CDP.name());
        projectChefPermission.setId(new ProjectPropertyKey(projectId, chef.getUserId()));
        projectUserPermissionRepository.save(projectChefPermission);

        ArrayList<ContributerModel> contributorsReturn = new ArrayList<>();

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
     * The method for getting the list of projects of a user.
     *
     * @param keyword the keyword to search for.
     * @return the list of projects of a user.
     */
    @Override
    public List<ProjectResponseModel> getUserProjectList(final String keyword) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      // getting the connected user
      UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User chef = userRepository.findByUserEmail(principal.getUsername());

        List<ProjectResponseModel> projects = new ArrayList<>();
        List<ProjectUserPermission> projectUserPermissions = projectUserPermissionRepository.findAll().stream()
                .filter(projectUserPermission -> projectUserPermission.getUser().getUserEmail()
                        .equals(userDetails.getUsername()))
                .collect(Collectors.toList());

        if (!projectUserPermissions.isEmpty()) {
            for (ProjectUserPermission projectUserPermission : projectUserPermissions) {
                Project project = projectUserPermission.getProject();

                List<ProjectUserPermission> userPermissionList = projectUserPermissionRepository.findAll().stream()
                        .filter(permission -> permission.getProject().getProjectId().equals(project.getProjectId()))
                        .collect(Collectors.toList());

                ArrayList<ContributerModel> contributorModelList = new ArrayList<>();
                for (ProjectUserPermission userPermission : userPermissionList) {
                    User user = userPermission.getUser();
                    if (!(chef.equals(user))) {
                      contributorModelList.add(new ContributerModel(user.getUserId(), user.getUserFirstName(),
                        user.getUserLastName(), null, null, userPermission.getUserFunction(), user.getUserAvatarPath(), userPermission.getPermission()));
                    }

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

                projects.add(new ProjectResponseModel(project.getProjectId(), project.getName(),
                        project.getDescription(), project.getCreatedAt(), project.getUpdatedAt(),
                        project.getConfirmationState(), project.getChefId(), null, contributorModelList, null, null, projectStatus));
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

        Map<String, Boolean> result = new ObjectMapper().readValue(isConfirmed.getBody(), HashMap.class);
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
     *         successful
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
     *         been removed
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

        ArrayList<ContributerModel> contributorModelList = new ArrayList<>();
        for (ProjectUserPermission userPermission : userPermissionList) {
            User user = userPermission.getUser();
            contributorModelList
                    .add(new ContributerModel(user.getUserId(), user.getUserFirstName(), user.getUserLastName(),
                          user.getUserEmail(), user.getIsAdmin(), userPermission.getUserFunction(), user.getUserAvatarPath(), userPermission.getPermission()));
        }

        ArrayList<AttachmentResponseModel> attachmentList = new ArrayList<>();
        for (Attachment attachment : project.getAttachments()) {
            attachmentList.add(new AttachmentResponseModel(attachment.getAttachmentId(), attachment.getFileName(),
                    attachment.getFilePath(), attachment.getContentType(), attachment.getCreatedAt(), attachment.getUpdatedAt(), attachment.getCreator().getUserFirstName(), attachment.getCreator().getUserLastName()));
        }

        ArrayList<MulticriteriaAnalyseResponseModel> multicriteriaAnaliseList = new ArrayList<>();
        for (MultiCriteriaAnalysis multicriteriaAnalise : project.getMultiCriteriaAnalysisList()) {
            PredictibilityAnalyseResponseModel predictibilityAnalyse = null;
            PredictibilityAnalysis predictibilityAnalysis = multicriteriaAnalise.getPredictibilityAnalysis();
            if (predictibilityAnalysis != null) {
                ExecutionPlanScoreResponseModel executionPlanScore = null;
                if (predictibilityAnalysis.getExecutionPlan() != null) {
                    executionPlanScore = executionPlanService
                            .getExecutionPlanScore(predictibilityAnalysis.getExecutionPlan().getExecutionPlanId());
                }
                predictibilityAnalyse = new PredictibilityAnalyseResponseModel(
                        predictibilityAnalysis.getPredictibilityAnalysisId(), predictibilityAnalysis.getIsFinished(),
                        predictibilityAnalysis.getPredictibilityAnalysisIteration(), executionPlanScore);
            }
            multicriteriaAnaliseList.add(new MulticriteriaAnalyseResponseModel(
                    multicriteriaAnalise.getMultiCriteriaAnalysisId(), multicriteriaAnalise.getCreatedAt(),
                    multicriteriaAnalise.getUpdatedAt(), multicriteriaAnalise.getMultiCriteriaAnalysisIteration(),
                    multicriteriaAnalise.getIsFinished(), new ArrayList<>(multicriteriaAnalise.getFormSteps()),
                    predictibilityAnalyse));
        }

        return new ProjectResponseModel(project.getProjectId(), project.getName(), project.getDescription(),
                project.getCreatedAt(), project.getUpdatedAt(), project.getConfirmationState(), project.getChefId(),
                project.getIsArchived(), contributorModelList, attachmentList, multicriteriaAnaliseList, null);
    }

    /**
     * Deletes a project and all its related data from the system.
     *
     * @param projectId The ID of the project to be deleted.
     * @return ResponseEntity with a message indicating the success of the
     *         operation.
     * @throws ProjectNotFoundException if the project with the given ID does not
     *                                  exist.
     */
    @Override
    @Transactional
    public ResponseEntity<String> deleteProject(final Long projectId) throws ProjectNotFoundException {
        Project projectToDelete = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Ce project n'existe pas !"));

        List<ProjectUserPermission> projectUserPermissionList = projectToDelete.getProjectUserPermissions();

        for (ProjectUserPermission pup : projectUserPermissionList) {
            pup.getUser().detachPermission(pup);
            projectUserPermissionRepository.delete(pup);
        }
        projectRepository.delete(projectToDelete);
        return ResponseEntity.ok().body("Le projet a été bien supprimé");
    }
}
