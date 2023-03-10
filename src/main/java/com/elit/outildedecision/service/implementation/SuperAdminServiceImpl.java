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
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  private static final int PAGE_SIZE = 10;

  public SuperAdminServiceImpl(final ProjectRepository projectRepository,
                               final UserRepository userRepository) {
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  @Override
  public ResponseEntity confirmProject(final Long projectId) throws ProjectNotFoundException {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas!"));
    project.setConfirmationState(ProjectConfirmationState.CONFIRMED.name());
    projectRepository.save(project);
    return ResponseEntity.ok("Votre projet a été confirmé");
  }

  @Override
  public ResponseEntity suspendUser(final Long userId) throws UserNotFoundException {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas!"));
    user.setIsUserActive(false);
    userRepository.save(user);
    return ResponseEntity.ok("L'utilisateur a été suspendu");
  }

  @Override
  public ResponseEntity deleteProject(final Long projectId) throws ProjectNotFoundException {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas!"));
    projectRepository.delete(project);
    return ResponseEntity.ok("Le projet a été supprimé");
  }

  @Override
  public List<Project> getAllNonConfirmedProjects() {
    return projectRepository.findAllByConfirmationState(ProjectConfirmationState.WAITING.name());
  }

  @Override
  public Page<Project> getAllNonConfirmedProjects(final int page) {
    return projectRepository.findAllByConfirmationState(
        ProjectConfirmationState.WAITING.name(), PageRequest.of(page, PAGE_SIZE));
  }

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

  @Override
  public ResponseEntity confirmProjects(final List<Long> projectIds) throws ProjectNotFoundException {
    for (Long id : projectIds) {
      confirmProject(id);
    }
    return ResponseEntity.ok().body("Vos projets sont confirmés");
  }

  @Override
  public Page<Project> findNotConfirmedProjectByKeyword(final String keyword,
                                                        final int page) {
    return projectRepository.findByNameOrDescriptionContainingIgnoreCase(
            keyword, PageRequest.of(page, PAGE_SIZE));
  }
}
