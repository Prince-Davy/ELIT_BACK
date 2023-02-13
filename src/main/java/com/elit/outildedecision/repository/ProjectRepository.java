package com.elit.outildedecision.repository;


import com.elit.outildedecision.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByDescriptionContainsOrNameContains(String keyword, String keyword1, Pageable pageable);


    @Query("select mu.project from MultiCriteriaAnalysis mu where mu.predictibilityAnalysis.executionPlan.executionPlanId = ?1")
    Project findProjectByExecutionPlanId(Long id);

    List<Project> findAllByConfirmationState(String confirmationState);

    Page<Project> findAllByConfirmationState(String confirmationState, Pageable pageable);

    Page<Project> findAllByConfirmationStateOrderByCreatedAtDesc(String name, Pageable of);

    Page<Project> findAllByConfirmationStateOrderByCreatedAtAsc(String name, Pageable of);

   @Query("select p from Project p where p.name like CONCAT('%',?1,'%')")
    Page<Project> findByNameOrDescriptionContainingIgnoreCase(String keyword, Pageable pageable);

}