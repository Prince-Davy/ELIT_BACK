package com.elit.outildedecision.collaboration.vote.service.implementation;

import com.elit.outildedecision.collaboration.vote.dto.VoteRequest;
import com.elit.outildedecision.collaboration.vote.dto.VoteResponse;
import com.elit.outildedecision.collaboration.vote.entity.Vote;
import com.elit.outildedecision.collaboration.vote.mapper.VoteMapper;
import com.elit.outildedecision.collaboration.vote.repository.VoteRepository;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
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
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.Constants;
import com.elit.outildedecision.shared.util.ProjectPermession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {

  @Mock
  private VoteRepository voteRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PredictibilityAnalysisRepository predictibilityAnalysisRepository;

  @Mock
  private ProjectUserPermissionRepository projectUserPermissionRepository;

  @Mock
  private VoteMapper voteMapper;

  @InjectMocks
  private VoteServiceImpl voteService;

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
    testUser = new User();
    testUser.setUserId(USER_ID);
    testUser.setUserEmail(USER_EMAIL);

    testPredictabilityAnalysis = new PredictibilityAnalysis();
    testPredictabilityAnalysis.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    testPredictabilityAnalysis.setPredictibilityAnalysisIteration(PREDICTIBILITY_VALUE);

    testProject = new Project();
    testProject.setProjectId(PROJECT_ID);

    testMultiCriteriaAnalysis = new MultiCriteriaAnalysis();
    testMultiCriteriaAnalysis.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(testMultiCriteriaAnalysis);

    testPermission = new ProjectUserPermission();
    testPermission.setUser(testUser);
    testPermission.setProject(testProject);
    testPermission.setPermission(ProjectPermession.CDP.name());

    testVote = new Vote();
    testVote.setUser(testUser);
    testVote.setVoteId(1L);
  }

  private void mockPrincipal(final User user) {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void testCreateVote_success() throws Exception {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);
    voteRequest.setChosenPMM(CHOSEN_PMM);
    voteRequest.setJustification("Test justification");
    voteRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
    voteRequest.setIsConfirmed(true);

    mockPrincipal(testUser);
    when(userRepository.findByUserEmail(anyString())).thenReturn(testUser);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(testPredictabilityAnalysis));
    when(voteMapper.toEntity(voteRequest)).thenReturn(new Vote());

    String response = voteService.createVote(testProject.getProjectId(), voteRequest);

    assertEquals("Le vote a bien été enregistré.", response);
    verify(voteRepository, times(1)).save(any(Vote.class));
  }

  @Test
  void testCreateVote_ThrowsVoteAlreadyExist() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);

    User user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");

    mockPrincipal(user);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(voteRepository.findByPredictibilityAnalysisAndUserId(1L, user.getUserId())).thenReturn(new Vote());

    VoteAlreadyExistException p = Assertions.assertThrows(VoteAlreadyExistException.class,
      () -> voteService.createVote(testProject.getProjectId(), voteRequest));

    Assertions.assertEquals("Cet utilisateur a déjà soumis un vote.", p.getMessage());
  }

  @Test
  void testCreateVote_ThrowsPredictibilityAnalysisNotExists() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUserId(1L);

    mockPrincipal(user);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);

    PredictibilityAnalysisNotFoundException p = Assertions.assertThrows(PredictibilityAnalysisNotFoundException.class,
      () -> voteService.createVote(testProject.getProjectId(), voteRequest));

    Assertions.assertEquals("Analyse de Predictibilité n'existe pas ", p.getMessage());
  }

  @Test
  void testCreateVote_ThrowsProjectNotExists() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUserId(1L);

    mockPrincipal(user);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(testPredictabilityAnalysis));

    ProjectNotFoundException p = Assertions.assertThrows(ProjectNotFoundException.class,
      () -> voteService.createVote(5L, voteRequest));

    Assertions.assertEquals(Constants.PROJECT_DOESNT_EXIST, p.getMessage());
  }

  @Test
  void testCheckPermissionByProjectIdAndUserId_withValidProjectAndUser_shouldReturnPermissionList()
    throws Exception {
    testPermission.setPermission("CONTRIBUTER");

    List<ProjectUserPermission> userProjectPermissions = new ArrayList<>();
    userProjectPermissions.add(testPermission);
    testUser.setUserProjectPermissions(userProjectPermissions);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

    List<String> result = voteService.checkPermissionByProjectIdAndUserId(1L, 1L);

    assertTrue(result.contains("CONTRIBUTER"));
  }

  @Test
  void testCheckPermissionByProjectIdAndUserId_whenUserNotFound() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());
    UserNotFoundException p = Assertions.assertThrows(UserNotFoundException.class,
      () -> voteService.checkPermissionByProjectIdAndUserId(1L, 1L));

    Assertions.assertEquals("Utilisateur introuvable.", p.getMessage());
  }

  @Test
  void testGetVote_shouldReturn_VoteModelList() throws UserNotFoundException {
    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);
    user.setUserProjectPermissions(new ArrayList<>());

    mockPrincipal(user);

    VoteResponse expected = new VoteResponse(null, null, null, null, 1L, null, 1L);

    List<Vote> voteList = new ArrayList<>();
    Vote vote = new Vote();
    vote.setUser(testUser);
    voteList.add(vote);

    List<ProjectUserPermission> permissions = new ArrayList<>();
    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testProject.setProjectUserPermissions(permissions);

    when(userRepository.findByUserEmail(user.getUsername())).thenReturn(user);
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(predictibilityAnalysisRepository.findById(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(Optional.of(testPredictabilityAnalysis));
    when(voteRepository.findAllByPredictibilityAnalysisId(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(voteList);
    when(voteMapper.toResponse(any(Vote.class))).thenReturn(expected);

    assertEquals(List.of(expected), voteService.getVote(1L));
  }

  @Test
  void testGetVote_shouldReturn_VoteModelList_whenUserIsCDPOrContributer() throws UserNotFoundException {
    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    mockPrincipal(user);

    VoteResponse expected = new VoteResponse(1L, null, null, null, 1L, null, 1L);

    List<Vote> voteList = new ArrayList<>();
    Vote vote = new Vote();
    vote.setVoteId(1L);
    vote.setPredictibilityAnalysis(testPredictabilityAnalysis);
    vote.setUser(user);
    voteList.add(vote);

    List<ProjectUserPermission> permissions = new ArrayList<>();
    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testProject.setProjectUserPermissions(permissions);

    user.setUserProjectPermissions(permissions);

    when(userRepository.findByUserEmail(user.getUsername())).thenReturn(user);
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(predictibilityAnalysisRepository.findById(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(Optional.of(testPredictabilityAnalysis));
    when(voteRepository.findAllByPredictibilityAnalysisId(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(voteList);
    when(voteMapper.toResponse(any(Vote.class))).thenReturn(expected);

    assertEquals(List.of(expected), voteService.getVote(1L));
  }

  @Test
  void testGetVote_whenPredectibilityDoseNotExists() {
    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    mockPrincipal(user);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(testPredictabilityAnalysis.getPredictibilityAnalysisId())).thenReturn(Optional.empty());

    PredictibilityAnalysisNotFoundException exception = Assertions.assertThrows(
      PredictibilityAnalysisNotFoundException.class, () -> voteService.getVote(1L));

    Assertions.assertEquals("Predictibility Analysis Id non trouvé.", exception.getMessage());
  }

  @Test
  void testGetVote_whenUserDoseNotExisitsInProject() {
    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUsername("test@test.fr");
    user.setUserId(1L);

    mockPrincipal(user);

    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testProject.setProjectUserPermissions(null);

    when(userRepository.findByUserEmail(user.getUsername())).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(1L)).thenReturn(Optional.of(testPredictabilityAnalysis));
    UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> voteService.getVote(1L));

    Assertions.assertEquals("Cet utilisateur n'existe pas dans le projet !", exception.getMessage());
  }

  @Test
  void updateVote_whenProjectAssociateNotFound() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setPredictibilityAnalysisId(PREDICTIBILITY_ANALYSIS_ID);

    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testVote.setPredictibilityAnalysis(testPredictabilityAnalysis);

    User user = new User();
    user.setUserEmail("test@test.fr");
    user.setUserId(1L);

    mockPrincipal(user);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(voteRepository.findById(testVote.getVoteId())).thenReturn(Optional.of(testVote));

    ProjectNotFoundException p = Assertions.assertThrows(ProjectNotFoundException.class,
      () -> voteService.updateVote(10L, 1L, voteRequest));
    Assertions.assertEquals("Ce projet n'existe pas", p.getMessage());
  }

  @Test
  void updateVote_whenOwner_shouldReturnMessageAndSave() throws Exception {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setChosenPMM("modified");
    voteRequest.setJustification("modified");
    voteRequest.setPredictibilityValue(2);

    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testVote.setPredictibilityAnalysis(testPredictabilityAnalysis);
    testVote.setUser(testUser);

    mockPrincipal(testUser);
    when(userRepository.findByUserEmail(anyString())).thenReturn(testUser);
    when(voteRepository.findById(testVote.getVoteId())).thenReturn(Optional.of(testVote));

    String result = voteService.updateVote(PROJECT_ID, testVote.getVoteId(), voteRequest);

    assertEquals("Vote mis à jour.", result);
    verify(voteRepository, times(1)).save(testVote);
  }

  @Test
  void updateVote_whenNotOwner_shouldThrowConflictWithoutSaving() throws Exception {
    VoteRequest voteRequest = new VoteRequest();

    MultiCriteriaAnalysis m = new MultiCriteriaAnalysis();
    m.setProject(testProject);
    testPredictabilityAnalysis.setMultiCriteriaAnalysis(m);
    testVote.setPredictibilityAnalysis(testPredictabilityAnalysis);
    testVote.setUser(testUser);

    User otherUser = new User();
    otherUser.setUserId(2L);
    otherUser.setUserEmail("other@test.fr");

    mockPrincipal(otherUser);
    when(userRepository.findByUserEmail(anyString())).thenReturn(otherUser);
    when(voteRepository.findById(testVote.getVoteId())).thenReturn(Optional.of(testVote));

    Assertions.assertThrows(ConflictException.class,
      () -> voteService.updateVote(PROJECT_ID, testVote.getVoteId(), voteRequest));
    verify(voteRepository, never()).save(any(Vote.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shoudGetAllVotesForCDP_whenNotFoundProjectAssociateUserPermission()
    throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = voteService.getClass().getDeclaredMethod("getAllVotesForCDP", Project.class, Long.class);
    method.setAccessible(true);
    List<VoteResponse> result = (List<VoteResponse>) method.invoke(voteService, testProject, 1L);

    Assertions.assertEquals(new ArrayList<VoteResponse>(), result);
  }
}
