
package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * This interface defines the contract for the Super Admin Service, which provides methods for super admin users to
 * confirm projects, suspend users, and manage non-confirmed projects.
 */
public interface SuperAdminService {

  /**
   * Confirms the project with the specified ID.
   *
   * @param projectId the ID of the project to confirm
   * @return a ResponseEntity indicating the success or failure of the confirmation operation
   * @throws ProjectNotFoundException if the specified project does not exist
   */
  ResponseEntity confirmProject(Long projectId) throws ProjectNotFoundException;

  /**
   * Suspends the user with the specified ID.
   *
   * @param userId the ID of the user to suspend
   * @return a ResponseEntity indicating the success or failure of the suspension operation
   * @throws UserNotFoundException if the specified user does not exist
   */
  ResponseEntity suspendUser(Long userId) throws UserNotFoundException;

  /**
   * Deletes the project with the specified ID.
   *
   * @param projectId the ID of the project to delete
   * @return a ResponseEntity indicating the success or failure of the deletion operation
   * @throws ProjectNotFoundException if the specified project does not exist
   */
  ResponseEntity deleteProject(Long projectId) throws ProjectNotFoundException;

  /**
   * Retrieves a paginated list of non-confirmed projects.
   *
   * @param page the page number to retrieve
   * @return a Page of Project objects representing the non-confirmed projects
   */
  Page<Project> getAllNonConfirmedProjects(int page);

  /**
   * Retrieves a paginated list of non-confirmed projects, ordered by date.
   *
   * @param page the page number to retrieve
   * @param order a String representing the order to sort the projects by
   * @return a Page of Project objects representing the non-confirmed projects, sorted by date
   */
  Page<Project> getAllNonConfirmedProjectsOrderByDate(int page, String order);

  /**
   * Confirms the projects with the specified IDs.
   *
   * @param projectIds the List of IDs of the projects to confirm
   * @return a ResponseEntity indicating the success or failure of the confirmation operation
   * @throws ProjectNotFoundException if any of the specified projects do not exist
   */
  ResponseEntity confirmProjects(List<Long> projectIds) throws ProjectNotFoundException;

  /**
   * Retrieves a paginated list of non-confirmed projects that match the specified keyword.
   *
   * @param keyword a String representing the keyword to search for
   * @param page the page number to retrieve
   * @return a Page of Project objects representing the non-confirmed projects that match the specified keyword
   */
  Page<Project> findNotConfirmedProjectByKeyword(String keyword, int page);
}
