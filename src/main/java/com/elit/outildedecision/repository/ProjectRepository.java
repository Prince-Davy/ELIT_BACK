
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing projects.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

  /**
   * Finds all projects with a given confirmation state.
   *
   * @param confirmationState the confirmation state to search for
   * @return a list of projects with the given confirmation state
   */
  List<Project> findAllByConfirmationState(String confirmationState);

  /**
   * Finds all projects with a given confirmation state and returns them as a pageable list.
   *
   * @param confirmationState the confirmation state to search for
   * @param pageable the pageable object to use for pagination
   * @return a pageable list of projects with the given confirmation state
   */
  Page<Project> findAllByConfirmationState(String confirmationState, Pageable pageable);

  /**
   * Finds all projects with a given confirmation state and orders them by creation date, descending.
   *
   * @param name the confirmation state to search for
   * @param of the pageable object to use for pagination
   * @return a pageable list of projects with the given confirmation state, ordered by creation date in descending order
   */
  Page<Project> findAllByConfirmationStateOrderByCreatedAtDesc(String name, Pageable of);

  /**
   * Finds all projects with a given confirmation state and orders them by creation date, ascending.
   *
   * @param name the confirmation state to search for
   * @param of the pageable object to use for pagination
   * @return a pageable list of projects with the given confirmation state, ordered by creation date in ascending order
   */
  Page<Project> findAllByConfirmationStateOrderByCreatedAtAsc(String name, Pageable of);

  /**
   * Finds all projects whose name or description contains the given keyword, ignoring case.
   *
   * @param keyword the keyword to search for
   * @param pageable the pageable object to use for pagination
   * @return a pageable list of projects whose name or description contains the given keyword, ignoring case
   */
  @Query("select p from Project p where p.name like CONCAT('%',?1,'%')")
  Page<Project> findByNameOrDescriptionContainingIgnoreCase(String keyword, Pageable pageable);
}
