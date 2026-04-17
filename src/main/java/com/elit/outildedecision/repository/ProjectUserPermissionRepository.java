
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.ProjectPropertyKey;
import com.elit.outildedecision.entity.ProjectUserPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository interface for managing project user permissions.
 */
@Repository
public interface ProjectUserPermissionRepository extends JpaRepository<ProjectUserPermission, ProjectPropertyKey> {

  /**
   * Deletes the project user permission with the given project ID and user ID.
   *
   * @param projectId the ID of the project
   * @param userId the ID of the user
   */
  @Transactional
  @Modifying
  @Query("DELETE FROM ProjectUserPermission pp "
    + "WHERE  pp.project.projectId=:pProjectID "
    + "and pp.user.userId=:pUserId")
  void deleteByProjectUser(@Param("pProjectID") Long projectId, @Param("pUserId") Long userId);

  /**
   * Finds the project user permission with the given project ID and user ID.
   *
   * @param projectId the ID of the project
   * @param userId the ID of the user
   * @return the project user permission with the given project ID and user ID, or null if not found
   */
  @Query("SELECT p FROM ProjectUserPermission p "
    + "WHERE  p.project.projectId=:pProjectID "
    + "and p.user.userId=:pUserId")
  ProjectUserPermission findByProjectIdUserID(
    @Param("pProjectID") Long projectId, @Param("pUserId") Long userId);

  /**
   * Finds all project user permissions for the given project ID.
   *
   * @param projectId the ID of the project
   * @return a list of all project user permissions for the given project ID
   */
  @Query("SELECT p FROM ProjectUserPermission p "
    + "WHERE p.project.projectId=:pProjectID")
  List<ProjectUserPermission> findAllByProjectId(@Param("pProjectID") Long projectId);

  /**
   * Finds all project user permissions for the given user ID.
   *
   * @param pageable the pagination information
   * @param keyword the keyword to search for in project name or description
   * @param status the project status to filter by
   * @param userEmail email user
   * @return a list of all project user permissions for the given user ID
   */
  @Query("SELECT p FROM ProjectUserPermission p "
    + "WHERE (LOWER(p.project.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
    + "LOWER(p.project.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
    + "(:status = 'ALL' OR :status IS NULL OR LOWER(p.project.projectState) = LOWER(:status)) "
    + "AND p.user.userEmail=:userEmail "
    + "GROUP BY p")
  Page<ProjectUserPermission> findProject(
    @Param("keyword") String keyword,
    @Param("status") String status,
    @Param("userEmail") String userEmail,
    Pageable pageable);
  /**
   * Finds all project user permissions for the given user ID.
   *
   * @param keyword the keyword to search for in project name or description
   * @param status the project status to filter by
   * @param typeAnalyse the project type analyse filter by
   * @param userEmail email user
   * @return Totale Element
   */
  @Query("SELECT count(*) FROM ProjectUserPermission p "
          + "WHERE (LOWER(p.project.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(p.project.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:status = 'ALL' OR :status IS NULL OR LOWER(p.project.projectState) = LOWER(:status)) AND "
          + "(:typeAnalyse = 'ALL' OR :typeAnalyse IS NULL OR (LOWER(p.project.formProgressStatus) = LOWER(:typeAnalyse) OR (p.project.formProgressStatus='BOTH' AND :typeAnalyse!='AUDIT'))) AND "
          + "p.user.userEmail=:userEmail and  p.project.chefId = p.user.userId")
    long getTotalProjectCreatedUser(
            @Param("keyword") String keyword,
            @Param("typeAnalyse") String typeAnalyse,
            @Param("status") String status,
            @Param("userEmail") String userEmail);
  /**
   * Finds all project user permissions for the given user ID.
   *
   * @param keyword the keyword to search for in project name or description
   * @param status the project status to filter by
   * @param typeAnalyse the project type analyse filter by
   * @param userEmail email user
   * @return Totale Element
   */
  @Query("SELECT count(*) FROM ProjectUserPermission p "
          + "WHERE (LOWER(p.project.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(p.project.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:status = 'ALL' OR :status IS NULL OR LOWER(p.project.projectState) = LOWER(:status)) AND "
          + "(:typeAnalyse = 'ALL' OR :typeAnalyse IS NULL OR (LOWER(p.project.formProgressStatus) = LOWER(:typeAnalyse) OR (p.project.formProgressStatus='BOTH' AND :typeAnalyse!='AUDIT'))) AND "
          + "p.user.userEmail=:userEmail and  p.project.chefId != p.user.userId")
    long getTotalProjectAffectedUser(
            @Param("keyword") String keyword,
            @Param("typeAnalyse") String typeAnalyse,
            @Param("status") String status,
            @Param("userEmail") String userEmail
    );

  /**
   * Finds all project user permissions for the given user ID.
   *
   * @param pageable the pagination information
   * @param keyword the keyword to search for in project name or description
   * @param status the project status to filter by
   * @param typeAnalyse the project type analyse to filter by
   * @param userEmail email user
   * @return a list of all project user permissions for the given user ID
   */
  @Query("SELECT p FROM ProjectUserPermission p "
          + "WHERE (LOWER(p.project.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(p.project.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:status = 'ALL' OR :status IS NULL OR LOWER(p.project.projectState) = LOWER(:status)) AND "
          + "(:typeAnalyse = 'ALL' OR :typeAnalyse IS NULL OR (LOWER(p.project.formProgressStatus) = LOWER(:typeAnalyse) OR (p.project.formProgressStatus='BOTH' AND :typeAnalyse!='AUDIT'))) AND "
          + "p.user.userEmail=:userEmail  and  p.project.chefId != p.user.userId "
          + "GROUP BY p")
  Page<ProjectUserPermission> findProjectAffected(
          @Param("keyword") String keyword,
          @Param("typeAnalyse") String typeAnalyse,
          @Param("status") String status,
          @Param("userEmail") String userEmail,
          Pageable pageable);

  /**
   * Finds all project user permissions for the given user ID.
   *
   * @param pageable the pagination information
   * @param keyword the keyword to search for in project name or description
   * @param status the project status to filter by
   * @param typeAnalyse the project type analyse to filter by
   * @param userEmail email user
   * @return a list of all project user permissions for the given user ID
   */
  @Query("SELECT p FROM ProjectUserPermission p "
    + "WHERE (LOWER(p.project.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
    + "LOWER(p.project.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
    + "(:status = 'ALL' OR :status IS NULL OR LOWER(p.project.projectState) = LOWER(:status)) AND "
    + "size(p.project.audits) > 0 AND "
    + "p.user.userEmail=:userEmail  and  p.project.chefId != p.user.userId "
    + "GROUP BY p")
  Page<ProjectUserPermission> findProjectWithAuditAffected(
    @Param("keyword") String keyword,
    @Param("typeAnalyse") String typeAnalyse,
    @Param("status") String status,
    @Param("userEmail") String userEmail,
    Pageable pageable);

  /**
   * Finds all project user permissions for the given user ID.
   *
   * @param pageable the pagination information
   * @param keyword the keyword to search for in project name or description
   * @param status the project status to filter by
   * @param typeAnalyse the project type analyse to filter by
   * @param userEmail email user
   * @return a list of all project user permissions for the given user ID
   */
  @Query("SELECT p FROM ProjectUserPermission p "
          + "WHERE (LOWER(p.project.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
          + "LOWER(p.project.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
          + "(:status = 'ALL' OR :status IS NULL OR LOWER(p.project.projectState) = LOWER(:status)) AND "
          + "(:typeAnalyse = 'ALL' OR :typeAnalyse IS NULL OR (LOWER(p.project.formProgressStatus) = LOWER(:typeAnalyse) OR (p.project.formProgressStatus='BOTH' AND :typeAnalyse!='AUDIT'))) AND "
          + "p.user.userEmail=:userEmail  and  p.project.chefId = p.user.userId "
          + "GROUP BY p")
  Page<ProjectUserPermission> findProjectCreatedByUser(
          @Param("keyword") String keyword,
          @Param("typeAnalyse") String typeAnalyse,
          @Param("status") String status,
          @Param("userEmail") String userEmail,
          Pageable pageable);

  /**
   * Finds all project user permissions for the given user ID.
   *
   * @param pageable the pagination information
   * @param keyword the keyword to search for in project name or description
   * @param status the project status to filter by
   * @param typeAnalyse the project type analyse to filter by
   * @param userEmail email user
   * @return a list of all project user permissions for the given user ID
   */
  @Query("SELECT p FROM ProjectUserPermission p "
    + "WHERE (LOWER(p.project.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
    + "LOWER(p.project.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
    + "(:status = 'ALL' OR :status IS NULL OR LOWER(p.project.projectState) = LOWER(:status)) AND "
    + "size(p.project.audits) > 0 AND "
    + "p.user.userEmail=:userEmail  and  p.project.chefId = p.user.userId "
    + "GROUP BY p")
  Page<ProjectUserPermission> findProjectWithAuditCreatedByUser(
    @Param("keyword") String keyword,
    @Param("typeAnalyse") String typeAnalyse,
    @Param("status") String status,
    @Param("userEmail") String userEmail,
    Pageable pageable);
}
