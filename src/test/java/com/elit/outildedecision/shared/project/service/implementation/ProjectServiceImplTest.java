package com.elit.outildedecision.shared.project.service.implementation;

import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.ai.feedback.repository.FeedbackIaRepository;
import com.elit.outildedecision.ai.feedback.service.FeedbackIaService;
import com.elit.outildedecision.shared.attachment.entity.Attachment;
import com.elit.outildedecision.shared.attachment.service.FileSystemService;
import com.elit.outildedecision.shared.security.PermissionService;
import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.collaboration.multicriteria.service.MultiCriteriaAnalysisService;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectOwnerException;
import com.elit.outildedecision.shared.attachment.exception.AttachmentNotFoundException;
import com.elit.outildedecision.shared.attachment.dto.AttachmentResponse;
import com.elit.outildedecision.shared.project.dto.ContributorRequest;
import com.elit.outildedecision.collaboration.multicriteria.dto.MulticriteriaAnalysisResponse;
import com.elit.outildedecision.shared.project.dto.ProjectRequest;
import com.elit.outildedecision.shared.project.dto.ProjectResponse;
import com.elit.outildedecision.shared.project.dto.ProjectPageResponse;
import com.elit.outildedecision.shared.project.dto.ProjectFilterRequest;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.kernel.journey.ProjectJourneyStatusRules;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.project.repository.specification.ProjectFilterSpecification;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.ai.result.service.ResultIaService;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.shared.util.JourneyState;
import com.elit.outildedecision.shared.util.ProjectAnalysisState;
import com.elit.outildedecision.shared.util.ProjectPermession;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.elit.outildedecision.shared.attachment.mapper.AttachmentMapper;
import com.elit.outildedecision.shared.attachment.mapper.AttachmentMapperImpl;
import com.elit.outildedecision.shared.project.mapper.ProjectMapper;
import com.elit.outildedecision.shared.project.mapper.ProjectMapperImpl;
import com.elit.outildedecision.collaboration.multicriteria.dto.FormStepResponse;
import com.elit.outildedecision.collaboration.multicriteria.mapper.MulticriteriaMapper;
import com.elit.outildedecision.collaboration.multicriteria.mapper.MulticriteriaMapperImpl;
import com.elit.outildedecision.shared.project.service.ProjectValidationResult;
import com.elit.outildedecision.ai.result.dto.ResultResponse;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImplTest.class);

  @Mock
  UserRepository userRepository;
  @Mock
  ProjectRepository projectRepository;
  @Mock
  FeedbackIaRepository feedbackIaRepository;
  @Mock
  ResultIaRepository resultIaRepository;
  @Mock
  AiAnswerRepository aiAnswerRepository;
  @Mock
  MultiCriteriaAnalysisService multiCriteriaService;
  @Mock
  MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock
  ProjectUserPermissionRepository projectUserPermissionRepository;
  @Mock
  PermissionService permissionService;
  @Mock
  ResultIaService resultIaService;
  @Mock
  FeedbackIaService feedbackIaService;
  @Mock
  FileSystemService fileSystemService;
  @Spy
  ProjectMapper projectMapper = new ProjectMapperImpl();
  @Spy
  AttachmentMapper attachmentMapper = new AttachmentMapperImpl();
  @Spy
  MulticriteriaMapper multicriteriaMapper = new MulticriteriaMapperImpl();
  @Spy
  ProjectJourneyStatusRules journeyStatusRules = new ProjectJourneyStatusRules();
  @Mock
  ProjectFilterSpecification projectFilterSpecification;
  User user;
  Project projectToSave;
  ProjectUserPermission projectUserPermission;
  @InjectMocks
  private ProjectServiceImpl projectServiceImpl;

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

  @AfterAll
  static void cleanUp() {
      try (Stream<Path> files = Files.walk(Path.of("."))) {
        files
          .filter(path -> path.getFileName().toString().matches("null.*"))
          .map(Path::toFile)
          .forEach(file -> {
            boolean delete = file.delete();
            if (delete) {
              LOGGER.trace("File {} successfully deleted", file);
            }
            else {
              LOGGER.error("File {} could not be deleted, continuing anyway", file);
            }
          });
      } catch (IOException e) {
        LOGGER.error("Error cleaning up, will not reattempt it", e);
      }
  }

  @Test
  void persistProject() throws UserNotFoundException, ProjectNotFoundException, FileCouldNotBeSaved, IOException, ParseException, org.json.simple.parser.ParseException {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    ArrayList<ContributorRequest> contributeurs = new ArrayList<>();

    ContributorRequest contributerModel1 = new ContributorRequest();
    contributerModel1.setContributerId(1l);
    contributerModel1.setFirstName("First Name Contributeur 1");
    contributerModel1.setLastName("Last Name Contributeur 1");

    ContributorRequest contributerModel2 = new ContributorRequest();
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

    ProjectRequest project = new ProjectRequest();
    project.setName("nameProject");
    project.setContributers(contributeurs);

    when(userRepository.findByUserEmail(user.getUserEmail())).thenReturn(user);
    when(projectRepository.save(any())).thenReturn(projectToSave);

    when(userRepository.findById(1l)).thenReturn(Optional.of(user1));
    when(userRepository.findById(2l)).thenReturn(Optional.of(user2));


    ProjectResponse projectToReturn = new ProjectResponse();
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

    Assertions.assertEquals(projectToReturn, projectServiceImpl.persistProject(project, "userEmail"));
  }

  @Test
  void persistProject_whenUserNotFound() {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    ArrayList<ContributorRequest> contributers = new ArrayList<>();
    contributers.add(new ContributorRequest());
    ProjectRequest project = new ProjectRequest();
    project.setName("nameProject");
    project.setContributers(contributers);

    when(userRepository.findByUserEmail(user.getUserEmail())).thenReturn(user);
    when(projectRepository.save(any())).thenReturn(projectToSave);

    UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> projectServiceImpl.persistProject(project, "userEmail"));
    Assertions.assertEquals("Cet utilisateur n'existe pas.", exception.getMessage());
  }

  @Test
  void getAllProjects() {
    List<Project> projectList = new ArrayList<>();
    projectList.add(projectToSave);
    when(projectRepository.findAll()).thenReturn(projectList);
    Assertions.assertEquals(
        projectList.stream().map(projectMapper::toResponseBase).toList(),
        projectServiceImpl.getAllProjects());
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

    List<ProjectResponse> projects = new ArrayList<>();
    List<ProjectUserPermission> projectUserPermissions = new ArrayList<>();

    when(projectUserPermissionRepository.findAll()).thenReturn(projectUserPermissions);
    Assertions.assertEquals(projects, projectServiceImpl.getUserProjects(keyword));
  }

  @Test
  void shouldListProjectsThroughTheFilterSpecificationWhenListingUserProjects()
      throws UserNotAuthorizedException, ExecutionPlanNotFoundException {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    ProjectFilterRequest triModel = new ProjectFilterRequest("", "COMPLETED", null, true, 0, 10, "IA");
    when(projectUserPermissionRepository.findAll(ArgumentMatchers.<Specification<ProjectUserPermission>>any(),
        any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));
    when(projectUserPermissionRepository.count(ArgumentMatchers.<Specification<ProjectUserPermission>>any()))
        .thenReturn(0L);

    ProjectPageResponse response = projectServiceImpl.getUserProjectList(triModel);

    Assertions.assertEquals(0, response.getProjectsPage().getTotalElements());
    verify(projectFilterSpecification, atLeastOnce()).byFilter(triModel, user.getUserEmail(), true);
    verify(projectFilterSpecification, atLeastOnce()).byFilter(triModel, user.getUserEmail(), false);
  }

  @Test
  void validateOrRejectProject() throws ProjectNotFoundException {
    Long id = 1L;
    Project projectToConfirm = new Project();
    projectToConfirm.setProjectId(id);
    projectToConfirm.setConfirmationState("WAITING");

    when(projectRepository.findById(id)).thenReturn(Optional.of(projectToConfirm));

    Assertions.assertEquals(ProjectValidationResult.VALIDATED, projectServiceImpl.validateOrRejectProject(id, true));
  }

  @Test
  void validateOrRejectProject_whenProjectNotFound() {
    Long id = 1L;
    when(projectRepository.findById(id)).thenReturn(Optional.empty());

    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, () -> projectServiceImpl.validateOrRejectProject(id, true));
    Assertions.assertEquals("Ce Projet n'existe pas!", exception.getMessage());
  }

  @Test
  void validateOrRejectProject_whenProjectAlredyValidated() throws ProjectNotFoundException {
    Long id = 1L;
    Project projectToConfirm = new Project();
    projectToConfirm.setProjectId(id);
    projectToConfirm.setConfirmationState("CONFIRMED");

    when(projectRepository.findById(id)).thenReturn(Optional.of(projectToConfirm));

    Assertions.assertEquals(ProjectValidationResult.ALREADY_CONFIRMED, projectServiceImpl.validateOrRejectProject(id, true));
  }

  @Test
  void validateOrRejectProject_whenProjectAlredyRejected() throws ProjectNotFoundException {
    Long id = 1L;
    Project projectToConfirm = new Project();
    projectToConfirm.setProjectId(id);
    projectToConfirm.setConfirmationState("REJECTED");

    when(projectRepository.findById(id)).thenReturn(Optional.of(projectToConfirm));

    Assertions.assertEquals(ProjectValidationResult.ALREADY_REJECTED, projectServiceImpl.validateOrRejectProject(id, false));
  }

  @Test
  void validateOrRejectProject_whenProjectIsRefused() throws ProjectNotFoundException {
    Long id = 1L;
    Project projectToConfirm = new Project();
    projectToConfirm.setProjectId(id);
    projectToConfirm.setConfirmationState("WAITING");

    when(projectRepository.findById(id)).thenReturn(Optional.of(projectToConfirm));

    Assertions.assertEquals(ProjectValidationResult.REJECTED, projectServiceImpl.validateOrRejectProject(id, false));
  }

  @Test
  void addProjectContributor() throws UserNotFoundException, ProjectNotFoundException {
    Long projectId = 1L;
    ContributorRequest contributor = new ContributorRequest();
    contributor.setContributerId(user.getUserId());
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectToSave));
    when(userRepository.findById(contributor.getContributerId())).thenReturn(Optional.of(user));
    Assertions.assertEquals("Le collaborateur a été bien enregistré", projectServiceImpl.addProjectContributor(projectId, contributor));
  }

  @Test
  void addProjectContributor_whenProjectDosNotExists() {
    Long projectId = 1L;
    ContributorRequest contributor = new ContributorRequest();
    contributor.setContributerId(user.getUserId());
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, () -> projectServiceImpl.addProjectContributor(projectId, contributor));
    Assertions.assertEquals("Ce Projet n'existe pas! ", exception.getMessage());
  }

  @Test
  void addProjectContributor_whenUserDosNotExists() {
    Long projectId = 1L;
    ContributorRequest contributor = new ContributorRequest();
    contributor.setContributerId(user.getUserId());
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectToSave));
    when(userRepository.findById(contributor.getContributerId())).thenReturn(Optional.empty());

    UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> projectServiceImpl.addProjectContributor(projectId, contributor));
    Assertions.assertEquals("Cet utilisateur n'existe pas !", exception.getMessage());

  }

  @Test
  void deleteProjectContributor() throws UserNotFoundException, ProjectNotFoundException, ProjectOwnerException {
    Long projectId = 1L;
    Long contributorID = 1L;
    projectUserPermission.setPermission(ProjectPermession.CONTRIBUTER.toString());
    when(projectRepository.existsById(projectId)).thenReturn(true);
    when(projectUserPermissionRepository.findByProjectIdUserID(projectId, contributorID)).thenReturn(projectUserPermission);
    Assertions.assertEquals("Le collaborateur a été bien retiré du projet", projectServiceImpl.deleteProjectContributor(projectId, contributorID));
  }

  @Test
  void deleteProjectContributor_whenProjectNotFound() {
    Long projectId = 1L;
    Long contributorID = 1L;
    when(projectRepository.existsById(projectId)).thenReturn(false);
    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, () -> projectServiceImpl.deleteProjectContributor(projectId, contributorID));

    Assertions.assertEquals("Ce Projet n'existe pas!", exception.getMessage());
  }

  @Test
  void deleteProjectContributor_whenUseNotFound() {
    Long projectId = 1L;
    Long contributorID = 1L;
    when(projectRepository.existsById(projectId)).thenReturn(true);
    when(projectUserPermissionRepository.findByProjectIdUserID(projectId, contributorID)).thenReturn(null);
    UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> projectServiceImpl.deleteProjectContributor(projectId, contributorID));

    Assertions.assertEquals("Cet utilisateur n'existe pas dans le projet !", exception.getMessage());
  }

  // CDP = chef de projet
  @Test
  void deleteProjectContributor_whenUseIsCDP() {
    Long projectId = 1L;
    Long contributorID = 1L;
    when(projectRepository.existsById(projectId)).thenReturn(true);
    when(projectUserPermissionRepository.findByProjectIdUserID(projectId, contributorID)).thenReturn(projectUserPermission);
    ProjectOwnerException exception = Assertions.assertThrows(ProjectOwnerException.class, () -> projectServiceImpl.deleteProjectContributor(projectId, contributorID));
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
    ArrayList<MultiCriteriaAnalysis> multicriteriaAnalysisList = new ArrayList<>();
    MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    List<FormStep> formSteps = new ArrayList<>();
    FormStep formStep = new FormStep();
    formStep.setMultiCriteriaAnalysis(new MultiCriteriaAnalysis());
    formSteps.add(formStep);
    multiCriteriaAnalysis.setFormSteps(formSteps);
    multicriteriaAnalysisList.add(multiCriteriaAnalysis);
    projectToSave.setMultiCriteriaAnalysisList(multicriteriaAnalysisList);


    ArrayList<ContributorRequest> contributorModelList = new ArrayList<>();
    ContributorRequest contributerModel = new ContributorRequest(user.getUserId(),
      user.getUserFirstName(),
      user.getUserLastName(),
      user.getUserEmail(),
      user.getIsAdmin(),
      projectUserPermission.getUserFunction(),
      user.getUserAvatarPath(),
      projectUserPermission.getPermission());
    contributorModelList.add(contributerModel);

    ArrayList<AttachmentResponse> attachmentModelList = new ArrayList<>();
    attachmentModelList.add(new AttachmentResponse());
    ArrayList<MulticriteriaAnalysisResponse> multicriteriaAnalysisodelList = new ArrayList<>();
    MulticriteriaAnalysisResponse multicriteriaAnalyseResponseModel = new MulticriteriaAnalysisResponse();
    multicriteriaAnalyseResponseModel.setFormSteps(multicriteriaMapper.toFormStepResponses(formSteps));
    multicriteriaAnalysisodelList.add(multicriteriaAnalyseResponseModel);
    ProjectResponse projectResponseModel = new ProjectResponse(
      projectToSave.getProjectId(),
      projectToSave.getName(),
      projectToSave.getDescription(),
      projectToSave.getCreatedAt(),
      projectToSave.getUpdatedAt(),
      projectToSave.getConfirmationState(),
      projectToSave.getChefId(),
      null,
      null,
      projectToSave.getIsArchived(),
      projectToSave.getFormProgressStatus(),
      projectToSave.getProjectState(),
      contributorModelList,
      attachmentModelList,
      multicriteriaAnalysisodelList,
      null,
      new ResultResponse(),
      JourneyState.NOT_STARTED,
      JourneyState.NOT_STARTED,
      JourneyState.NOT_STARTED
    );

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectToSave));
    when(multiCriteriaService.buildProjectAnalyses(any())).thenReturn(multicriteriaAnalysisodelList);
    Assertions.assertEquals(projectResponseModel, projectServiceImpl.getProjectData(projectId));

  }

  @Test
  void getProjectData_whenProjectNotFound() {
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, () -> projectServiceImpl.getProjectData(projectId));
    Assertions.assertEquals("Ce Projet n'existe pas!", exception.getMessage());

  }

  @Test
  void deleteProject() throws ProjectNotFoundException, AttachmentNotFoundException, IOException, ResponseNotFoundException {
    Long projectId = 1L;
    Project projectToDelete = new Project();
    List<ProjectUserPermission> projectUserPermissionList = new ArrayList<>();
    projectUserPermissionList.add(projectUserPermission);
    user.setUserProjectPermissions(projectUserPermissionList);
    user.detachPermission(projectUserPermission);
    projectToDelete.setProjectUserPermissions(projectUserPermissionList);
    MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    List<MultiCriteriaAnalysis> multiCriteriaAnalysisList = new ArrayList<>();
    multiCriteriaAnalysisList.add(multiCriteriaAnalysis);
    projectToDelete.setMultiCriteriaAnalysisList(multiCriteriaAnalysisList);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectToDelete));
    Assertions.assertEquals("Le projet a été bien supprimé", projectServiceImpl.deleteProject(projectId));
  }


  @Test
  void deleteProject_WhenProjectNotFound() {
    Long projectId = 1L;
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, () -> projectServiceImpl.deleteProject(projectId));
    Assertions.assertEquals("Le projet n'existe pas.", exception.getMessage());
  }
}
