package com.elit.outildedecision.audit.repository;

import com.elit.outildedecision.audit.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Audit repository following Single Responsibility Principle.
 * Responsible only for audit data access operations.
 */
@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

  /**
   * Find audits by project ID following Interface Segregation Principle.
   * Separates project-specific audit queries.
   * @param projectId the project identifier
   * @return list of audits ordered by creation date
   */
  @Query("SELECT a FROM Audit a WHERE a.project.projectId = :projectId ORDER BY a.auditLaunchDate DESC")
  List<Audit> findByProjectProjectIdOrderByCreatedAtDesc(@Param("projectId") Long projectId);

  /**
   * Count audits by project following Dependency Inversion Principle.
   * Depends on project abstraction, not concrete implementation.
   * @param projectId the project identifier
   * @return count of audits for the project
   */
  @Query("SELECT COUNT(a) FROM Audit a WHERE a.project.projectId = :projectId")
  Long countAuditsByProjectId(@Param("projectId") Long projectId);
}
