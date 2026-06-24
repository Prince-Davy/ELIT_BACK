package com.elit.outildedecision.audit.service.implementation;

import com.elit.outildedecision.audit.dto.AuditScoreResponse;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.exception.AuditNotFoundException;
import com.elit.outildedecision.audit.mapper.AuditMapper;
import com.elit.outildedecision.audit.repository.AuditScoreRepository;
import com.elit.outildedecision.audit.service.AuditScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link AuditScoreService} interface.
 * Provides business logic for retrieving audit score data from the repository
 * and mapping it to DTOs.
 */
@Service
@Slf4j
public class AuditScoreServiceImpl implements AuditScoreService {

  private final AuditScoreRepository auditScoreRepository;
  private final AuditMapper auditMapper;

  /**
   * Constructs an instance of {@code AuditScoreServiceImpl} with required dependencies.
   *
   * @param auditScoreRepository The repository used to access audit score data.
   * @param auditMapper          The mapper used to convert entities to DTOs.
   */
  public AuditScoreServiceImpl(final AuditScoreRepository auditScoreRepository, final AuditMapper auditMapper) {
    this.auditScoreRepository = auditScoreRepository;
    this.auditMapper = auditMapper;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public AuditScoreResponse findAuditScoreByAuditId(final Long auditId) {
    final AuditScore auditScore = auditScoreRepository.findByAudit_AuditId(auditId)
        .orElseThrow(() -> new AuditNotFoundException("AuditScore not found for auditId: " + auditId));

    return auditMapper.toAuditScoreResponse(auditScore);
  }

}
