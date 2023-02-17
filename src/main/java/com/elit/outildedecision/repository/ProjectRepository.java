package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
  List<Project> findAllByConfirmationState(String confirmationState);

  Page<Project> findAllByConfirmationState(String confirmationState, Pageable pageable);

  Page<Project> findAllByConfirmationStateOrderByCreatedAtDesc(String name, Pageable of);

  Page<Project> findAllByConfirmationStateOrderByCreatedAtAsc(String name, Pageable of);

  @Query("select p from Project p where p.name like CONCAT('%',?1,'%')")
  Page<Project> findByNameOrDescriptionContainingIgnoreCase(String keyword, Pageable pageable);
}
