package com.elit.outildedecision.audit.service.impl;

import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.mapper.AuditMapper;
import com.elit.outildedecision.audit.model.AuditScoreDTO;
import com.elit.outildedecision.audit.repository.AuditScoreRepository;
import com.elit.outildedecision.audit.service.AuditScoreService;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

/**
 * Implementation of the {@link AuditScoreService} interface.
 * Provides business logic for retrieving audit score data from the repository
 * and mapping it to DTOs.
 */
@Service
public class AuditScoreServiceImpl implements AuditScoreService {

  private final AuditScoreRepository auditScoreRepository;
  private final AuditMapper  auditMapper;


  /**
   * Constructs an instance of {@code AuditScoreServiceImpl} with required dependencies.
   *
   * @param auditScoreRepository The repository used to access audit score data.
   * @param auditMapper The mapper used to convert domain models to DTOs.
   */
  public AuditScoreServiceImpl(final AuditScoreRepository auditScoreRepository, final AuditMapper auditMapper) {
    this.auditScoreRepository = auditScoreRepository;
    this.auditMapper = auditMapper;
  }


  /**
   * Retrieves the audit score for a given audit ID and maps it to a DTO.
   *
   * @param auditId The ID of the audit.
   * @return The {@link AuditScoreDTO} containing the audit score details.
   * @throws EntityNotFoundException if no audit score is found for the given audit ID.
   */
  @Override
  public AuditScoreDTO findAuditScoreByAuditId(final Long auditId) throws  EntityNotFoundException {
    AuditScore auditScore = auditScoreRepository.findByAudit_AuditId(auditId)
      .orElseThrow(() -> new EntityNotFoundException("AuditScore not found for auditId: " + auditId));

    return auditMapper.auditScoretoAuditScoreDto(auditScore);
  }

}
