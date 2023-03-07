package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.repository.VoteRepository;
import com.elit.outildedecision.util.ProjectPermession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VoteServiceImplTest {

  @Mock
  private VoteRepository voteRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PredictibilityAnalysisRepository predictibilityAnalysisRepository;

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
  private List<ProjectUserPermission> testPermissions;
  private VoteModelRequest testRequest;



  @Before
  public void setUp() {
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


    // create a test project permission
    testPermission = new ProjectUserPermission();
    testPermission.setUser(testUser);
    testPermission.setProject(testProject);
    testPermission.setPermission(ProjectPermession.CDP.name());
    testPermissions = Arrays.asList(testPermission);

    // create a test request
    testRequest = new VoteModelRequest();
    testRequest.setPredictibilityValue(PREDICTIBILITY_VALUE);
  }

  @Test
  public void testCheckPermissionByProjectIdAndUserId_withValidProjectAndUser_shouldReturnPermissionList()
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
  public void testCreateVote() throws Exception {
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

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    when(predictibilityAnalysisRepository.findById(anyLong())).thenReturn(Optional.of(new PredictibilityAnalysis()));

    // Act
    ResponseEntity<String> response = voteService.createVote(voteModelRequest);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Le vote a bien été enregistré.", response.getBody());
    verify(voteRepository, times(1)).save(any(Vote.class));
  }

}

