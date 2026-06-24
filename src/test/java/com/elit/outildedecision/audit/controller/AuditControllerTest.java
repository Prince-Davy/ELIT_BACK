package com.elit.outildedecision.audit.controller;

import com.elit.outildedecision.audit.dto.AuditRequest;
import com.elit.outildedecision.audit.dto.AuditResponse;
import com.elit.outildedecision.audit.dto.AuditScoreResponse;
import com.elit.outildedecision.audit.service.AuditScoreService;
import com.elit.outildedecision.audit.service.AuditService;
import com.elit.outildedecision.audit.exception.AuditNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

  @Mock private AuditService auditService;
  @Mock private AuditScoreService auditScoreService;

  @InjectMocks
  private AuditController auditController;

  @AfterEach
  void cleanupSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  private Long auditId;
  private Long projectId;

  @BeforeEach
  void setUp() {
    auditId = 12L;
    projectId = 13L;
  }


  private void setAuthenticatedUser(String username) {
    User principal = new User(username, "secret", List.of());
    UsernamePasswordAuthenticationToken auth =
      new UsernamePasswordAuthenticationToken(principal, "secret", principal.getAuthorities());
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);
  }

  @Test
  void createAudit_devraitDeleguerAuServiceAvecNomUtilisateurAuthentifie() throws Exception {
    // Arrange
    setAuthenticatedUser("john.doe");
    AuditRequest request = mock(AuditRequest.class);
    AuditResponse body = new AuditResponse();
    when(auditService.createAudit(request, "john.doe")).thenReturn(body);

    // Act
    ResponseEntity<AuditResponse> response = auditController.createAudit(request);

    // Assert
    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertSame(body, response.getBody());
    verify(auditService, times(1)).createAudit(request, "john.doe");
  }

  @Test
  void updateAudit_devraitDeleguerAuServiceAvecNomUtilisateurAuthentifie() throws Exception {
    // Arrange
    setAuthenticatedUser("jane.doe");
    AuditRequest request = mock(AuditRequest.class);
    AuditResponse body = new AuditResponse();
    when(auditService.updateAudit(request, "jane.doe")).thenReturn(body);

    // Act
    AuditResponse response = auditController.updateAudit(request);

    // Assert
    assertSame(body, response);
    verify(auditService, times(1)).updateAudit(request, "jane.doe");
  }

  @Test
  void getAuditsByProject_devraitRetournerListeDepuisService() throws Exception {
    // Arrange
    List<AuditResponse> expected = List.of(new AuditResponse(), new AuditResponse());
    when(auditService.getAuditsByProject(projectId)).thenReturn(expected);

    // Act
    List<AuditResponse> result = auditController.getAuditsByProject(projectId);

    // Assert
    assertSame(expected, result);
    verify(auditService, times(1)).getAuditsByProject(projectId);
  }

  @Test
  void getAuditsByProject_devraitPropagerProjectNotFoundException() throws Exception {
    // Arrange
    when(auditService.getAuditsByProject(projectId))
      .thenThrow(new ProjectNotFoundException("not found"));

    // Act + Assert
    assertThrows(ProjectNotFoundException.class,
      () -> auditController.getAuditsByProject(projectId));
    verify(auditService, times(1)).getAuditsByProject(projectId);
  }

  @Test
  void calculateScoreOfTheAudit_shouldReturnResponse() throws AuditNotFoundException {
    when(auditService.calculateScoringOfAudit(auditId)).thenReturn(AuditScoreResponse.builder().build());
    auditController.calculateScoreOfTheAudit(auditId);

    verify(auditService, times(1)).calculateScoringOfAudit(eq(auditId));
  }

  @Test
  void calculateScoreOfTheAudit_shouldPropagateAuditNotFoundException() throws AuditNotFoundException {
    doThrow(new AuditNotFoundException("Audit not found"))
      .when(auditService).calculateScoringOfAudit(auditId);

    assertThrows(AuditNotFoundException.class,
      () -> auditController.calculateScoreOfTheAudit(auditId));
    verify(auditService, times(1)).calculateScoringOfAudit(auditId);
  }

  @Test
  void findAuditScoreByAuditId_shouldReturnResponse() throws AuditNotFoundException {
    AuditScoreResponse auditScore = AuditScoreResponse.builder().build();
    when(auditScoreService.findAuditScoreByAuditId(auditId)).thenReturn(auditScore);

    AuditScoreResponse response = auditController.findAuditScoreByAuditId(auditId);

    verify(auditScoreService, times(1)).findAuditScoreByAuditId(eq(auditId));
    assertSame(auditScore, response);
  }

  @Test
  void findAuditScoreByAuditId_shouldPropagateAuditNotFoundException() throws AuditNotFoundException {
    when(auditScoreService.findAuditScoreByAuditId(auditId))
      .thenThrow(new AuditNotFoundException("not found"));

    assertThrows(AuditNotFoundException.class,
      () -> auditController.findAuditScoreByAuditId(auditId));
    verify(auditScoreService, times(1)).findAuditScoreByAuditId(auditId);
  }

  @Test
  void getAuditByProjectAndAuditId_shouldReturnAudit_whenFound() throws Exception {
    // Arrange
    AuditResponse expected = new AuditResponse();
    when(auditService.getAuditByProjectAndAuditId(projectId, auditId)).thenReturn(expected);

    // Act
    AuditResponse response = auditController.getAuditByProjectAndAuditId(projectId, auditId);

    // Assert
    assertSame(expected, response);
    verify(auditService, times(1)).getAuditByProjectAndAuditId(projectId, auditId);
  }

  @Test
  void getAuditByProjectAndAuditId_shouldPropagateProjectNotFound() throws Exception {
    // Arrange
    when(auditService.getAuditByProjectAndAuditId(projectId, auditId))
      .thenThrow(new ProjectNotFoundException("not found"));

    // Act + Assert
    assertThrows(ProjectNotFoundException.class,
      () -> auditController.getAuditByProjectAndAuditId(projectId, auditId));
    verify(auditService, times(1)).getAuditByProjectAndAuditId(projectId, auditId);
  }

  @Test
  void getAuditByProjectAndAuditId_shouldPropagateAuditNotFound() throws Exception {
    // Arrange
    when(auditService.getAuditByProjectAndAuditId(projectId, auditId))
      .thenThrow(new AuditNotFoundException("not found"));

    // Act + Assert
    assertThrows(AuditNotFoundException.class,
      () -> auditController.getAuditByProjectAndAuditId(projectId, auditId));
    verify(auditService, times(1)).getAuditByProjectAndAuditId(projectId, auditId);
  }

  @Test
  void hasAuditForProject_shouldReturnTrue_whenProjectHasAudit() throws Exception {
    when(auditService.hasAuditForProject(projectId)).thenReturn(true);
    Assertions.assertTrue(auditController.hasAuditForProject(projectId));
    verify(auditService, times(1)).hasAuditForProject(projectId);
  }

  @Test
  void hasAuditForProject_shouldReturnFalse_whenProjectHasNoAudit() throws Exception {
    when(auditService.hasAuditForProject(projectId)).thenReturn(false);
    Assertions.assertFalse(auditController.hasAuditForProject(projectId));
    verify(auditService, times(1)).hasAuditForProject(projectId);
  }

  @Test
  void hasAuditForProject_shouldPropagateProjectNotFound() throws Exception {
    when(auditService.hasAuditForProject(projectId)).thenThrow(new ProjectNotFoundException("not found"));
    assertThrows(ProjectNotFoundException.class,
      () -> auditController.hasAuditForProject(projectId));
    verify(auditService, times(1)).hasAuditForProject(projectId);
  }
}
