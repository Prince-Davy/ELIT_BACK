package com.elit.outildedecision.features.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
   * Find audits by project ID with pagination following Open/Closed Principle.
   * Extensible for different pagination strategies.
   * @param projectId the project identifier
   * @param pageable  pagination information
   * @return page of audits ordered by creation date
   */
  @Query("SELECT a FROM Audit a WHERE a.project.projectId = :projectId ORDER BY a.auditLaunchDate DESC")
  Page<Audit> findByProjectProjectIdOrderByCreatedAtDesc(@Param("projectId") Long projectId,
      @Param("pageable") Pageable pageable);

  /**
   * Find audits by user email following Liskov Substitution Principle.
   * Can substitute different user identification methods.
   * @param userEmail the user email
   * @return list of user's audits
   */
  @Query("SELECT a FROM Audit a WHERE a.user.userEmail = :userEmail ORDER BY a.auditLaunchDate DESC")
  List<Audit> findByUserEmailOrderByCreatedAtDesc(@Param("userEmail") String userEmail);

  /**
   * Count audits by project following Dependency Inversion Principle.
   * Depends on project abstraction, not concrete implementation.
   * @param projectId the project identifier
   * @return count of audits for the project
   */
  @Query("SELECT COUNT(a) FROM Audit a WHERE a.project.projectId = :projectId")
  Long countAuditsByProjectId(@Param("projectId") Long projectId);
}
