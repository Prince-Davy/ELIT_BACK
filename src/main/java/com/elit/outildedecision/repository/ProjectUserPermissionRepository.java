package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.ProjectPropertyKey;
import com.elit.outildedecision.entity.ProjectUserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProjectUserPermissionRepository extends JpaRepository<ProjectUserPermission, ProjectPropertyKey> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ProjectUserPermission WHERE  project_id=:pProjectID and user_id=:pUserId")
    void deleteByProjectUser(@Param("pProjectID") Long projectId, @Param("pUserId") Long userID);

    @Query("SELECT p FROM ProjectUserPermission p WHERE  project_id=:pProjectID and user_id=:pUserId")
    ProjectUserPermission findByProjectIdUserID(@Param("pProjectID") Long projectId, @Param("pUserId") Long userID);

    @Query("SELECT p FROM ProjectUserPermission p WHERE p.project.projectId=:pProjectID")
    List<ProjectUserPermission> findAllByProjectId(@Param("pProjectID") Long projectId);

}
