
package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.service.SuperAdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

/**
 * SuperAdminController.
 */
@RestController
@RequestMapping("/")
public class SuperAdminController {
  /**
   * SuperAdminService.
   */
  private final SuperAdminService superAdminService;
  /**
   * Constructor of SuperAdminController.
   * dependency injection of SuperAdminService.
   * @param superAdminService SuperAdminService
   */
  public SuperAdminController(final SuperAdminService superAdminService) {
    this.superAdminService = superAdminService;
  }

  /**
   * Confirm a project.
   * @param projectId the project id
   * @throws ProjectNotFoundException ProjectNotFoundException if the project is not found in the database
   * @return ResponseEntity
   */
  @PutMapping("/projects/{projectId}")
  public ResponseEntity<String> confirmProject(@PathVariable final Long projectId)
      throws ProjectNotFoundException {
    return superAdminService.confirmProject(projectId);
  }
  /**
   * Suspend a user.
   * @param userId the user id
   * @throws UserNotFoundException UserNotFoundException if the user is not found in the database
   * @return ResponseEntity
   */
  @PutMapping("/users/{userId}")
  public ResponseEntity<String> suspendUser(@PathVariable final Long userId) throws UserNotFoundException {
    return superAdminService.suspendUser(userId);
  }
  /**
   * Delete a project.
   *
   * @param projectId the project id
   * @throws ProjectNotFoundException ProjectNotFoundException if the project is not found in the database
   * @return ResponseEntity
   */
  @DeleteMapping("/projects/{projectId}")
  public ResponseEntity<String> deleteProject(@PathVariable final Long projectId)
      throws ProjectNotFoundException {
    return superAdminService.deleteProject(projectId);
  }
  /**
   * Get all non-Confirmed projects.
   * @param page the page number
   * @return Page<Project>
   */
  @GetMapping("/projects/notConfirmed/{page}")
  public Page<Project> getAllNonConfirmedProjects(@PathVariable final int page) {
    return superAdminService.getAllNonConfirmedProjects(page);
  }
  /**
   * Get all confirmed projects ordered by creation date.
   * @param page the page number
   * @param order the order
   *              ASC or DESC
   * @return Page<Project>
   */
  @GetMapping("/projects/notConfirmed/{page}/orderByDate")
  public Page<Project> getAllConfirmedProjectsOrderByDate(
      @PathVariable final int page, @RequestParam final String order) {
    return superAdminService.getAllNonConfirmedProjectsOrderByDate(page, order);
  }
  /**
   * Confirm a list of projects based on their ids.
   * @param projectIds the list of project ids
   *                   to be confirmed
   * @return ResponseEntity
   * @throws ProjectNotFoundException ProjectNotFoundException if the project is not found
   */
  @PutMapping("/projects/confirmProjects")
  public ResponseEntity<String> confirmProjects(@RequestBody final List<Long> projectIds)
      throws ProjectNotFoundException {
    return superAdminService.confirmProjects(projectIds);
  }
  /**
   * find a project by keyword.
   * @param keyword the keyword
   *                to be searched
   * @param page the page number
   *             to be returned
   * @return Page<Project>
   *      the page of projects
   */
  @GetMapping("/projects/notConfirmed/{page}/search")
  public Page<Project> findNotConfirmedProjectByKeyword(
      @RequestParam final String keyword, @PathVariable final int page) {
    return superAdminService.findNotConfirmedProjectByKeyword(keyword, page);
  }
}
