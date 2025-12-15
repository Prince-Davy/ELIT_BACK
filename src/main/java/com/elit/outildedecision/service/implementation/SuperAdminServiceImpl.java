
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.SuperAdminService;
import com.elit.outildedecision.util.ProjectConfirmationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Service implementation for super admin operations.
 */
@Service
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

  /**
   * Number of projects to show per page.
   */
  private static final int PAGE_SIZE = 10;
  /**
   * Project repository for accessing project data.
   */
  private final ProjectRepository projectRepository;
  /**
   * User repository for accessing user data.
   */
  private final UserRepository userRepository;

  /**
   * Constructor for creating a new SuperAdminServiceImpl instance.
   *
   * @param projectRepository The project repository to use.
   * @param userRepository    The user repository to use.
   */
  public SuperAdminServiceImpl(final ProjectRepository projectRepository,
                               final UserRepository userRepository) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  /**
   * Confirms the specified project by setting its confirmation state to "CONFIRMED".
   *
   * @param projectId The ID of the project to confirm.
   * @return A response entity with a message indicating that the project has been confirmed.
   * @throws ProjectNotFoundException If the specified project does not exist.
   */
  @Override
  public ResponseEntity<String> confirmProject(final Long projectId) throws ProjectNotFoundException {
    Project project =
      projectRepository
        .findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas!"));
    project.setConfirmationState(ProjectConfirmationState.CONFIRMED.name());
    projectRepository.save(project);
    return ResponseEntity.ok("Votre projet a été confirmé");
  }

  /**
   * Suspends the specified user by setting their "isUserActive" field to false.
   *
   * @param userId The ID of the user to suspend.
   * @return A response entity with a message indicating that the user has been suspended.
   * @throws UserNotFoundException If the specified user does not exist.
   */
  @Override
  public ResponseEntity<String> suspendUser(final Long userId) throws UserNotFoundException {
    User user =
      userRepository
        .findById(userId)
        .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas!"));
    user.setIsUserActive(false);
    userRepository.save(user);
    return ResponseEntity.ok("L'utilisateur a été suspendu");
  }

  /**
   * Deletes the specified project.
   *
   * @param projectId The ID of the project to delete.
   * @return A response entity with a message indicating that the project has been deleted.
   * @throws ProjectNotFoundException If the specified project does not exist.
   */
  @Override
  public ResponseEntity<String> deleteProject(final Long projectId) throws ProjectNotFoundException {
    Project project =
      projectRepository
        .findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas!"));
    projectRepository.delete(project);
    return ResponseEntity.ok("Le projet a été supprimé");
  }

  /**
   * Retrieves a page of non-confirmed projects.
   *
   * @param page The page number to retrieve.
   * @return A page of non-confirmed projects.
   */
  @Override
  public Page<Project> getAllNonConfirmedProjects(final int page) {
    return projectRepository.findAllByConfirmationState(
      ProjectConfirmationState.WAITING.name(), PageRequest.of(page, PAGE_SIZE));
  }

  /**
   * Retrieves a page of non-confirmed projects ordered by creation date in the specified order.
   *
   * @param page  The page number to retrieve.
   * @param order The order in which to retrieve the projects ("ASC" or "DESC").
   * @return A page of non-confirmed projects ordered by creation date in the specified
   * order.
   */

  @Override
  public Page<Project> getAllNonConfirmedProjectsOrderByDate(final int page,
                                                             final String order) {
    switch (order) {
      case "DESC":
        return projectRepository.findAllByConfirmationStateOrderByCreatedAtDesc(
          ProjectConfirmationState.WAITING.name(), PageRequest.of(page, PAGE_SIZE));
      case "ASC":
        return projectRepository.findAllByConfirmationStateOrderByCreatedAtAsc(
          ProjectConfirmationState.WAITING.name(), PageRequest.of(page, PAGE_SIZE));
      default:
        return projectRepository.findAllByConfirmationState(
          ProjectConfirmationState.WAITING.name(), PageRequest.of(page, PAGE_SIZE));
    }
  }

  /**
   * Confirms the specified list of projects.
   *
   * @param projectIds The IDs of the projects to confirm.
   * @return A response entity with a message indicating that the projects have been confirmed.
   * @throws ProjectNotFoundException If any of the specified projects do not exist.
   */
  @Override
  public ResponseEntity<String> confirmProjects(final List<Long> projectIds) throws ProjectNotFoundException {
    for (Long id : projectIds) {
      confirmProject(id);
    }
    return ResponseEntity.ok().body("Vos projets sont confirmés");
  }

  /**
   * Searches for non-confirmed projects by keyword.
   *
   * @param keyword The keyword to search for.
   * @param page    The page number to retrieve.
   * @return A page of non-confirmed projects that match the specified keyword.
   */
  @Override
  public Page<Project> findNotConfirmedProjectByKeyword(final String keyword,
                                                        final int page) {
    return projectRepository.findByNameOrDescriptionContainingIgnoreCase(
      keyword, PageRequest.of(page, PAGE_SIZE));
  }
}
