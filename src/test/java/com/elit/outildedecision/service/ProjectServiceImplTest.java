package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.*;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.elit.outildedecision.service.implementation.ProjectServiceImpl;
import com.elit.outildedecision.util.ProjectAnalysisState;
import com.elit.outildedecision.util.ProjectConfirmationState;
import com.elit.outildedecision.util.ProjectPermession;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
  @InjectMocks
  private ProjectServiceImpl projectServiceImpl;
  @Mock
  UserRepository userRepository;
  @Mock
  ProjectRepository projectRepository;

  @Mock
  MultiCriteriaAnalysisService multiCriteriaService;
  @Mock
  ProjectUserPermissionRepository projectUserPermissionRepository;

  User user;
  Project projectToSave;
  ProjectUserPermission projectUserPermission;

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");

    projectToSave = new Project();
    projectUserPermission = new ProjectUserPermission();
    projectUserPermission.setProject(projectToSave);
    projectUserPermission.setUser(user);
    projectUserPermission.setPermission(ProjectPermession.CDP.name());
  }

  @Test
  void persistProject() throws UserNotFoundException, ProjectNotFoundException, ProjectFolderAlreadyExistsException, FileCouldNotBeSaved, IOException, ParseException, org.json.simple.parser.ParseException {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    ArrayList<ContributerModel> contributeurs = new ArrayList<>();

    ContributerModel contributerModel1 = new ContributerModel();
    contributerModel1.setContributerId(1l);
    contributerModel1.setFirstName("First Name Contributeur 1");
    contributerModel1.setLastName("Last Name Contributeur 1");

    ContributerModel contributerModel2 = new ContributerModel();
    contributerModel2.setContributerId(2l);
    contributerModel2.setFirstName("First Name Contributeur 2");
    contributerModel2.setLastName("Last Name Contributeur 2");

    User user1 = new User();
    user1.setUserId(1l);
    user1.setUserFirstName("First Name Contributeur 1");
    user1.setUserLastName("Last Name Contributeur 1");

    User user2 = new User();
    user2.setUserId(2l);
    user2.setUserFirstName("First Name Contributeur 2");
    user2.setUserLastName("Last Name Contributeur 2");

    contributeurs.add(contributerModel1);
    contributeurs.add(contributerModel2);

    ProjectModel project = new ProjectModel();
    project.setName("nameProject");
    project.setContributers(contributeurs);

    when(userRepository.findByUserEmail(user.getUserEmail())).thenReturn(user);
    when(projectRepository.save(any())).thenReturn(projectToSave);

    when(userRepository.findById(1l)).thenReturn(Optional.of(user1));
    when(userRepository.findById(2l)).thenReturn(Optional.of(user2));


    ProjectResponseModel projectToReturn = new ProjectResponseModel();
    projectToReturn.setProjectId(projectToSave.getProjectId());
    projectToReturn.setName(project.getName());
    projectToReturn.setDescription(projectToSave.getDescription());
    projectToReturn.setIsArchived(false);
    projectToReturn.setConfirmationState("CONFIRMED");
    projectToReturn.setChefId(user1.getUserId());
    projectToReturn.setContributors(project.getContributers());
    projectToReturn.setCreatedAt(projectToSave.getCreatedAt());
    projectToReturn.setUpdatedAt(projectToSave.getUpdatedAt());
    projectToReturn.setMultiCriteriaStatus(ProjectAnalysisState.NOT_STARTED.toString());

    Assertions.assertEquals(projectToReturn, projectServiceImpl.persistProject(project,"userEmail"));
  }

  @Test
  void persistProject_whenUserNotFound() {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    ArrayList<ContributerModel> contributers = new ArrayList<>();
    contributers.add(new ContributerModel());
    ProjectModel project = new ProjectModel();
    project.setName("nameProject");
    project.setContributers(contributers);

    when(userRepository.findByUserEmail(user.getUserEmail())).thenReturn(user);
    when(projectRepository.save(any())).thenReturn(projectToSave);

    UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, ()-> projectServiceImpl.persistProject(project,"userEmail"));
    Assertions.assertEquals("Cet utilisateur n'existe pas.", exception.getMessage());
  }

  @Test
  void getAllProjects() {
    List <Project> projectList = new ArrayList<>();
    projectList.add(projectToSave);
    when(projectRepository.findAll()).thenReturn(projectList);
    Assertions.assertEquals(projectList, projectServiceImpl.getAllProjects());
  }

  @Test
  void getUserProjects() {
    String keyword = "test@test.com";
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    List<Project> projects = new ArrayList<>();
    List<ProjectUserPermission> projectUserPermissions = new ArrayList<>();

    when(projectUserPermissionRepository.findAll()).thenReturn(projectUserPermissions);
    Assertions.assertEquals(projects, projectServiceImpl.getUserProjects(keyword));
  }

  @Test
  void getUserProjectList() {

    String keyword = "test@test.com";
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    List<ProjectResponseModel> projects = new ArrayList<>();
    List<ProjectUserPermission> projectUserPermissions = new ArrayList<>();

    when(projectUserPermissionRepository.findAll()).thenReturn(projectUserPermissions);
    Assertions.assertEquals(projects, projectServiceImpl.getUserProjects(keyword));
  }


  @Test
  void validateOrRejectProject() throws ProjectNotFoundException, IOException {
    Long id = 1L;
    Project projectToConfirm = new Project();
    projectToConfirm.setProjectId(id);
    projectToConfirm.setConfirmationState("WAITING");

    String json = "{\"isConfirmed\": true}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> isConfirmed = new HttpEntity<>(json, headers);

    when(projectRepository.findById(id)).thenReturn(Optional.of(projectToConfirm));

    ProjectConfirmationState.valueOf(projectToConfirm.getConfirmationState());
    Assertions.assertEquals(ResponseEntity.ok().body("Le Projet a été validé"), projectServiceImpl.validateOrRejectProject(id, isConfirmed));
  }

  @Test
  void validateOrRejectProject_whenProjectNotFound(){
    Long id = 1L;
    String json = "{\"isConfirmed\": true}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> isConfirmed = new HttpEntity<>(json, headers);

    when(projectRepository.findById(id)).thenReturn(Optional.empty());

    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, ()-> projectServiceImpl.validateOrRejectProject(id, isConfirmed));
    Assertions.assertEquals("Ce Projet n'existe pas!", exception.getMessage());
  }

  @Test
  void validateOrRejectProject_whenProjectAlredyValidated() throws ProjectNotFoundException, IOException {
    Long id = 1L;
    Project projectToConfirm = new Project();
    projectToConfirm.setProjectId(id);
    projectToConfirm.setConfirmationState("CONFIRMED");

    String json = "{\"isConfirmed\": true}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> isConfirmed = new HttpEntity<>(json, headers);

    when(projectRepository.findById(id)).thenReturn(Optional.of(projectToConfirm));

    ProjectConfirmationState.valueOf(projectToConfirm.getConfirmationState());
    Assertions.assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce projet est déjà validé"), projectServiceImpl.validateOrRejectProject(id, isConfirmed));
  }

  @Test
  void validateOrRejectProject_whenProjectAlredyRejected() throws ProjectNotFoundException, IOException {
    Long id = 1L;
    Project projectToConfirm = new Project();
    projectToConfirm.setProjectId(id);
    projectToConfirm.setConfirmationState("REJECTED");

    String json = "{\"isConfirmed\": false}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> isConfirmed = new HttpEntity<>(json, headers);

    when(projectRepository.findById(id)).thenReturn(Optional.of(projectToConfirm));

    ProjectConfirmationState.valueOf(projectToConfirm.getConfirmationState());
    Assertions.assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce projet est déjà rejected"), projectServiceImpl.validateOrRejectProject(id, isConfirmed));
  }

  @Test
  void validateOrRejectProject_whenProjectIsRefused() throws ProjectNotFoundException, IOException {
    Long id = 1L;
    Project projectToConfirm = new Project();
    projectToConfirm.setProjectId(id);
    projectToConfirm.setConfirmationState("WAITING");

    String json = "{\"isConfirmed\": false}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> isConfirmed = new HttpEntity<>(json, headers);

    when(projectRepository.findById(id)).thenReturn(Optional.of(projectToConfirm));

    ProjectConfirmationState.valueOf(projectToConfirm.getConfirmationState());
    Assertions.assertEquals(ResponseEntity.ok().body("Le Projet a été refusé"), projectServiceImpl.validateOrRejectProject(id, isConfirmed));
  }

  @Test
  void addProjectContributor() throws UserNotFoundException, ProjectNotFoundException {
    Long projectId = 1L;
    ContributerModel contributor = new ContributerModel();
    contributor.setContributerId(user.getUserId());
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectToSave));
    when(userRepository.findById(contributor.getContributerId())).thenReturn(Optional.of(user));
    Assertions.assertEquals(ResponseEntity.ok().body("Le collaborateur a été bien enregistré"), projectServiceImpl.addProjectContributor(projectId, contributor));
  }

  @Test
  void addProjectContributor_whenProjectDosNotExists() {
    Long projectId = 1L;
    ContributerModel contributor = new ContributerModel();
    contributor.setContributerId(user.getUserId());
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, ()-> projectServiceImpl.addProjectContributor(projectId, contributor));
    Assertions.assertEquals("Ce Projet n'existe pas! ", exception.getMessage());
  }
  @Test
  void addProjectContributor_whenUserDosNotExists() {
    Long projectId = 1L;
    ContributerModel contributor = new ContributerModel();
    contributor.setContributerId(user.getUserId());
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectToSave));
    when(userRepository.findById(contributor.getContributerId())).thenReturn(Optional.empty());

    UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, ()-> projectServiceImpl.addProjectContributor(projectId, contributor));
    Assertions.assertEquals("Cet utilisateur n'existe pas !", exception.getMessage());

  }

  @Test
  void deleteProjectContributor() throws UserNotFoundException, ProjectNotFoundException, ProjectOwnerException {
    Long projectId = 1L;
    Long contributorID = 1L;
    projectUserPermission.setPermission(ProjectPermession.CONTRIBUTER.name());
    when(projectRepository.existsById(projectId)).thenReturn(true);
    when(projectUserPermissionRepository.findByProjectIdUserID(projectId, contributorID)).thenReturn(projectUserPermission);
    Assertions.assertEquals(ResponseEntity.ok().body("Le collaborateur a été bien retiré du projet"),projectServiceImpl.deleteProjectContributor(projectId,contributorID));
  }

  @Test
  void deleteProjectContributor_whenProjectNotFound() {
    Long projectId = 1L;
    Long contributorID = 1L;
    when(projectRepository.existsById(projectId)).thenReturn(false);
    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, ()-> projectServiceImpl.deleteProjectContributor(projectId,contributorID));

    Assertions.assertEquals("Ce Projet n'existe pas!", exception.getMessage());
  }

  @Test
  void deleteProjectContributor_whenUseNotFound() {
    Long projectId = 1L;
    Long contributorID = 1L;
    when(projectRepository.existsById(projectId)).thenReturn(true);
    when(projectUserPermissionRepository.findByProjectIdUserID(projectId, contributorID)).thenReturn(null);
    UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, ()-> projectServiceImpl.deleteProjectContributor(projectId,contributorID));

    Assertions.assertEquals("Cet utilisateur n'existe pas dans le projet !", exception.getMessage());
  }

  // CDP = chef de projet
  @Test
  void deleteProjectContributor_whenUseIsCDP() {
    Long projectId = 1L;
    Long contributorID = 1L;
    when(projectRepository.existsById(projectId)).thenReturn(true);
    when(projectUserPermissionRepository.findByProjectIdUserID(projectId, contributorID)).thenReturn(projectUserPermission);
    ProjectOwnerException exception = Assertions.assertThrows(ProjectOwnerException.class, ()-> projectServiceImpl.deleteProjectContributor(projectId,contributorID));
    Assertions.assertEquals("Cet utilisateur ne peut pas etre elimine car c'est le chef du projet!", exception.getMessage());
  }

  @Test
  void getProjectData() throws ProjectNotFoundException, UserNotAuthorizedException, ExecutionPlanNotFoundException {
    Long projectId = 1L;
    List<ProjectUserPermission> userPermissionList = new ArrayList<>();
    userPermissionList.add(projectUserPermission);
    projectToSave.setProjectUserPermissions(userPermissionList);
    ArrayList<Attachment> attachmentList = new ArrayList<>();
    Attachment attachment = new Attachment();
    attachment.setCreator(new User());
    attachmentList.add(attachment);
    projectToSave.setAttachments(attachmentList);
    ArrayList<MultiCriteriaAnalysis> multicriteriaAnaliseList = new ArrayList<>();
    MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    List<FormStep> formSteps = new ArrayList<>();
      FormStep formStep = new FormStep();
    formStep.setMultiCriteriaAnalysis(new MultiCriteriaAnalysis());
    formSteps.add(formStep);
    multiCriteriaAnalysis.setFormSteps(formSteps);
    multicriteriaAnaliseList.add(multiCriteriaAnalysis);
    projectToSave.setMultiCriteriaAnalysisList(multicriteriaAnaliseList);


    ArrayList<ContributerModel> contributorModelList = new ArrayList<>();
    ContributerModel contributerModel = new ContributerModel( user.getUserId(),
      user.getUserFirstName(),
      user.getUserLastName(),
      user.getUserEmail(),
      user.getIsAdmin(),
      projectUserPermission.getUserFunction(),
      user.getUserAvatarPath(),
      projectUserPermission.getPermission());
    contributorModelList.add(contributerModel);

    ArrayList<AttachmentResponseModel> attachmentModelList = new ArrayList<>();
    attachmentModelList.add(new AttachmentResponseModel());
    ArrayList<MulticriteriaAnalyseResponseModel> multicriteriaAnaliseodelList = new ArrayList<>();
    MulticriteriaAnalyseResponseModel multicriteriaAnalyseResponseModel = new MulticriteriaAnalyseResponseModel();
    multicriteriaAnalyseResponseModel.setFormSteps(formSteps);
    multicriteriaAnaliseodelList.add(multicriteriaAnalyseResponseModel);
    ProjectResponseModel projectResponseModel = new ProjectResponseModel(
      projectToSave.getProjectId(),
      projectToSave.getName(),
      projectToSave.getDescription(),
      projectToSave.getCreatedAt(),
      projectToSave.getUpdatedAt(),
      projectToSave.getConfirmationState(),
      projectToSave.getChefId(),
      projectToSave.getIsArchived(),
      contributorModelList,
      attachmentModelList,
      multicriteriaAnaliseodelList,
      null
    );

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectToSave));
    Assertions.assertEquals(projectResponseModel, projectServiceImpl.getProjectData(projectId));

  }
  @Test
  void getProjectData_whenProjectNotFound() {
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, ()-> projectServiceImpl.getProjectData(projectId));
    Assertions.assertEquals("Ce Projet n'existe pas!", exception.getMessage());

  }

  @Test
  void deleteProject() throws ProjectNotFoundException {
    Long projectId = 1L;
    Project projectToDelete = new Project();
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    user.detachPermission(projectUserPermission);
    projectToDelete.setProjectUserPermissions(projectUserPermissionList);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectToDelete));
    Assertions.assertEquals(ResponseEntity.ok().body("Le projet a été bien supprimé"), projectServiceImpl.deleteProject(projectId));
  }
  @Test
  void deleteProject_WhenProjectNotFound() {
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, ()-> projectServiceImpl.deleteProject(projectId));
    Assertions.assertEquals("Ce project n'existe pas !", exception.getMessage());
  }
}
