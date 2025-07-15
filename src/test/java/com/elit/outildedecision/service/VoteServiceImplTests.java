package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.elit.outildedecision.service.implementation.VoteServiceImpl;
import com.elit.outildedecision.util.Constants;
import com.elit.outildedecision.enumeration.ProjectPermession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTests {

  @Mock
  private VoteRepository voteRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PredictibilityAnalysisRepository predictibilityAnalysisRepository;

  @Mock
  private ProjectUserPermissionRepository projectUserPermissionRepository;

  @InjectMocks
  private VoteServiceImpl voteService;
  @Mock
  private Authentication authentication;

  private static final String USER_EMAIL = "test@example.com";
  private static final Long USER_ID = 1L;
  private static final Long PROJECT_ID = 1L;
  private static final Long PREDICTIBILITY_ANALYSIS_ID = 1L;
  private static final int PREDICTIBILITY_VALUE = 1;
  private static final String CHOSEN_PMM = "pmm1";
  private User testUser;
  private PredictibilityAnalysis testPredictabilityAnalysis;
  private Project testProject;
  private ProjectUserPermission testPermission;
  private Vote testVote;
  private MultiCriteriaAnalysis testMultiCriteriaAnalysis;


  @BeforeEach
  void setUp() {
    // create a test user
    testUser = new User();
    testUser.setUserId(USER_ID);
    testUser.setUserEmail(USER_EMAIL);

    // create a test predictability analysis
    testPredictabilityAnalysis = new PredictibilityAnalysis();
    testPredictabilityAnalysis.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    testPredictabilityAnalysis.setPredictibilityAnalysisIteration(PREDICTIBILITY_VALUE);

    // create a test project
    testProject = new Project();
    testProject.setProjectId(PROJECT_ID);

    // create a test multicriteria analysis
    testMultiCriteriaAnalysis = new MultiCriteriaAnalysis();
    testMultiCriteriaAnalysis.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(testMultiCriteriaAnalysis);


    // create a test project permission
    testPermission = new ProjectUserPermission();
    testPermission.setUser(testUser);
    testPermission.setProject(testProject);
    testPermission.setPermission(ProjectPermession.CDP.name());

    // create a test vote
    testVote = new Vote();
    testVote.setUser(testUser);
    testVote.setVoteId(1L);
  }

  @Test
  void testCreateVote() throws Exception {
    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);


    when(userRepository.findByUserEmail(anyString())).thenReturn(testUser);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(new PredictibilityAnalysis()));

    // Act
    ResponseEntity<String> response = voteService.createVote(voteModelRequest);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Le vote a bien été enregistré.", response.getBody());
    verify(voteRepository, times(1)).save(any(Vote.class));
  }

  @Test
  void testCreateVote_success() throws Exception {
    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);


    testPredictabilityAnalysis.getMultiCriteriaAnalysis().getProject().setProjectId(testProject.getProjectId());

    when(userRepository.findByUserEmail(anyString())).thenReturn(testUser);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(testPredictabilityAnalysis));

    // Act
    ResponseEntity<String> response = voteService.createVote(testProject.getProjectId(),voteModelRequest);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Le vote a bien été enregistré.", response.getBody());
    verify(voteRepository, times(1)).save(any(Vote.class));
  }
  @Test
  void testCreateVote_whenVoteAlreadyExistInUser()  {
    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    User user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");

    Vote voteFromDB = new Vote();

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(voteRepository.findByPredictibilityAnalysisAndUserId(1L, user.getUserId())).thenReturn(voteFromDB);
    VoteAlreadyExistException p = Assertions.assertThrows(VoteAlreadyExistException.class, ()-> voteService.createVote(voteModelRequest));

    Assertions.assertEquals("Cet utilisateur à déjà soumis un vote.", p.getMessage());
  }

  @Test
  void testCreateVote_ThrowsVoteAlreadyExist()  {
    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    User user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");

    Vote voteFromDB = new Vote();

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    testPredictabilityAnalysis.getMultiCriteriaAnalysis().getProject().setProjectId(testProject.getProjectId());
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(voteRepository.findByPredictibilityAnalysisAndUserId(1L, user.getUserId())).thenReturn(voteFromDB);
    VoteAlreadyExistException p = Assertions.assertThrows(VoteAlreadyExistException.class, ()-> voteService.createVote(testProject.getProjectId(),voteModelRequest));

    Assertions.assertEquals("Cet utilisateur a déjà soumis un vote.", p.getMessage());
  }

  @Test
  void testCreateVote_whenPredictibilityAnalysisDoseNotExists()  {
    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    RuntimeException p = Assertions.assertThrows(RuntimeException.class, ()-> voteService.createVote(voteModelRequest));

    Assertions.assertEquals("Cet analyse n'existe pas", p.getMessage());
  }
  @Test
  void testCreateVote_ThrowsPredictibilityAnalysisNotExists()  {
    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    testPredictabilityAnalysis.getMultiCriteriaAnalysis().getProject().setProjectId(testProject.getProjectId());
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    PredictibilityAnalysisNotFoundException p = Assertions.assertThrows(PredictibilityAnalysisNotFoundException.class, ()-> voteService.createVote(testProject.getProjectId(),voteModelRequest));

    Assertions.assertEquals("Analyse de Predictibilité n'existe pas ", p.getMessage());
  }
  @Test
  void testCreateVote_ThrowsProjectNotExists()  {
    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    testVote.setPredictibilityAnalysis(testPredictabilityAnalysis);
    voteModelRequest.setIsConfirmed(true);

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    testPredictabilityAnalysis.getMultiCriteriaAnalysis().getProject().setProjectId(testProject.getProjectId());
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(testPredictabilityAnalysis));
    ProjectNotFoundException p = Assertions.assertThrows(ProjectNotFoundException.class, ()-> voteService.createVote(5L,voteModelRequest));

    Assertions.assertEquals(Constants.PROJECT_DOESNT_EXIST, p.getMessage());
  }

  @Test
  void testCheckPermissionByProjectIdAndUserId_withValidProjectAndUser_shouldReturnPermissionList()
    throws Exception {
    // Arrange
    testPermission.setPermission("CONTRIBUTER");
    testPermission.setProject(testProject);
    testPermission.setUser(testUser);

    List<ProjectUserPermission> userProjectPermissions = new ArrayList<>();
    userProjectPermissions.add(testPermission);
    testUser.setUserProjectPermissions(userProjectPermissions);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

    // Act
    List<String> result = voteService.checkPermissionByProjectIdAndUserId(1L, 1L);

    // Assert
    assertThat(result.contains("CONTRIBUTER"));
  }

  @Test
  void testCheckPermissionByProjectIdAndUserId_whenUserNotFound()  {
    when(userRepository.findById(any())).thenReturn(Optional.empty());
    UserNotFoundException p = Assertions.assertThrows(UserNotFoundException.class, ()-> voteService.checkPermissionByProjectIdAndUserId(1L, 1L));

    Assertions.assertEquals("Utilisateur introuvable.", p.getMessage());
  }

  @Test
  void testGetVote_shouldReturn_VoteModelList() throws UserNotFoundException {

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);
    user.setUserProjectPermissions(new ArrayList<>());
     Project project = new Project();
    ProjectUserPermission pup = new ProjectUserPermission();
    pup.setId(new ProjectPropertyKey());
    pup.setUser(user);
    pup.setProject(project);
    pup.setPermission("permission");
    ArrayList<ProjectUserPermission> list = new ArrayList<ProjectUserPermission>();
    list.add(pup);
    user.setUserProjectPermissions(list);
    user.detachPermission(pup);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    List< VoteModelResponse > result = new ArrayList<>();
    VoteModelResponse vMp = new VoteModelResponse(null,null,null,null,1L,null,1L);
    result.add(vMp);

    List<Vote> voteList = new ArrayList<>();
    Vote vote = new Vote();
    vote.setUser(testUser);
    voteList.add(vote);

    List<ProjectUserPermission> p = new ArrayList<>();
    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testProject.setProjectUserPermissions(p);

    when(userRepository.findByUserEmail(user.getUsername())).thenReturn(user);
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(predictibilityAnalysisRepository.findById(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(Optional.of(testPredictabilityAnalysis));
    when(voteRepository.findAllByPredictibilityAnalysisId(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(voteList);
    Assertions.assertEquals(result, voteService.getVote(1L));

  }
  @Test
  void testGetVote_success() throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException {

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(testUser));

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    List< VoteModelResponse > result = new ArrayList<>();
    VoteModelResponse vMp = new VoteModelResponse(null,null,null,null,1L,null,1L);
    result.add(vMp);
    List<ProjectUserPermission> p = new ArrayList<>();
    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testProject.setProjectUserPermissions(p);
    testVote.setPredictibilityAnalysis(testPredictabilityAnalysis);

    User user = new User();
    user.setUserEmail(userDetails.getUsername());
    user.setUserId(1L);
    user.setUserProjectPermissions(p);


    when(userRepository.findByUserEmail(userDetails.getUsername())).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(Optional.of(testPredictabilityAnalysis));
    Assertions.assertEquals(result, voteService.getVote(testProject.getProjectId(),testPredictabilityAnalysis.getPredictibilityAnalysisId()));

  }

  @Test
  void testGetVote_shouldReturn_VoteModelList_whenUserIsCDPOrContributer() throws UserNotFoundException {

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    UserDetails userDetails = (UserDetails)  SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    List< VoteModelResponse > result = new ArrayList<>();
    VoteModelResponse vMp = new VoteModelResponse(1L,null,null,null,1L,null,1L);
    result.add(vMp);

    List<Vote> voteList = new ArrayList<>();
    Vote vote = new Vote();
    vote.setVoteId(1L);
    vote.setPredictibilityAnalysis(testPredictabilityAnalysis);
    vote.setUser(user);
    voteList.add(vote);

    List<ProjectUserPermission> p = new ArrayList<>();
    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testProject.setProjectUserPermissions(p);

    user.setUserEmail(userDetails.getUsername());
    user.setUserId(1L);
    user.setUserProjectPermissions(p);

    when(userRepository.findByUserEmail(userDetails.getUsername())).thenReturn(user);
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(predictibilityAnalysisRepository.findById(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(Optional.of(testPredictabilityAnalysis));
    //when(voteRepository.findByPredictibilityAnalysisAndUserId(testPredictabilityAnalysis.getPredictibilityAnalysisId(), user.getUserId())).thenReturn(testVote);
    when(voteRepository.findAllByPredictibilityAnalysisId(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(voteList);

    Assertions.assertEquals(result, voteService.getVote(1L));

  }


  @Test
  void testGetVote_whenPredectibilityDoseNotExists() {

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    when(predictibilityAnalysisRepository.findById(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(Optional.empty());

    RuntimeException exception = Assertions.assertThrows(RuntimeException.class, ()-> voteService.getVote(1L));

    Assertions.assertEquals("Predictibility Analysis Id non trouvé.", exception.getMessage());

  }

  @Test
  void testGetVote_whenProjectAssociateDoseNotExists() {

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    List< VoteModelResponse > result = new ArrayList<>();
    VoteModelResponse vMp = new VoteModelResponse(null,null,null,null,1L,null,1L);
    result.add(vMp);
    List<ProjectUserPermission> p = new ArrayList<>();
    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testProject.setProjectUserPermissions(p);

    when(predictibilityAnalysisRepository.findById(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(Optional.of(testPredictabilityAnalysis));

    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class, ()-> voteService.getVote(10L,testPredictabilityAnalysis.getPredictibilityAnalysisId()));

    Assertions.assertEquals("Le projet lié a cet analyse n'existe pas", exception.getMessage());

  }

  @Test
  void testGetVote_whenUserDoseNotExisitsInProject() {

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);


    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testProject.setProjectUserPermissions(null);

    when(userRepository.findByUserEmail(user.getUsername())).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(1L)).thenReturn(Optional.of(testPredictabilityAnalysis));
    UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, ()-> voteService.getVote(1L));

    Assertions.assertEquals("Cet utilisateur n'existe pas dans le projet !", exception.getMessage());

  }

  @Test
   void updateVote()  {

    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testVote.setPredictibilityAnalysis(testPredictabilityAnalysis);

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    when(userRepository.findByUserEmail(user.getUsername())).thenReturn(user);
    when(voteRepository.findById(testVote.getVoteId())).thenReturn(Optional.ofNullable(testVote));
    Assertions.assertEquals(ResponseEntity.ok().body("Vote mis à jour."), voteService.updateVote(testVote.getVoteId(),voteModelRequest));
  }

  @Test
  void updateVote_whenUserIsNotAutorised()  {
    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(3L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testVote.setPredictibilityAnalysis(testPredictabilityAnalysis);
    testVote.setUser(testUser);
    UserDetails userDetails = (UserDetails)  SecurityContextHolder.getContext().getAuthentication().getPrincipal();


    testPermission.setPermission(ProjectPermession.CONTRIBUTER.toString());

    when(userRepository.findByUserEmail(userDetails.getUsername())).thenReturn(user);
    when(voteRepository.findById(testVote.getVoteId())).thenReturn(Optional.ofNullable(testVote));
    when(projectUserPermissionRepository.findByProjectIdUserID(testProject.getProjectId(), user.getUserId())).thenReturn(testPermission);
    Assertions.assertEquals(ResponseEntity.status(HttpStatus.CONFLICT).body("Vous n'êtes pas autorisé à modifier ce vote."), voteService.updateVote(testVote.getVoteId(),voteModelRequest));
  }

  @Test
  void updateVote_whenVoteDoseNotExists()  {
    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);


    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    UserDetails userDetails = (UserDetails)  SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    when(userRepository.findByUserEmail(userDetails.getUsername())).thenReturn(user);
    when(voteRepository.findById(3L)).thenReturn(Optional.empty());

    RuntimeException p = Assertions.assertThrows(RuntimeException.class, ()-> voteService.updateVote(3L,voteModelRequest));
    Assertions.assertEquals("Ce vote n'existe pas!", p.getMessage());
  }

  @Test
  void updateVote_whenProjectAssociateNotFound()  {

    // Arrange
    VoteModelRequest voteModelRequest = new VoteModelRequest();
    voteModelRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteModelRequest.setChosenPMM(CHOSEN_PMM);
    voteModelRequest.setJustification("Test justification");
    voteModelRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteModelRequest.setIsConfirmed(true);

    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testVote.setPredictibilityAnalysis(testPredictabilityAnalysis);

    UserDetails userDetails = (UserDetails)  SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    when(userRepository.findByUserEmail(userDetails.getUsername())).thenReturn(user);
    when(voteRepository.findById(testVote.getVoteId())).thenReturn(Optional.ofNullable(testVote));
    ProjectNotFoundException p = Assertions.assertThrows(ProjectNotFoundException.class, ()-> voteService.updateVote(10L,1L, voteModelRequest));
    Assertions.assertEquals("Ce projet n'existe pas", p.getMessage());
  }

  @Test
  void shoudGetAllVotesForCDP_whenNotFoundProjectAssociateUserPermission() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    //use reflection to test private methods
    Method method = voteService.getClass().getDeclaredMethod("getAllVotesForCDP", testProject.getClass(), Long.class);
    method.setAccessible(true);
    List<VoteModelResponse> result = (List<VoteModelResponse>) method.invoke(voteService, testProject, 1L);

    List< VoteModelResponse > response = new ArrayList<>();
    Assertions.assertEquals(response,result);
  }

}

