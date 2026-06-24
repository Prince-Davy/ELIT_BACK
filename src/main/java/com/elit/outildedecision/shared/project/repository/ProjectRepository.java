package com.elit.outildedecision.shared.project.repository;

import com.elit.outildedecision.shared.kernel.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing projects.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
