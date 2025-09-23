package com.elit.outildedecision.audit.repository;

import com.elit.outildedecision.audit.entity.AuditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link AuditScore} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 */
@Repository
public interface AuditScoreRepository extends JpaRepository<AuditScore, Long> {

  /**
   * Finds an {@link AuditScore} by the associated audit's ID.
   *
   * @param auditId The ID of the audit.
   * @return An {@link Optional} containing the {@link AuditScore} if found, or empty if not.
   */
  Optional<AuditScore> findByAudit_AuditId(Long auditId);
}
