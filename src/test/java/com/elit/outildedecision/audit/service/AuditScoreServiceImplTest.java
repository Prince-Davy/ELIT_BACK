package com.elit.outildedecision.audit.service;


import com.elit.outildedecision.audit.dto.AuditScoreResponse;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.exception.AuditNotFoundException;
import com.elit.outildedecision.audit.mapper.AuditMapper;
import com.elit.outildedecision.audit.repository.AuditScoreRepository;
import com.elit.outildedecision.audit.service.implementation.AuditScoreServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditScoreServiceImplTest {

  private static final Long AUDIT_ID = 42L;

  @Mock
  private AuditScoreRepository auditScoreRepository;

  @Mock
  private AuditMapper auditMapper;

  @InjectMocks
  private AuditScoreServiceImpl auditScoreService;


  @Test
  public void returnsDto_whenFound() {

    AuditScore entity = mock(AuditScore.class);
    AuditScoreResponse expectedDto = mock(AuditScoreResponse.class);

    when(auditScoreRepository.findByAudit_AuditId(AUDIT_ID)).thenReturn(Optional.of(entity));
    when(auditMapper.toAuditScoreResponse(entity)).thenReturn(expectedDto);

    AuditScoreResponse result = auditScoreService.findAuditScoreByAuditId(AUDIT_ID);

    assertSame(expectedDto, result, "Le service doit renvoyer le DTO mappé");

    verify(auditScoreRepository).findByAudit_AuditId(AUDIT_ID);
    verify(auditMapper).toAuditScoreResponse(entity);
    verifyNoMoreInteractions(auditScoreRepository, auditMapper);
  }


  @Test
  void throwsAuditNotFound_whenMissing() {
    when(auditScoreRepository.findByAudit_AuditId(AUDIT_ID)).thenReturn(Optional.empty());

    AuditNotFoundException ex = assertThrows(
      AuditNotFoundException.class,
      () -> auditScoreService.findAuditScoreByAuditId(AUDIT_ID),
      "Doit lever AuditNotFoundException quand aucun score n'est trouvé"
    );

    assertTrue(ex.getMessage().contains(String.valueOf(AUDIT_ID)));

    verify(auditScoreRepository).findByAudit_AuditId(AUDIT_ID);
    verifyNoInteractions(auditMapper);
    verifyNoMoreInteractions(auditScoreRepository);
  }

}
