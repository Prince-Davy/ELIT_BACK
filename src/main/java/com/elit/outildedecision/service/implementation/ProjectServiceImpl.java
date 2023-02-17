package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ProjectOwnerException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.*;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.AttachmentService;
import com.elit.outildedecision.service.ExecutionPlanService;
import com.elit.outildedecision.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.service.ProjectService;
import com.elit.outildedecision.util.ProjectConfirmationState;
import com.elit.outildedecision.util.ProjectPermession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
// @Transactional
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;
  private final AttachmentService attachmentService;
  private final ExecutionPlanService executionPlanService;
  private final ProjectUserPermissionRepository projectUserPermissionRepository;
  private final MultiCriteriaAnalysisService multiCriteriaService;
  @Autowired EntityManager entityManager;
  @Value("${file.upload-dir}")
  String STORING_DIRECTORY;

  public ProjectServiceImpl(
      ProjectRepository projectRepository,
      UserRepository userRepository,
      ExecutionPlanService executionPlanService,
      AttachmentService attachmentService,
      ProjectUserPermissionRepository projectUserPermissionRepository,
      MultiCriteriaAnalysisService multiCriteriaService) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
    this.attachmentService = attachmentService;
    this.executionPlanService = executionPlanService;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
    this.multiCriteriaService = multiCriteriaService;
  }

  /**
   * @param project
   * @return ProjectResponseModel
   * @throws Exception
   */
  @Override
  public ProjectResponseModel persistProject(ProjectModel project) throws Exception {
    Project projectToSave = new Project();
    // getting the connected user
    String chefEmail =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User chef = userRepository.findByUserEmail(chefEmail);
    Date dateOfCreation = new Date();
    String folderName = dateOfCreation.getTime() + "_" + project.getName();
    File projectDirectory =
        new File(STORING_DIRECTORY + folderName.replaceAll(" ", "_").toLowerCase());
    if (projectDirectory.exists()) {
      throw new Exception("Dossier du projet dupliqué, réessayer plus tard.");
    }
    projectDirectory.mkdirs();
    projectToSave.setFolderPath(folderName.replaceAll(" ", "_").toLowerCase());
    projectToSave.setChefId(chef.getUserId());
    projectToSave.setDescription(project.getDescription());
    projectToSave.setName(project.getName());
    projectToSave.setConfirmationState(ProjectConfirmationState.WAITING.name());
    projectToSave.setIsArchived(false);
    Long projectId = projectRepository.save(projectToSave).getProjectId();
    // adding permissions to project
    if (Objects.nonNull(project.getContributers())) {
      for (ContributerModel contributer : project.getContributers()) {
        User user =
            userRepository
                .findById(contributer.getContributerId())
                .orElseThrow(() -> new Exception("Cet utilisateur n'existe pas."));
        ProjectUserPermission projectUserPermission = new ProjectUserPermission();
        projectUserPermission.setUser(user);
        projectUserPermission.setProject(projectRepository.getById(projectId));
        projectUserPermission.setPermission(
            user.getUserId().equals(chef.getUserId())
                ? ProjectPermession.CDP.name()
                : contributer.getRole()); // TODO : à fixer ajout du CDP
        projectUserPermission.setId(new ProjectPropertyKey(projectId, user.getUserId()));
        projectUserPermissionRepository.save(projectUserPermission);
      }
    }
    if (Objects.nonNull(project.getFiles())) {
      attachmentService.uploadAttachment(project.getFiles(), projectId);
    }

    multiCriteriaService.createMulticriteriaAnalysisForm(projectId);

    ProjectResponseModel projectToReturn = new ProjectResponseModel();
    projectToReturn.setProjectId(projectToSave.getProjectId());
    projectToReturn.setName(projectToSave.getName());
    projectToReturn.setDescription(projectToSave.getDescription());
    projectToReturn.setIsArchived(projectToSave.getIsArchived());
    projectToReturn.setConfirmationState(projectToSave.getConfirmationState());
    projectToReturn.setContributors(project.getContributers());
    projectToReturn.setCreatedAt(projectToSave.getCreatedAt());
    projectToReturn.setUpdatedAt(projectToSave.getUpdatedAt());

    return projectToReturn;
  }

  @Override
  public List<Project> getAllProjects() {
    return projectRepository.findAll();
  }

  // @PreAuthorize("isPermittedForTheProject('MANAGER', #projectId)")
  @Override
  public Project getProjectById(Long projectId) throws ProjectNotFoundException {
    return projectRepository
        .findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas!"));
  }

  @Override
  public List<Project> getUserProjects(String keyword) {
    String principal =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    System.out.println("principal " + principal);

    List<Project> projects = new ArrayList<>();
    List<ProjectUserPermission> projectUserPermissions =
        projectUserPermissionRepository.findAll().stream()
            .filter(
                projectUserPermission ->
                    projectUserPermission.getUser().getUserEmail().equals(principal))
            .collect(Collectors.toList());
    System.out.println("projectUserPermissions = " + projectUserPermissions);

    if (!projectUserPermissions.isEmpty()) {
      for (ProjectUserPermission projectUserPermission : projectUserPermissions) {
        System.out.println(
            "projectUserPermission.getProject() " + projectUserPermission.getProject());
        projects.add(projectUserPermission.getProject());
      }
    }
    if (keyword != null) {
      if (!"".equals(keyword)) {
        return projects.stream()
            .filter(
                project ->
                    (Objects.nonNull(project.getDescription())
                            && project.getDescription().contains(keyword))
                        || project.getName().contains(keyword))
            .collect(Collectors.toList());
      }
    }
    System.out.println("projects " + projects);
    return projects;
  }

  @Override
  public List<ProjectResponseModel> getUserProjectList(String keyword) {
    String principal =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    List<ProjectResponseModel> projects = new ArrayList<>();
    List<ProjectUserPermission> projectUserPermissions =
        projectUserPermissionRepository.findAll().stream()
            .filter(
                projectUserPermission ->
                    projectUserPermission.getUser().getUserEmail().equals(principal))
            .collect(Collectors.toList());

    if (!projectUserPermissions.isEmpty()) {
      for (ProjectUserPermission projectUserPermission : projectUserPermissions) {
        Project project = projectUserPermission.getProject();

        List<ProjectUserPermission> userPermissionList =
            projectUserPermissionRepository.findAll().stream()
                .filter(
                    permission ->
                        permission.getProject().getProjectId().equals(project.getProjectId()))
                .collect(Collectors.toList());

        ArrayList<ContributerModel> contributorModelList = new ArrayList<>();
        for (ProjectUserPermission userPermission : userPermissionList) {
          User user = userPermission.getUser();
          contributorModelList.add(
              new ContributerModel(
                  user.getUserId(),
                  user.getUserFirstName(),
                  user.getUserLastName(),
                  null,
                  null,
                  null,
                  userPermission.getPermission()));
        }

        projects.add(
            new ProjectResponseModel(
                project.getProjectId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                project.getConfirmationState(),
                null,
                null,
                contributorModelList,
                null,
                null));
      }
    }
    if (keyword != null) {
      if (!"".equals(keyword)) {
        return projects.stream()
            .filter(
                project ->
                    (Objects.nonNull(project.getDescription())
                            && project.getDescription().contains(keyword))
                        || project.getName().contains(keyword))
            .collect(Collectors.toList());
      }
    }
    return projects;
  }

  @Override
  public ResponseEntity<String> validateOrRejectProject(Long id, HttpEntity<String> isConfirmed)
      throws ProjectNotFoundException, IOException {
    Project projectToConfirm =
        projectRepository
            .findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas!"));

    ProjectConfirmationState projectConfirmationState =
        ProjectConfirmationState.valueOf(projectToConfirm.getConfirmationState());

    Map<String, Boolean> result =
        new ObjectMapper().readValue(isConfirmed.getBody(), HashMap.class);
    if (result.get("isConfirmed")) {
      switch (projectConfirmationState) {
        case CONFIRMED:
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce projet est déjà validé");
        default:
          projectToConfirm.setConfirmationState(ProjectConfirmationState.CONFIRMED.name());
          projectRepository.save(projectToConfirm);
          return ResponseEntity.ok().body("Le Projet a été validé");
      }
    } else {
      switch (projectConfirmationState) {
        case REJECTED:
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce projet est déjà rejected");
        default:
          projectToConfirm.setConfirmationState(ProjectConfirmationState.REJECTED.name());
          projectRepository.save(projectToConfirm);
          return ResponseEntity.ok().body("Le Projet a été refusé");
      }
    }
  }

  @Override
  public ResponseEntity<String> addProjectContributor(Long projectId, ContributerModel contributor)
      throws Exception {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas! "));

    User user =
        userRepository
            .findById(contributor.getContributerId())
            .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas !"));

    ProjectUserPermission projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setUser(user);
    projectUserPermission.setProject(project);
    projectUserPermission.setPermission(contributor.getRole());
    projectUserPermission.setId(new ProjectPropertyKey(projectId, user.getUserId()));
    projectUserPermissionRepository.save(projectUserPermission);

    return ResponseEntity.ok().body("Le collaborateur a été bien enregistré");
  }

  @Override
  public ResponseEntity<String> deleteProjectContributor(Long projectId, Long contributorID)
      throws Exception {
    projectRepository
        .findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas!"));

    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(projectId, contributorID);

    if (Objects.isNull(projectUserPermission)) {
      throw new UserNotFoundException("Cet utilisateur n'existe pas dans le projet !");
    }
    System.out.println("projectUserPermission " + projectUserPermission);

    if (projectUserPermission.getPermission().contains(ProjectPermession.CDP.name())) {
      throw new ProjectOwnerException(
          "Cet utilisateur ne peut pas etre elimine car c'est le chef du projet!");
    }

    projectUserPermissionRepository.deleteByProjectUser(projectId, contributorID);

    return ResponseEntity.ok().body("Le collaborateur a été bien retiré du projet");
  }

  @Override
  public ProjectResponseModel getProjectData(Long projectId) throws ProjectNotFoundException {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas!"));

    List<ProjectUserPermission> userPermissionList = project.getProjectUserPermissions();

    ArrayList<ContributerModel> contributorModelList = new ArrayList<>();
    for (ProjectUserPermission userPermission : userPermissionList) {
      User user = userPermission.getUser();
      contributorModelList.add(
          new ContributerModel(
              user.getUserId(),
              user.getUserFirstName(),
              user.getUserLastName(),
              user.getUserEmail(),
              user.getUserJob(),
              user.getIsAdmin(),
              userPermission.getPermission()));
    }

    ArrayList<AttachmentResponseModel> attachmentList = new ArrayList<>();
    for (Attachment attachment : project.getAttachments()) {
      attachmentList.add(
          new AttachmentResponseModel(
              attachment.getAttachmentId(),
              attachment.getFileName(),
              attachment.getFilePath(),
              attachment.getContentType()));
    }

    ArrayList<MulticriteriaAnalyseResponseModel> multicriteriaAnaliseList = new ArrayList<>();
    for (MultiCriteriaAnalysis multicriteriaAnalise : project.getMultiCriteriaAnalysisList()) {
      PredictibilityAnalyseResponseModel predictibilityAnalyse = null;
      PredictibilityAnalysis predictibilityAnalysis =
          multicriteriaAnalise.getPredictibilityAnalysis();
      if (predictibilityAnalysis != null) {
        ExecutionPlanScoreResponseModel executionPlanScore = null;
        if (predictibilityAnalysis.getExecutionPlan() != null) {
          executionPlanScore =
              executionPlanService.getExecutionPlanScore(
                  predictibilityAnalysis.getExecutionPlan().getExecutionPlanId());
        }
        predictibilityAnalyse =
            new PredictibilityAnalyseResponseModel(
                predictibilityAnalysis.getPredictibilityAnalysisId(),
                predictibilityAnalysis.getIsFinished(),
                predictibilityAnalysis.getPredictibilityAnalysisIteration(),
                executionPlanScore);
      }
      multicriteriaAnaliseList.add(
          new MulticriteriaAnalyseResponseModel(
              multicriteriaAnalise.getMultiCriteriaAnalysisId(),
              multicriteriaAnalise.getCreatedAt(),
              multicriteriaAnalise.getUpdatedAt(),
              multicriteriaAnalise.getMultiCriteriaAnalysisIteration(),
              multicriteriaAnalise.getIsFinished(),
              new ArrayList(multicriteriaAnalise.getFormSteps()),
              predictibilityAnalyse));
    }

    return new ProjectResponseModel(
        project.getProjectId(),
        project.getName(),
        project.getDescription(),
        project.getCreatedAt(),
        project.getUpdatedAt(),
        project.getConfirmationState(),
        project.getChefId(),
        project.getIsArchived(),
        contributorModelList,
        attachmentList,
        multicriteriaAnaliseList);
  }

  @Override
  @Transactional
  public ResponseEntity<String> deleteProject(Long projectId)
      throws ProjectNotFoundException, IOException {
    Project projectToDelete =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce project n'existe pas !"));

    List<ProjectUserPermission> projectUserPermissionList =
        projectToDelete.getProjectUserPermissions();

    for (ProjectUserPermission pup : projectUserPermissionList) {
      pup.getUser().detachPermission(pup);
      projectUserPermissionRepository.delete(pup);
    }
    projectRepository.delete(projectToDelete);
    return ResponseEntity.ok().body("Le projet a été bien supprimé");
  }

  @Override
  public Project updateProject(Long id, Project project) throws ProjectNotFoundException {
    Project projectFromDB =
        projectRepository
            .findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Ce Projet n'existe pas!"));
    projectFromDB.setName(project.getName());
    projectFromDB.setDescription(project.getDescription());
    projectFromDB.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    return projectRepository.save(projectFromDB);
  }
}
