package com.elit.outildedecision.audit.controller;

import com.elit.outildedecision.audit.model.AuditRequestModel;
import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.service.AuditService;
import com.elit.outildedecision.exception.ProjectNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

  @Mock
  private AuditService auditService;

  @InjectMocks
  private AuditController auditController;

  @AfterEach
  void cleanupSecurityContext() {
    SecurityContextHolder.clearContext();
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
    AuditRequestModel request = mock(AuditRequestModel.class);
    AuditResponseModel body = new AuditResponseModel();
    ResponseEntity<AuditResponseModel> expected =
      ResponseEntity.status(HttpStatus.CREATED).body(body);
    when(auditService.createAudit(request, "john.doe")).thenReturn(expected);

    // Act
    ResponseEntity<AuditResponseModel> response = auditController.createAudit(request);

    // Assert
    assertSame(expected, response);
    verify(auditService, times(1)).createAudit(request, "john.doe");
  }

  @Test
  void updateAudit_devraitDeleguerAuServiceAvecNomUtilisateurAuthentifie() throws Exception {
    // Arrange
    setAuthenticatedUser("jane.doe");
    AuditRequestModel request = mock(AuditRequestModel.class);
    AuditResponseModel body = new AuditResponseModel();
    ResponseEntity<AuditResponseModel> expected =
      ResponseEntity.status(HttpStatus.CREATED).body(body);
    when(auditService.updateAudit(request, "jane.doe")).thenReturn(expected);

    // Act
    ResponseEntity<AuditResponseModel> response = auditController.updateAudit(request);

    // Assert
    assertSame(expected, response);
    verify(auditService, times(1)).updateAudit(request, "jane.doe");
  }

  @Test
  void getAuditsByProject_devraitRetournerListeDepuisService() throws Exception {
    // Arrange
    Long projectId = 42L;
    List<AuditResponseModel> expected = List.of(new AuditResponseModel(), new AuditResponseModel());
    when(auditService.getAuditsByProject(projectId)).thenReturn(expected);

    // Act
    List<AuditResponseModel> result = auditController.getAuditsByProject(projectId);

    // Assert
    assertSame(expected, result);
    verify(auditService, times(1)).getAuditsByProject(projectId);
  }

  @Test
  void getAuditsByProjectPaginated_devraitRetournerPageDepuisService() throws Exception {
    // Arrange
    Long projectId = 7L;
    int page = 1;
    int size = 20;
    List<AuditResponseModel> content = List.of(new AuditResponseModel());
    Page<AuditResponseModel> expected =
      new PageImpl<>(content, PageRequest.of(page, size), content.size());
    when(auditService.getAuditsByProjectPaginated(projectId, page, size)).thenReturn(expected);

    // Act
    Page<AuditResponseModel> result =
      auditController.getAuditsByProjectPaginated(projectId, page, size);

    // Assert
    assertSame(expected, result);
    verify(auditService, times(1)).getAuditsByProjectPaginated(projectId, page, size);
  }

  @Test
  void getAuditsByProject_devraitPropagerProjectNotFoundException() throws Exception {
    // Arrange
    Long projectId = 999L;
    when(auditService.getAuditsByProject(projectId))
      .thenThrow(new ProjectNotFoundException("not found"));

    // Act + Assert
    assertThrows(ProjectNotFoundException.class,
      () -> auditController.getAuditsByProject(projectId));
    verify(auditService, times(1)).getAuditsByProject(projectId);
  }
}
